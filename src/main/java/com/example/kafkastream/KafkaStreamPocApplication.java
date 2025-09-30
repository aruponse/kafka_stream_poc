package com.example.kafkastream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Main Spring Boot Application class for Kafka Streams PoC
 * 
 * This application demonstrates three main use cases with Kafka Streams:
 * 1. Content Transformation: SimpleEvent payload transformation
 * 2. JSON Schema Conversion: LegacyEvent to NewFormatEvent conversion
 * 3. Routing and Division: GenericAction routing based on actionType
 * 
 * Features:
 * - Kafka Streams processing with custom topology
 * - H2 in-memory database for persistence
 * - REST API endpoints for event publishing and querying
 * - Automatic consumption and persistence of processed events
 * 
 * API Endpoints:
 * - POST /api/events/simple - Publish SimpleEvent or LegacyEvent
 * - POST /api/events/simple/typed - Publish typed SimpleEvent
 * - POST /api/events/legacy - Publish LegacyEvent  
 * - POST /api/events/action - Publish GenericAction
 * - GET /api/events/processed - Query all processed events
 * - GET /api/events/processed/stats - Get processing statistics
 * - DELETE /api/events/processed - Clear all processed events
 * 
 * H2 Console: http://localhost:8081/h2-console
 * JDBC URL: jdbc:h2:mem:testdb
 * Username: sa
 * Password: (empty)
 */
@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamPocApplication.class, args);
    }
}