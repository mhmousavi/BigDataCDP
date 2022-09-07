import os

import kafka

consumer = kafka.KafkaConsumer(bootstrap_servers=os.getenv("BOOTSRAP_SERVER").split(","))

consumer.subscribe(list(consumer.topics()))

while True:
    msg = next(consumer)
    print(msg.topic)
    val = msg.value.decode("utf-8")
    print(val)
