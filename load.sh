#!/bin/bash

# Loader AWS Lambda 

aws lambda create-function --debug \
  --function-name example \
  --handler  aws-lambda-example.core \
  --runtime java8 \
  --memory 256 \
  --timeout 59 \
  --role arn:aws:iam::611066707117:role/lambda_exec_role \
  --zip-file fileb://./target/aws-lambda-example-0.1.0-SNAPSHOT-standalone.jar
