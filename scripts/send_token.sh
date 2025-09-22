#!/bin/bash

URL=$1
EMAIL=$2
TOKEN=$3
#
#echo '{"email":"'$EMAIL'"}' \
#  | gzip -c \
#  | curl -X POST ${URL}/login/generate \
#      -H "Content-Type: application/json" \
#      -H "Content-Encoding: gzip" \
#      --data-binary @- \
#  | gunzip
#
#echo '{"test":"value"}' \
#  | gzip -c \
#  | curl -X POST ${URL}/echo/test \
#      -H "Content-Type: application/json" \
#      -H "Cookie: <attached automatically>" \
#      -H "Authorization: Bearer <result from login>" \
#      -H "Content-Encoding: gzip" \
#      --data-binary @- \
#  | gunzip


echo '{"email":"'$EMAIL'", "token":"'$TOKEN'"}' \
  | gzip -c \
  | curl -v -X POST ${URL}/login/perform \
      -H "Content-Type: application/json" \
      -H "Content-Encoding: gzip" \
      --data-binary @- \
  | gunzip