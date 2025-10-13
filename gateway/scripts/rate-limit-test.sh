#!/bin/bash

URL="http://localhost:8080/api/users/test"
API_KEY="clientId1"

REQUESTS_PER_SECOND=3      # Number of requests to send per second
DURATION_SECONDS=90        # Total test duration (1.5 minutes)
TOTAL_REQUESTS=$((REQUESTS_PER_SECOND * DURATION_SECONDS))

echo "==============================================================="
echo "BURSTY RATE LIMIT TEST - $REQUESTS_PER_SECOND req/sec for $DURATION_SECONDS seconds"
echo "==============================================================="
echo "URL: $URL"
echo "API Key: $API_KEY"
echo "Total Requests: $TOTAL_REQUESTS"
echo ""

success_count=0
rate_limit_count=0
other_count=0

counter=1
start_time=$(date +%s)

while true; do
    current_time=$(date +%s)
    elapsed=$((current_time - start_time))
    if [ "$elapsed" -ge "$DURATION_SECONDS" ]; then
        break
    fi

    # Send burst of requests in parallel
    for i in $(seq 1 $REQUESTS_PER_SECOND); do
        (
            response=$(curl -s -w "|%{http_code}" -H "X-API-Key: $API_KEY" "$URL")
            status=$(echo "$response" | cut -d'|' -f2)
            timestamp=$(date +"%H:%M:%S.%3N")

            if [ "$status" -eq 429 ]; then
                echo -e "$timestamp - Request $counter: \e[31mHTTP 429 ❌ RATE LIMITED\e[0m"
                ((rate_limit_count++))
            elif [ "$status" -eq 200 ]; then
                echo -e "$timestamp - Request $counter: \e[32mHTTP 200 ✅ ALLOWED\e[0m"
                ((success_count++))
            else
                echo -e "$timestamp - Request $counter: \e[33mHTTP $status ⚠️ OTHER\e[0m"
                ((other_count++))
            fi
        ) &
        counter=$((counter + 1))
    done

    wait  # Wait for this batch to finish
    sleep 1  # Wait 1 second before next burst
done

echo ""
echo "==============================================================="
echo "FINAL SUMMARY"
echo "==============================================================="
echo -e "✅ ALLOWED (200):    \e[32m$success_count\e[0m"
echo -e "❌ RATE LIMITED (429): \e[31m$rate_limit_count\e[0m"
echo -e "⚠️ OTHER:            \e[33m$other_count\e[0m"
echo "==============================================================="



#!/bin/bash

# URL="http://localhost:8082/leave-attendance/api/v1/test"

# echo "ULTIMATE RATE LIMIT TEST - Should see HTTP 429"
# echo "=============================================="
# echo "Using API Key: clientId1"
# echo ""

# # Phase 1: Burst requests to exceed limit
# echo "PHASE 1: Sending 60 rapid requests (exceeds 40 burst capacity)..."
# echo ""

# success_count=0
# rate_limit_count=0

# for i in {1..60}; do
#     response=$(curl -s -w "|%{http_code}" --header "X-API-Key: clientId1" "$URL")
#     status=$(echo "$response" | cut -d'|' -f2)

#     timestamp=$(date +"%H:%M:%S.%3N")

#     if [ "$status" -eq 429 ]; then
#         echo -e "$timestamp - Request $i: \e[31mHTTP 429 ❌ RATE LIMITED\e[0m"
#         ((rate_limit_count++))
#     elif [ "$status" -eq 200 ]; then
#         echo -e "$timestamp - Request $i: \e[32mHTTP 200 ✅ ALLOWED\e[0m"
#         ((success_count++))
#     else
#         echo -e "$timestamp - Request $i: \e[33mHTTP $status ⚠️ OTHER\e[0m"
#     fi
# done

# echo ""
# echo "=============================================="
# echo -e "PHASE 1 RESULTS:"
# echo -e "  ✅ ALLOWED: $success_count requests"
# echo -e "  ❌ RATE LIMITED: $rate_limit_count requests"
# echo ""

# # Phase 2: Immediate follow-up test
# echo "PHASE 2: Immediate follow-up (should see more 429s)..."
# echo ""

# for i in {61..70}; do
#     status=$(curl -s -o /dev/null -w "%{http_code}" --header "X-API-Key: clientId1" "$URL")
#     timestamp=$(date +"%H:%M:%S.%3N")

#     if [ "$status" -eq 429 ]; then
#         echo -e "$timestamp - Request $i: \e[31mHTTP 429 ❌ RATE LIMITED\e[0m"
#     else
#         echo -e "$timestamp - Request $i: \e[32mHTTP 200 ✅ ALLOWED\e[0m"
#     fi
# done

# echo ""
# echo "=============================================="
# echo "EXPECTED BEHAVIOR:"
# echo "- First ~40 requests: ✅ ALLOWED"
# echo "- Remaining requests: ❌ RATE LIMITED"
# echo ""
# echo "If you don't see 429 responses, check:"
# echo "1. Redis is running: docker run -d -p 6379:6379 redis"
# echo "2. Spring Boot logs for Redis connection errors"
# echo "3. All dependencies are included"