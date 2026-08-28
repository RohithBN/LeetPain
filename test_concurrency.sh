#!/bin/bash

# Endpoint URL
URL="http://localhost:9000/problem/1/submit"

# Payload to send
PAYLOAD='{
  "language": "java",
  "solution": "public class Main { public static void main(String[] args) throws Exception { Thread.sleep(3000); System.out.println(\"ok\"); } }"
}'

echo "=================================================="
echo " Sending 10 concurrent requests..."
echo " Watch your Spring Boot application terminal logs!"
echo "=================================================="

# Send 10 requests in parallel (-P 10)
seq 1 10 | xargs -n 1 -P 10 -I {} curl -s -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d "$PAYLOAD"

echo -e "\n\nAll 10 requests dispatched to Spring Boot (HTTP 202 Queue)."