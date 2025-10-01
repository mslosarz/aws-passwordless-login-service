#!/bin/bash

URL=$1
SESSION_ID=$2
JWT_TOKEN=$3

echo '{"test":"value"}' \
  | gzip -c \
  | curl -X POST ${URL}/echo/test \
      -H "Content-Type: application/json" \
      -H "Cookie: sessionId=${SESSION_ID}" \
      -H "Authorization: Bearer ${JWT_TOKEN}" \
      -H "Content-Encoding: gzip" \
      --data-binary @- \
  | gunzip


