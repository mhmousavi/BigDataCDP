import kafka

consumer = kafka.KafkaConsumer(bootstrap_servers="kafka:9092")

consumer.subscribe(list(consumer.topics()))

while True:
    msg = next(consumer)
    print(msg.topic)
    val = msg.value.decode("utf-8")
    print(val)
