package com.example.kafkastream.controller;

import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.LegacyEvent;
import com.example.kafkastream.dto.SimpleEvent;
import com.example.kafkastream.model.ProcessedEvent;
import com.example.kafkastream.service.ProcessedEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Kafka Streams PoC
 * Provides endpoints to:
 * 1. Publish events to Kafka topics (input-topic and actions-topic)
 * 2. Query processed events from H2 database
 */
@RestController
@RequestMapping("/api/events")
@Validated
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProcessedEventService processedEventService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.topics.actions-topic}")
    private String actionsTopic;

    @Autowired
    public EventController(KafkaTemplate<String, String> kafkaTemplate,
                          ProcessedEventService processedEventService,
                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.processedEventService = processedEventService;
        this.objectMapper = objectMapper;
    }

    /**
     * POST /api/events/simple
     * Accepts SimpleEvent or LegacyEvent and publishes to input-topic for Use Cases 1 and 2
     */
    @PostMapping("/simple")
    public ResponseEntity<Map<String, Object>> publishSimpleEvent(@RequestBody Map<String, Object> eventData) {
        try {
            String key = UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(eventData);
            
            logger.info("Publishing event to {}: key={}, message={}", inputTopic, key, jsonMessage);
            
            kafkaTemplate.send(inputTopic, key, jsonMessage);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Event published successfully to " + inputTopic,
                "key", key,
                "topic", inputTopic,
                "data", eventData
            ));
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing event data", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "error",
                "message", "Invalid JSON format: " + e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error publishing simple event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish event: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/events/simple/typed
     * Typed endpoint for SimpleEvent - Use Case 1
     */
    @PostMapping("/simple/typed")
    public ResponseEntity<Map<String, Object>> publishTypedSimpleEvent(@RequestBody SimpleEvent simpleEvent) {
        try {
            String key = simpleEvent.getId() != null ? simpleEvent.getId() : UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(simpleEvent);
            
            logger.info("Publishing SimpleEvent to {}: key={}, message={}", inputTopic, key, jsonMessage);
            
            kafkaTemplate.send(inputTopic, key, jsonMessage);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "SimpleEvent published successfully to " + inputTopic,
                "key", key,
                "topic", inputTopic,
                "eventType", "SimpleEvent",
                "data", simpleEvent
            ));
            
        } catch (Exception e) {
            logger.error("Error publishing typed simple event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish SimpleEvent: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/events/legacy
     * Typed endpoint for LegacyEvent - Use Case 2
     */
    @PostMapping("/legacy")
    public ResponseEntity<Map<String, Object>> publishLegacyEvent(@RequestBody LegacyEvent legacyEvent) {
        try {
            String key = UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(legacyEvent);
            
            logger.info("Publishing LegacyEvent to {}: key={}, message={}", inputTopic, key, jsonMessage);
            
            kafkaTemplate.send(inputTopic, key, jsonMessage);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "LegacyEvent published successfully to " + inputTopic,
                "key", key,
                "topic", inputTopic,
                "eventType", "LegacyEvent",
                "data", legacyEvent
            ));
            
        } catch (Exception e) {
            logger.error("Error publishing legacy event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish LegacyEvent: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/events/action
     * Accepts GenericAction and publishes to actions-topic for Use Case 3
     */
    @PostMapping("/action")
    public ResponseEntity<Map<String, Object>> publishActionEvent(@RequestBody GenericAction genericAction) {
        try {
            String key = UUID.randomUUID().toString();
            String jsonMessage = objectMapper.writeValueAsString(genericAction);
            
            logger.info("Publishing GenericAction to {}: key={}, message={}", actionsTopic, key, jsonMessage);
            
            kafkaTemplate.send(actionsTopic, key, jsonMessage);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "GenericAction published successfully to " + actionsTopic,
                "key", key,
                "topic", actionsTopic,
                "actionType", genericAction.getActionType(),
                "data", genericAction
            ));
            
        } catch (Exception e) {
            logger.error("Error publishing action event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish GenericAction: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/events/processed
     * Retrieves all processed events from H2 database
     */
    @GetMapping("/processed")
    public ResponseEntity<Map<String, Object>> getProcessedEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String sourceTopic) {
        try {
            List<ProcessedEvent> events;
            
            if (eventType != null) {
                events = processedEventService.getProcessedEventsByType(eventType);
            } else if (sourceTopic != null) {
                events = processedEventService.getProcessedEventsBySourceTopic(sourceTopic);
            } else {
                events = processedEventService.getAllProcessedEvents();
            }
            
            logger.info("Retrieved {} processed events", events.size());
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "count", events.size(),
                "events", events,
                "filters", Map.of(
                    "eventType", eventType != null ? eventType : "all",
                    "sourceTopic", sourceTopic != null ? sourceTopic : "all"
                )
            ));
            
        } catch (Exception e) {
            logger.error("Error retrieving processed events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to retrieve processed events: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/events/processed/stats
     * Retrieves statistics about processed events
     */
    @GetMapping("/processed/stats")
    public ResponseEntity<Map<String, Object>> getProcessedEventsStats() {
        try {
            long totalEvents = processedEventService.getAllProcessedEvents().size();
            long transformedEvents = processedEventService.getCountByEventType("SIMPLE_EVENT_TRANSFORMED");
            long convertedEvents = processedEventService.getCountByEventType("LEGACY_EVENT_CONVERTED");
            long actionAEvents = processedEventService.getCountByEventType("GENERIC_ACTION_TYPE_A");
            long actionBEvents = processedEventService.getCountByEventType("GENERIC_ACTION_TYPE_B");
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "totalProcessedEvents", totalEvents,
                "eventTypeBreakdown", Map.of(
                    "SIMPLE_EVENT_TRANSFORMED", transformedEvents,
                    "LEGACY_EVENT_CONVERTED", convertedEvents,
                    "GENERIC_ACTION_TYPE_A", actionAEvents,
                    "GENERIC_ACTION_TYPE_B", actionBEvents
                )
            ));
            
        } catch (Exception e) {
            logger.error("Error retrieving processed events statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to retrieve statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE /api/events/processed
     * Deletes all processed events (useful for testing)
     */
    @DeleteMapping("/processed")
    public ResponseEntity<Map<String, Object>> deleteAllProcessedEvents() {
        try {
            processedEventService.deleteAllProcessedEvents();
            logger.info("All processed events deleted");
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All processed events deleted successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Error deleting processed events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to delete processed events: " + e.getMessage()
            ));
        }
    }
}