import os

import kafka

from main import read_kafka_df, current_active_sessions

consumer = kafka.KafkaConsumer(bootstrap_servers=os.getenv("BOOTSRAP_SERVER").split(","))
for topic in consumer.topics():
    df = read_kafka_df(topic)
    current_active_sessions(df, topic)
