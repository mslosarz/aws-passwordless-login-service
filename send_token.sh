#!/bin/bash

URL=$1
EMAIL=$2

echo '{"email":"'$EMAIL'"}' \
  | gzip -c \
  | curl -X POST ${URL}/login/generate \
      -H "Content-Type: application/json" \
      -H "Content-Encoding: gzip" \
      --data-binary @- \
  | gunzip

echo '{"test":"value"}' \
  | gzip -c \
  | curl -X POST ${URL}/echo/test \
      -H "Content-Type: application/json" \
      -H "Cookie: test" \
      -H "Content-Encoding: gzip" \
      --data-binary @- \
  | gunzip