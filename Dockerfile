FROM python:3

COPY ./requierments.txt /requirements.txt
RUN pip install -r /requirements.txt
