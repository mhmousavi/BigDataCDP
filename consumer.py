import kafka

consumer = kafka.KafkaConsumer(bootstrap_servers=["37.32.25.242:9091", "37.32.25.242:9092", "37.32.25.242:9093"])

consumer.subscribe(list(consumer.topics()))

while True:
    msg = next(consumer)
    print(msg.topic)
    val = msg.value.decode("utf-8")
    print(val)
