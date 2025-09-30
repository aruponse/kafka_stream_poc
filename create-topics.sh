#!/bin/bash

# Script to create Kafka topics for Kafka Streams PoC using Docker container
# Usage: ./create-topics.sh

CONTAINER_NAME="kafka-broker"
BOOTSTRAP_SERVER="localhost:9092"

echo "Creating Kafka topics for Kafka Streams PoC..."
echo "Using Docker container: $CONTAINER_NAME"
echo "Bootstrap Server: $BOOTSTRAP_SERVER"
echo ""

# Function to create topic using Docker exec
create_topic() {
    local topic_name=$1
    echo "Creating topic: $topic_name"
    docker exec $CONTAINER_NAME kafka-topics --create \
        --topic $topic_name \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --partitions 3 \
        --replication-factor 1 \
        --if-not-exists
    
    if [ $? -eq 0 ]; then
        echo "✅ Topic '$topic_name' created successfully"
    else
        echo "❌ Failed to create topic '$topic_name'"
    fi
    echo ""
}

# Create all required topics
create_topic "input-topic"
create_topic "actions-topic"
create_topic "output-topic-transformed"
create_topic "output-topic-json-converted"
create_topic "output-topic-action-a"
create_topic "output-topic-action-b"
create_topic "inbound-message-topic"
create_topic "create-chat-topic"
create_topic "create-message-topic"

echo "========================================="
echo "Listing all topics:"
echo "========================================="
docker exec $CONTAINER_NAME kafka-topics --list --bootstrap-server $BOOTSTRAP_SERVER

echo ""
echo "========================================="
echo "Topic Details:"
echo "========================================="
for topic in "inbound-message-topic" "create-chat-topic" "create-message-topic"; do
    echo "Topic: $topic"
    docker exec $CONTAINER_NAME kafka-topics --describe --topic $topic --bootstrap-server $BOOTSTRAP_SERVER 2>/dev/null || echo "❌ Topic '$topic' not found"
    echo ""
done

echo "========================================="
echo "✅ Topics created successfully!"
echo "You can now test the new inbound message event functionality."
echo "========================================="