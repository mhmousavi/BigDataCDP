import os

import kafka

from main import read_kafka_df, clicks_per_hour

consumer = kafka.KafkaConsumer(bootstrap_servers=os.getenv("BOOTSRAP_SERVER").split(","))
# for topic in consumer.topics():
df = read_kafka_df('company1')
clicks_per_hour(df, 'company1')
