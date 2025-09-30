#!/bin/bash

# Script to create Kafka topics for Kafka Streams PoC
# Usage: ./create-topics.sh [KAFKA_HOME]

KAFKA_HOME=${1:-"/usr/local/kafka"}
BOOTSTRAP_SERVER="localhost:9092"

echo "Creating Kafka topics for Kafka Streams PoC..."
echo "Kafka Home: $KAFKA_HOME"
echo "Bootstrap Server: $BOOTSTRAP_SERVER"
echo ""

# Function to create topic
create_topic() {
    local topic_name=$1
    echo "Creating topic: $topic_name"
    $KAFKA_HOME/bin/kafka-topics.sh --create \
        --topic $topic_name \
        --bootstrap-server $BOOTSTRAP_SERVER \
        --partitions 3 \
        --replication-factor 1 \
        --if-not-exists
}

# Create all required topics
create_topic "input-topic"
create_topic "actions-topic"
create_topic "output-topic-transformed"
create_topic "output-topic-json-converted"
create_topic "output-topic-action-a"
create_topic "output-topic-action-b"

echo ""
echo "Listing all topics:"
$KAFKA_HOME/bin/kafka-topics.sh --list --bootstrap-server $BOOTSTRAP_SERVER

echo ""
echo "Topics created successfully!"
echo "You can now start the Spring Boot application with: mvn spring-boot:run"