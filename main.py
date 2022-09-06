from datetime import datetime
from pyspark.sql import DataFrame, SparkSession
from pyspark.sql import functions as F
from pyspark.sql import types as T
from uuid import uuid4
import psycopg2

spark = SparkSession.builder.getOrCreate()

spark.sparkContext.setLogLevel("ERROR")

application = uuid4()


def read_kafka_df():
    df = (
        spark.readStream.format("kafka")
        .option("kafka.bootstrap.servers", "localhost:9092")
        .option("subscribe", "coursera")
        .load()
    )

    df = df.selectExpr("topic", "CAST(key AS STRING)", "CAST(value AS STRING)")
    schema = T.StructType(
        fields=[
            T.StructField("key", T.StringType()),
            T.StructField("ts", T.TimestampType()),
            T.StructField("event", T.StringType()),
            T.StructField("json", T.StringType()),
        ]
    )

    df = df.withColumn("value_json", F.from_json("value", schema))
    df = df.select("value_json.*")

    return df


def upsert_metric_postgres(metric, batch_num: int):
    now = datetime.utcnow()
    sql = f"INSERT INTO active_sessions (ts, active_sessions) VALUES (TIMESTAMP '{now.strftime('%Y-%m-%d %H:%M:%S')}', {metric.count()})"
    with psycopg2.connect("host=localhost port=5432 user=metrics password=metrics dbname=metrics") as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql)


def upsert_metric_cassandra(metric, batch_num: int):
    from cassandra.cluster import Cluster

    now = int(datetime.utcnow().timestamp())

    cluster = Cluster(["127.0.0.1"], port=9042)
    session = cluster.connect("metrics")
    session.execute(
        f"INSERT INTO active_sessions (application, ts, active_sessions) VALUES ({application}, {now}, {metric.count()})"
    )


def upsert_metric(metric, batch_num: int):
    upsert_metric_cassandra(metric, batch_num)
    upsert_metric_postgres(metric, batch_num)


def clicks_per_hour(df: DataFrame):
    # TODO : filter by click_
    # TODO: set 10 second to 1 hour
    df = df.withWatermark("ts", "10 seconds")
    df = df.groupBy(F.window(F.col("ts"), "10 seconds"), "event").count()
    query = df.writeStream.outputMode("update").foreachBatch(upsert_metric).start()
    # query = df.writeStream.outputMode("update").format("console").start()
    query.awaitTermination()


def current_active_sessions(df: DataFrame):
    df = df.withWatermark("ts", "2 seconds")
    df = df.groupBy(
        F.session_window(F.col("ts"), "2 seconds"),
        F.get_json_object("json", "$.session_id").alias("session_id"),
    ).count()
    query = df.writeStream.outputMode("append").foreachBatch(upsert_metric).start()
    # query = df.writeStream.outputMode("append").format("console").option("truncate", False).start()
    query.awaitTermination()


if __name__ == "__main__":
    df = read_kafka_df()
    current_active_sessions(df)
