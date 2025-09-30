#!/bin/bash

# Test script for Kafka Streams PoC API endpoints
# Usage: ./test-api.sh

BASE_URL="http://localhost:8081/api/events"

echo "==================================="
echo "Kafka Streams PoC - API Testing"
echo "==================================="
echo ""

# Function to make POST requests
post_request() {
    local endpoint=$1
    local data_file=$2
    local description=$3
    
    echo "[$description]"
    echo "POST $BASE_URL$endpoint"
    echo "Data: $(cat $data_file)"
    
    curl -X POST "$BASE_URL$endpoint" \
         -H "Content-Type: application/json" \
         -d @"$data_file" \
         -w "\nHTTP Status: %{http_code}\n" \
         -s | jq '.'
    
    echo ""
    echo "Waiting 2 seconds for processing..."
    sleep 2
    echo ""
}

# Function to make GET requests
get_request() {
    local endpoint=$1
    local description=$2
    
    echo "[$description]"
    echo "GET $BASE_URL$endpoint"
    
    curl -X GET "$BASE_URL$endpoint" \
         -H "Content-Type: application/json" \
         -w "\nHTTP Status: %{http_code}\n" \
         -s | jq '.'
    
    echo ""
}

# Check if server is running
echo "Checking if server is running..."
if ! curl -s "$BASE_URL/processed" > /dev/null; then
    echo "❌ Server is not running at $BASE_URL"
    echo "Please start the application with: mvn spring-boot:run"
    exit 1
fi
echo "✅ Server is running!"
echo ""

# Test Case 1: SimpleEvent (Content Transformation)
post_request "/simple/typed" "examples/simple-event.json" "Use Case 1: SimpleEvent Transformation"

# Test Case 2: LegacyEvent (JSON Schema Conversion)
post_request "/legacy" "examples/legacy-event.json" "Use Case 2: LegacyEvent Conversion"

# Test Case 3A: GenericAction Type A (Routing)
post_request "/action" "examples/action-a.json" "Use Case 3A: GenericAction Type A"

# Test Case 3B: GenericAction Type B (Routing)
post_request "/action" "examples/action-b.json" "Use Case 3B: GenericAction Type B"

# Wait for processing
echo "Waiting 5 seconds for all events to be processed..."
sleep 5
echo ""

# Get processed events
get_request "/processed" "Query All Processed Events"

# Get statistics
get_request "/processed/stats" "Get Processing Statistics"

# Test filtering
get_request "/processed?eventType=SIMPLE_EVENT_TRANSFORMED" "Filter by SimpleEvent Transformed"

get_request "/processed?eventType=LEGACY_EVENT_CONVERTED" "Filter by LegacyEvent Converted"

echo "==================================="
echo "API Testing Complete!"
echo "==================================="
echo ""
echo "You can also access the H2 Console at:"
echo "http://localhost:8081/h2-console"
echo "JDBC URL: jdbc:h2:mem:testdb"
echo "Username: sa"
echo "Password: (empty)"