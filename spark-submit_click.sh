#!/bin/bash

#spark-submit --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 main.py
spark-submit --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 main_click.py
#spark-submit --master local --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 main.py
