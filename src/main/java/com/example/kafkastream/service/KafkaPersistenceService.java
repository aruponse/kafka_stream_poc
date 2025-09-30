package com.example.kafkastream.service;

import com.example.kafkastream.model.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Listener Service that consumes from output topics and persists results to H2 database
 * This service closes the loop by consuming the processed events from Kafka Streams
 * and storing them in the database for later retrieval via REST API
 */
@Service
public class KafkaPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPersistenceService.class);

    private final ProcessedEventService processedEventService;

    @Value("${app.kafka.topics.output-topic-transformed}")
    private String outputTopicTransformed;

    @Value("${app.kafka.topics.output-topic-json-converted}")
    private String outputTopicJsonConverted;

    @Value("${app.kafka.topics.output-topic-action-a}")
    private String outputTopicActionA;

    @Value("${app.kafka.topics.output-topic-action-b}")
    private String outputTopicActionB;

    @Autowired
    public KafkaPersistenceService(ProcessedEventService processedEventService) {
        this.processedEventService = processedEventService;
    }

    /**
     * Consume from output-topic-transformed (Use Case 1: Content Transformation)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-transformed}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransformedEvents(@Payload String message,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received transformed event - Topic: {}, Key: {}, Message: {}", topic, key, message);
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "SIMPLE_EVENT_TRANSFORMED",
                key,
                message,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted transformed event with ID: {}", processedEvent.getId());
            
        } catch (Exception e) {
            logger.error("Error processing transformed event from topic: {}, message: {}", topic, message, e);
        }
    }

    /**
     * Consume from output-topic-json-converted (Use Case 2: JSON Schema Conversion)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-json-converted}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJsonConvertedEvents(@Payload String message,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received JSON converted event - Topic: {}, Key: {}, Message: {}", topic, key, message);
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "LEGACY_EVENT_CONVERTED",
                key,
                message,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted JSON converted event with ID: {}", processedEvent.getId());
            
        } catch (Exception e) {
            logger.error("Error processing JSON converted event from topic: {}, message: {}", topic, message, e);
        }
    }

    /**
     * Consume from output-topic-action-a (Use Case 3: Action Type A)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-action-a}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeActionAEvents(@Payload String message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received Action A event - Topic: {}, Key: {}, Message: {}", topic, key, message);
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "GENERIC_ACTION_TYPE_A",
                key,
                message,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted Action A event with ID: {}", processedEvent.getId());
            
        } catch (Exception e) {
            logger.error("Error processing Action A event from topic: {}, message: {}", topic, message, e);
        }
    }

    /**
     * Consume from output-topic-action-b (Use Case 3: Action Type B)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-action-b}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeActionBEvents(@Payload String message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received Action B event - Topic: {}, Key: {}, Message: {}", topic, key, message);
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "GENERIC_ACTION_TYPE_B",
                key,
                message,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted Action B event with ID: {}", processedEvent.getId());
            
        } catch (Exception e) {
            logger.error("Error processing Action B event from topic: {}, message: {}", topic, message, e);
        }
    }
}