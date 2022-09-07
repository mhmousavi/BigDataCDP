import os
import random
import time
from datetime import datetime
from uuid import uuid4
import json

from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers=os.getenv("BOOTSRAP_SERVER").split(","))

i = 0
while True:
    i += 1
    events = ['click_course', 'click_video']
    data = {
        "key": str(uuid4()),
        "event": random.choice(events),
        "ts": int(datetime.utcnow().timestamp()),
        "json": {"sessionId": "xxx" if i % 2 == 0 else "yyy"},
    }
    producer.send(topic="coursera", value=json.dumps(data).encode("utf-8"))
    producer.flush(timeout=1)
    time.sleep(1)

producer.close()
