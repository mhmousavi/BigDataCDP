#!/bin/bash

./venv/lib/python3.9/site-packages/pyspark/bin/spark-submit --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 main.py
