#!/bin/bash

spark-submit --num-executors 4 --executor-cores 2 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 clicks.py
