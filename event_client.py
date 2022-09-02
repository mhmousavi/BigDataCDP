import argparse
import json
import random
import time
from datetime import datetime
from uuid import uuid4

from kafka import KafkaProducer

parser = argparse.ArgumentParser()
parser.add_argument('-f', dest='file', type=str, help='file address')
parser.add_argument('-s', dest='start', type=str, help='start time')
parser.add_argument('-e', dest='end', type=str, help='end time')
parser.add_argument('-ss', dest='simulated_start', type=str, help='simulated start time')
parser.add_argument('-se', dest='simulated_end', type=str, help='simulated end time')
args = parser.parse_args()

# file_address = args.file
# start_time = args.start
# end_time = args.end
# simulated_start_time = args.simulated_start
# simulated_end_time = args.simulated_end

file_address = 'data/20150801-20160801-activity/20150801-20151101-raw_user_activity.json'
start_time = '2015-08-01T00:00:00'
end_time = '2015-11-01T23:59:59'
simulated_start_time = '2022-09-02T22:00:00'
simulated_end_time = '2022-09-02T23:00:00'

start_time_timestamp = int(datetime.strptime(start_time, '%Y-%m-%dT%H:%M:%S').timestamp())
end_time_timestamp = int(datetime.strptime(end_time, '%Y-%m-%dT%H:%M:%S').timestamp())
simulated_start_time_timestamp = int(datetime.strptime(simulated_start_time, '%Y-%m-%dT%H:%M:%S').timestamp())
simulated_end_time_timestamp = int(datetime.strptime(simulated_end_time, '%Y-%m-%dT%H:%M:%S').timestamp())


def simulated_time(timestamp):
    percent = (timestamp - start_time_timestamp) / (end_time_timestamp - start_time_timestamp)
    duration = int(percent * (simulated_end_time_timestamp - simulated_start_time_timestamp))
    return duration + simulated_start_time_timestamp

print('begin to read')
events = []
with open(file_address, 'r') as file:
    data = json.load(file)
    print('load end')
    for course_row in random.choices(data, k=20):
        course_id = course_row[0]
        print(course_id)
        for user_id, session in course_row[1].items():
            for session_id, activity_list in session.items():
                for activity in activity_list:
                    ts = simulated_time(int(datetime.strptime(activity[1], '%Y-%m-%dT%H:%M:%S').timestamp()))
                    event = {
                        'key': str(uuid4()),
                        'event': activity[0],
                        'ts': ts,
                        'json': {
                            'course_id': course_id,
                            'user_id': user_id,
                            'session_id': session_id,
                        }
                    }
                    events.append(event)

print('begin to sort')
events = random.choices(events, k=10000)
events = sorted(events, key=lambda e: e['ts'])

with open('data/clean.json', 'w') as file:
    file.write(json.dumps(events, indent=4))

event_id = 0
now = datetime.utcnow().timestamp()
while event_id < len(events) and events[event_id]['ts'] < now:
    event_id += 1

producer = KafkaProducer(bootstrap_servers="kafka:9092")
while event_id < len(events):
    now = datetime.utcnow().timestamp()
    while event_id < len(events) and events[event_id]['ts'] < now:
        producer.send(topic="coursera", value=json.dumps(event).encode("utf-8"))
        producer.flush(timeout=1)
        event_id += 1
    time.sleep(1)
producer.close()
