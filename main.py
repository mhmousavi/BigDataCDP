from datetime import datetime

import kafka
from pyspark.sql import DataFrame, SparkSession
from pyspark.sql import functions as F
from pyspark.sql import types as T
from uuid import uuid4
import psycopg2

spark = SparkSession.builder.getOrCreate()

spark.sparkContext.setLogLevel("ERROR")


def read_kafka_df(topic):
    df = (
        spark.readStream.format("kafka")
        .option("kafka.bootstrap.servers", "localhost:9092")
        .option("subscribe", topic)
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


# def upsert_metric_postgres(metric, batch_num: int):
#     now = datetime.utcnow()
#     sql = f"INSERT INTO active_sessions (ts, active_sessions) VALUES (TIMESTAMP '{now.strftime('%Y-%m-%d %H:%M:%S')}', {metric.count()})"
#     with psycopg2.connect("host=localhost port=5432 user=metrics password=metrics dbname=metrics") as conn:
#         with conn.cursor() as cursor:
#             cursor.execute(sql)


def upsert_metric_cassandra(metric, batch_num: int):
    from cassandra.cluster import Cluster

    now = int(datetime.utcnow().timestamp())
    application = uuid4()

    cluster = Cluster(["37.32.25.242"], port=9042)
    session = cluster.connect("metrics")
    session.execute(
        f"INSERT INTO active_sessions (application, ts, active_sessions) VALUES ({application}, {now}, {metric.count()})"
    )


def upsert_metric(metric, batch_num: int):
    upsert_metric_cassandra(metric, batch_num)
    # upsert_metric_postgres(metric, batch_num)


def clicks_per_hour(df: DataFrame, topic):
    df = df.filter(F.col("event").startswith("click_"))
    df = df.withWatermark("ts", "10 seconds")
    df = df.groupBy(F.window(F.col("ts"), "1 hour"), "event").count()
    query = df.writeStream.outputMode("update").foreachBatch(upsert_metric).start()
    # query = df.writeStream.outputMode("update").format("console").start()
    query.awaitTermination()


def current_active_sessions(df: DataFrame, topic):
    df = df.withWatermark("ts", "2 seconds")
    df = df.groupBy(
        F.session_window(F.col("ts"), "2 seconds"),
        F.get_json_object("json", "$.session_id").alias("session_id"),
    ).count()
    # query = df.writeStream.outputMode("append").foreachBatch(upsert_metric).start()
    query = df.writeStream.outputMode("append").format("console").option("truncate", False).start()
    query.awaitTermination()


if __name__ == "__main__":
    consumer = kafka.KafkaConsumer(bootstrap_servers=["37.32.25.242:9091", "37.32.25.242:9092", "37.32.25.242:9093"])
    for topic in consumer.topics():
        df = read_kafka_df(topic)
        clicks_per_hour(df, topic)
        # current_active_sessions(df, topic)
