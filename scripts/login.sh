#!/bin/bash

URL=$1
EMAIL=$2
TOKEN=$3

if [[ -z "$TOKEN" ]]; then
  echo '{"email":"'$EMAIL'"}' \
    | gzip -c \
    | curl -X POST ${URL}/login/generate \
        -H "Content-Type: application/json" \
        -H "Content-Encoding: gzip" \
        --data-binary @- \
    | gunzip
    exit 0
fi

echo '{"email":"'$EMAIL'", "token":"'$TOKEN'"}' \
  | gzip -c \
  | curl -v -X POST ${URL}/login/perform \
      -H "Content-Type: application/json" \
      -H "Content-Encoding: gzip" \
      --data-binary @- \
  | gunzip

