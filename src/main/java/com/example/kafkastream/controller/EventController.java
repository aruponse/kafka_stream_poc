package com.example.kafkastream.controller;

import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.LegacyEvent;
import com.example.kafkastream.dto.SimpleEvent;
import com.example.kafkastream.dto.InboundMessageEvent;
import com.example.kafkastream.model.OriginalEvent;
import com.example.kafkastream.model.ProcessedEvent;
import com.example.kafkastream.service.OriginalEventService;
import com.example.kafkastream.service.ProcessedEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedEventService processedEventService;
    private final OriginalEventService originalEventService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.topics.actions-topic}")
    private String actionsTopic;

    @Value("${app.kafka.topics.inbound-message-topic}")
    private String inboundMessageTopic;

    public EventController(KafkaTemplate<String, Object> kafkaTemplate,
                          ProcessedEventService processedEventService,
                          OriginalEventService originalEventService,
                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.processedEventService = processedEventService;
        this.originalEventService = originalEventService;
        this.objectMapper = objectMapper;
    }

    /**
     * POST /api/events/simple
     * Accepts SimpleEvent as JSON and publishes JSON to input-topic - Use Case 1
     */
    @PostMapping("/simple")
    public ResponseEntity<Map<String, Object>> publishSimpleEvent(@RequestBody Map<String, Object> eventData) {
        try {
            // Create SimpleEvent from JSON data
            SimpleEvent simpleEvent = new SimpleEvent(
                    eventData.get("id") != null ? eventData.get("id").toString() : UUID.randomUUID().toString(),
                    eventData.get("payload") != null ? eventData.get("payload").toString() : "",
                    eventData.get("timestamp") != null ? 
                            Long.valueOf(eventData.get("timestamp").toString()) : System.currentTimeMillis()
            );
            
            String key = simpleEvent.getId();
            
            logger.info("Publishing SimpleEvent (JSON) to {}: key={}, id={}, payload={}", 
                      inputTopic, key, simpleEvent.getId(), simpleEvent.getPayload());
            
            // Save original event for tracking
            String jsonData = objectMapper.writeValueAsString(simpleEvent);
            OriginalEvent originalEvent = new OriginalEvent(key, "SimpleEvent", inputTopic, jsonData);
            originalEventService.saveOriginalEvent(originalEvent);
            
            kafkaTemplate.send(inputTopic, key, simpleEvent);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "SimpleEvent published successfully to " + inputTopic + " using JSON serialization",
                "key", key,
                "topic", inputTopic,
                "eventType", "SimpleEvent",
                "data", Map.of(
                    "id", simpleEvent.getId(),
                    "payload", simpleEvent.getPayload(),
                    "timestamp", simpleEvent.getTimestamp()
                ),
                "originalEventId", originalEvent.getId()
            ));
            
        } catch (Exception e) {
            logger.error("Error publishing simple event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish SimpleEvent: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/events/legacy
     * Accepts LegacyEvent as JSON and publishes JSON to input-topic - Use Case 2
     */
    @PostMapping("/legacy")
    public ResponseEntity<Map<String, Object>> publishLegacyEvent(@RequestBody Map<String, Object> eventData) {
        try {
            // Create LegacyEvent from JSON data
            LegacyEvent legacyEvent = new LegacyEvent(
                    eventData.get("old_field_name") != null ? 
                            eventData.get("old_field_name").toString() : "",
                    eventData.get("value") != null ? eventData.get("value").toString() : ""
            );
            
            String key = UUID.randomUUID().toString();
            
            logger.info("Publishing LegacyEvent (JSON) to {}: key={}, oldFieldName={}, value={}", 
                      inputTopic, key, legacyEvent.getOldFieldName(), legacyEvent.getValue());
            
            // Save original event for tracking
            String jsonData = objectMapper.writeValueAsString(legacyEvent);
            OriginalEvent originalEvent = new OriginalEvent(key, "LegacyEvent", inputTopic, jsonData);
            originalEventService.saveOriginalEvent(originalEvent);
            
            kafkaTemplate.send(inputTopic, key, legacyEvent);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "LegacyEvent published successfully to " + inputTopic + " using JSON serialization",
                "key", key,
                "topic", inputTopic,
                "eventType", "LegacyEvent",
                "data", Map.of(
                    "old_field_name", legacyEvent.getOldFieldName(),
                    "value", legacyEvent.getValue()
                ),
                "originalEventId", originalEvent.getId()
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
     * Accepts GenericAction as JSON and publishes JSON to actions-topic - Use Case 3
     */
    @PostMapping("/action")
    public ResponseEntity<Map<String, Object>> publishActionEvent(@RequestBody Map<String, Object> actionData) {
        try {
            // Create GenericAction from JSON data
            GenericAction genericAction = new GenericAction(
                    actionData.get("actionType") != null ? 
                            actionData.get("actionType").toString() : "",
                    actionData.get("details") != null ? 
                            actionData.get("details").toString() : ""
            );
            
            String key = UUID.randomUUID().toString();
            
            logger.info("Publishing GenericAction (JSON) to {}: key={}, actionType={}, details={}", 
                      actionsTopic, key, genericAction.getActionType(), genericAction.getDetails());
            
            // Save original event for tracking
            String jsonData = objectMapper.writeValueAsString(genericAction);
            OriginalEvent originalEvent = new OriginalEvent(key, "GenericAction", actionsTopic, jsonData);
            originalEventService.saveOriginalEvent(originalEvent);
            
            kafkaTemplate.send(actionsTopic, key, genericAction);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "GenericAction published successfully to " + actionsTopic + " using JSON serialization",
                "key", key,
                "topic", actionsTopic,
                "eventType", "GenericAction",
                "data", Map.of(
                    "actionType", genericAction.getActionType(),
                    "details", genericAction.getDetails()
                ),
                "originalEventId", originalEvent.getId()
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
     * POST /api/events/inbound-message
     * Accepts InboundMessageEvent as JSON and publishes JSON to inbound-message-topic - Use Case 4
     */
    @PostMapping("/inbound-message")
    public ResponseEntity<Map<String, Object>> publishInboundMessageEvent(@RequestBody Map<String, Object> eventData) {
        try {
            logger.info("Received inbound message event data: {}", eventData);
            
            // Extract main event properties
            String app = eventData.get("app") != null ? eventData.get("app").toString() : "TestApp";
            Long timestamp = eventData.get("timestamp") != null ? 
                    Long.valueOf(eventData.get("timestamp").toString()) : System.currentTimeMillis();
            Integer version = eventData.get("version") != null ? 
                    Integer.valueOf(eventData.get("version").toString()) : 2;
            String type = eventData.get("type") != null ? eventData.get("type").toString() : "message";
            
            // Extract payload data
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadData = (Map<String, Object>) eventData.get("payload");
            if (payloadData == null) {
                throw new IllegalArgumentException("Payload is required");
            }
            
            String messageId = payloadData.get("id") != null ? payloadData.get("id").toString() : "";
            String source = payloadData.get("source") != null ? payloadData.get("source").toString() : "";
            String messageType = payloadData.get("type") != null ? payloadData.get("type").toString() : "text";
            
            // Extract nested payload (message content)
            @SuppressWarnings("unchecked")
            Map<String, Object> messageContentData = (Map<String, Object>) payloadData.get("payload");
            String text = "";
            if (messageContentData != null && messageContentData.get("text") != null) {
                text = messageContentData.get("text").toString();
            }
            InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent(text);
            
            // Extract sender data
            @SuppressWarnings("unchecked")
            Map<String, Object> senderData = (Map<String, Object>) payloadData.get("sender");
            InboundMessageEvent.Sender sender = null;
            if (senderData != null) {
                String phone = senderData.get("phone") != null ? senderData.get("phone").toString() : "";
                String name = senderData.get("name") != null ? senderData.get("name").toString() : "";
                String countryCode = senderData.get("country_code") != null ? senderData.get("country_code").toString() : "";
                String dialCode = senderData.get("dial_code") != null ? senderData.get("dial_code").toString() : "";
                sender = new InboundMessageEvent.Sender(phone, name, countryCode, dialCode);
            }
            
            // Create message payload
            InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                    messageId, source, messageType, messageContent, sender);
            
            // Create InboundMessageEvent
            InboundMessageEvent inboundMessageEvent = new InboundMessageEvent(
                    app, timestamp, version, type, messagePayload);
            
            String key = messageId; // Use message ID as key
            
            logger.info("Publishing InboundMessageEvent (JSON) to {}: key={}, app={}, messageId={}", 
                      inboundMessageTopic, key, app, messageId);
            
            // Save original event for tracking
            String jsonData = objectMapper.writeValueAsString(inboundMessageEvent);
            OriginalEvent originalEvent = new OriginalEvent(key, "InboundMessageEvent", inboundMessageTopic, jsonData);
            originalEventService.saveOriginalEvent(originalEvent);
            
            kafkaTemplate.send(inboundMessageTopic, key, inboundMessageEvent);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "InboundMessageEvent published successfully to " + inboundMessageTopic + " using JSON serialization",
                "key", key,
                "topic", inboundMessageTopic,
                "eventType", "InboundMessageEvent",
                "data", Map.of(
                    "app", inboundMessageEvent.getApp(),
                    "timestamp", inboundMessageEvent.getTimestamp(),
                    "version", inboundMessageEvent.getVersion(),
                    "type", inboundMessageEvent.getType(),
                    "messageId", inboundMessageEvent.getPayload().getId(),
                    "source", inboundMessageEvent.getPayload().getSource(),
                    "senderName", inboundMessageEvent.getPayload().getSender().getName(),
                    "content", inboundMessageEvent.getPayload().getPayload().getText()
                ),
                "originalEventId", originalEvent.getId(),
                "note", "This event will be transformed into CreateChatEvent and CreateMessageEvent"
            ));
            
        } catch (Exception e) {
            logger.error("Error publishing inbound message event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to publish InboundMessageEvent: " + e.getMessage()
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
     * GET /api/events/original
     * Retrieves all original events from H2 database
     */
    @GetMapping("/original")
    public ResponseEntity<Map<String, Object>> getOriginalEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String sourceTopic) {
        try {
            List<OriginalEvent> events;
            
            if (eventType != null) {
                events = originalEventService.getOriginalEventsByType(eventType);
            } else if (sourceTopic != null) {
                events = originalEventService.getOriginalEventsBySourceTopic(sourceTopic);
            } else {
                events = originalEventService.getAllOriginalEvents();
            }
            
            logger.info("Retrieved {} original events", events.size());
            
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
            logger.error("Error retrieving original events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to retrieve original events: " + e.getMessage()
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

    /**
     * DELETE /api/events/original
     * Deletes all original events (useful for testing)
     */
    @DeleteMapping("/original")
    public ResponseEntity<Map<String, Object>> deleteAllOriginalEvents() {
        try {
            originalEventService.deleteAllOriginalEvents();
            logger.info("All original events deleted");
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All original events deleted successfully"
            ));
            
        } catch (Exception e) {
            logger.error("Error deleting original events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Failed to delete original events: " + e.getMessage()
            ));
        }
    }
}