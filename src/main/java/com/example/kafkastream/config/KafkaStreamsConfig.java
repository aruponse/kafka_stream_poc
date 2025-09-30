package com.example.kafkastream.config;

import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.LegacyEvent;
import com.example.kafkastream.dto.NewFormatEvent;
import com.example.kafkastream.dto.SimpleEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Kafka Streams Configuration with three main use cases:
 * 1. Content Transformation: Transform message payload
 * 2. JSON Schema Conversion: Convert between different JSON formats
 * 3. Routing and Division: Route messages based on criteria
 */
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsConfig.class);

    @Value("${app.kafka.topics.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.topics.actions-topic}")
    private String actionsTopic;

    @Value("${app.kafka.topics.output-topic-transformed}")
    private String outputTopicTransformed;

    @Value("${app.kafka.topics.output-topic-json-converted}")
    private String outputTopicJsonConverted;

    @Value("${app.kafka.topics.output-topic-action-a}")
    private String outputTopicActionA;

    @Value("${app.kafka.topics.output-topic-action-b}")
    private String outputTopicActionB;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        logger.info("Initializing Kafka Streams topology");

        // =====================================
        // USE CASE 1: Content Transformation
        // =====================================
        KStream<String, String> inputStream = streamsBuilder
                .stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> logger.info("Processing input-topic event: key={}, value={}", key, value));

        // Transform SimpleEvent by adding processed timestamp and modifying payload
        inputStream
                .mapValues(value -> {
                    try {
                        SimpleEvent event = objectMapper.readValue(value, SimpleEvent.class);
                        logger.info("Transforming SimpleEvent: {}", event);
                        
                        // Transform the content
                        event.setPayload("TRANSFORMED: " + event.getPayload());
                        event.setTimestamp(System.currentTimeMillis()); // Update timestamp
                        
                        String transformedJson = objectMapper.writeValueAsString(event);
                        logger.info("Transformed to: {}", transformedJson);
                        return transformedJson;
                    } catch (Exception e) {
                        logger.error("Error transforming SimpleEvent", e);
                        return value; // Return original on error
                    }
                }, Named.as("transform-simple-event"))
                .to(outputTopicTransformed, Produced.with(Serdes.String(), Serdes.String()));

        // =====================================
        // USE CASE 2: JSON Schema Conversion
        // =====================================
        // Convert legacy JSON format to new format
        inputStream
                .mapValues(value -> {
                    try {
                        // Try to parse as LegacyEvent first
                        if (value.contains("old_field_name")) {
                            LegacyEvent legacyEvent = objectMapper.readValue(value, LegacyEvent.class);
                            logger.info("Converting LegacyEvent: {}", legacyEvent);
                            
                            // Convert to new format
                            NewFormatEvent newEvent = new NewFormatEvent();
                            newEvent.setNewFieldName(legacyEvent.getValue());
                            newEvent.setConvertedAt(System.currentTimeMillis());
                            newEvent.setData("legacy-system");
                            
                            String convertedJson = objectMapper.writeValueAsString(newEvent);
                            logger.info("Converted to NewFormatEvent: {}", convertedJson);
                            return convertedJson;
                        }
                        return value; // Return original if not legacy format
                    } catch (Exception e) {
                        logger.error("Error converting legacy event", e);
                        return value;
                    }
                }, Named.as("convert-legacy-event"))
                .to(outputTopicJsonConverted, Produced.with(Serdes.String(), Serdes.String()));

        // =====================================
        // USE CASE 3: Process actions-topic for Routing and Division
        // =====================================
        KStream<String, String> actionsStream = streamsBuilder
                .stream(actionsTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> logger.info("Processing actions-topic event: key={}, value={}", key, value));

        // Split stream based on actionType and route to different topics
        actionsStream
                .filter((key, value) -> {
                    try {
                        // Remove outer quotes if present (Kafka sometimes stores JSON as escaped string)
                        String cleanValue = value.startsWith("\"") && value.endsWith("\"") 
                            ? value.substring(1, value.length() - 1).replace("\\\"", "\"")
                            : value;
                        GenericAction action = objectMapper.readValue(cleanValue, GenericAction.class);
                        return "A".equalsIgnoreCase(action.getActionType());
                    } catch (Exception e) {
                        logger.error("Error parsing GenericAction for type A, key: {}, value: {}", key, value, e);
                        return false;
                    }
                })
                .mapValues((key, value) -> {
                    try {
                        // Remove outer quotes if present
                        String cleanValue = value.startsWith("\"") && value.endsWith("\"") 
                            ? value.substring(1, value.length() - 1).replace("\\\"", "\"")
                            : value;
                        GenericAction action = objectMapper.readValue(cleanValue, GenericAction.class);
                        logger.info("Processing Action Type A: {}", action);
                        
                        // Transform for Action A (e.g., add specific prefix)
                        action.setDetails("ACTION_A_PROCESSED: " + action.getDetails());
                        
                        String processedJson = objectMapper.writeValueAsString(action);
                        logger.info("Processed Action A to: {}", processedJson);
                        return processedJson;
                    } catch (Exception e) {
                        logger.error("Error processing Action A", e);
                        return value;
                    }
                }, Named.as("process-action-a"))
                .to(outputTopicActionA, Produced.with(Serdes.String(), Serdes.String()));

        // Process Action Type B
        actionsStream
                .filter((key, value) -> {
                    try {
                        // Remove outer quotes if present
                        String cleanValue = value.startsWith("\"") && value.endsWith("\"") 
                            ? value.substring(1, value.length() - 1).replace("\\\"", "\"")
                            : value;
                        GenericAction action = objectMapper.readValue(cleanValue, GenericAction.class);
                        return "B".equalsIgnoreCase(action.getActionType());
                    } catch (Exception e) {
                        logger.error("Error parsing GenericAction for type B, key: {}, value: {}", key, value, e);
                        return false;
                    }
                })
                .mapValues((key, value) -> {
                    try {
                        // Remove outer quotes if present
                        String cleanValue = value.startsWith("\"") && value.endsWith("\"") 
                            ? value.substring(1, value.length() - 1).replace("\\\"", "\"")
                            : value;
                        GenericAction action = objectMapper.readValue(cleanValue, GenericAction.class);
                        logger.info("Processing Action Type B: {}", action);
                        
                        // Transform for Action B (e.g., add specific prefix)
                        action.setDetails("ACTION_B_PROCESSED: " + action.getDetails());
                        
                        String processedJson = objectMapper.writeValueAsString(action);
                        logger.info("Processed Action B to: {}", processedJson);
                        return processedJson;
                    } catch (Exception e) {
                        logger.error("Error processing Action B", e);
                        return value;
                    }
                }, Named.as("process-action-b"))
                .to(outputTopicActionB, Produced.with(Serdes.String(), Serdes.String()));

        logger.info("Kafka Streams topology configured successfully");
        return inputStream; // Return the main stream
    }
}