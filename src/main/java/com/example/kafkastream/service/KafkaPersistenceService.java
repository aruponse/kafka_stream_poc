package com.example.kafkastream.service;

import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.NewFormatEvent;
import com.example.kafkastream.dto.SimpleEvent;
import com.example.kafkastream.dto.CreateChatEvent;
import com.example.kafkastream.dto.CreateMessageEvent;
import com.example.kafkastream.model.ProcessedEvent;
import com.example.kafkastream.model.OriginalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final OriginalEventService originalEventService;

    @Value("${app.kafka.topics.output-topic-transformed}")
    private String outputTopicTransformed;

    @Value("${app.kafka.topics.output-topic-json-converted}")
    private String outputTopicJsonConverted;

    @Value("${app.kafka.topics.output-topic-action-a}")
    private String outputTopicActionA;

    @Value("${app.kafka.topics.output-topic-action-b}")
    private String outputTopicActionB;

    @Value("${app.kafka.topics.create-chat-topic}")
    private String createChatTopic;

    @Value("${app.kafka.topics.create-message-topic}")
    private String createMessageTopic;

    public KafkaPersistenceService(ProcessedEventService processedEventService, OriginalEventService originalEventService) {
        this.processedEventService = processedEventService;
        this.originalEventService = originalEventService;
    }

    /**
     * Consume from output-topic-transformed (Use Case 1: Content Transformation)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-transformed}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "simpleEventKafkaListenerContainerFactory")
    public void consumeTransformedEvents(@Payload SimpleEvent event,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received transformed SimpleEvent (JSON) - Topic: {}, Key: {}, ID: {}, Payload: {}", 
                       topic, key, event.getId(), event.getPayload());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("SimpleEvent{id=%s, payload=%s, timestamp=%d}", 
                                                event.getId(), event.getPayload(), event.getTimestamp());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "SIMPLE_EVENT_TRANSFORMED",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted transformed SimpleEvent with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing transformed SimpleEvent from topic: {}, event: {}", topic, event, e);
        }
    }

    /**
     * Consume from output-topic-json-converted (Use Case 2: JSON Schema Conversion)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-json-converted}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "newFormatEventKafkaListenerContainerFactory")
    public void consumeJsonConvertedEvents(@Payload NewFormatEvent event,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received JSON converted NewFormatEvent (JSON) - Topic: {}, Key: {}, NewFieldName: {}, Data: {}", 
                       topic, key, event.getNewFieldName(), event.getData());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("NewFormatEvent{newFieldName=%s, convertedAt=%d, data=%s}", 
                                                event.getNewFieldName(), event.getConvertedAt(), event.getData());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "LEGACY_EVENT_CONVERTED",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted JSON converted NewFormatEvent with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing JSON converted NewFormatEvent from topic: {}, event: {}", topic, event, e);
        }
    }

    /**
     * Consume from output-topic-action-a (Use Case 3: Action Type A)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-action-a}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "genericActionKafkaListenerContainerFactory")
    public void consumeActionAEvents(@Payload GenericAction event,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received Action A GenericAction (JSON) - Topic: {}, Key: {}, ActionType: {}, Details: {}", 
                       topic, key, event.getActionType(), event.getDetails());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("GenericAction{actionType=%s, details=%s}", 
                                                event.getActionType(), event.getDetails());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "GENERIC_ACTION_TYPE_A",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted Action A GenericAction with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing Action A GenericAction from topic: {}, event: {}", topic, event, e);
        }
    }

    /**
     * Consume from output-topic-action-b (Use Case 3: Action Type B)
     */
    @KafkaListener(topics = "${app.kafka.topics.output-topic-action-b}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "genericActionKafkaListenerContainerFactory")
    public void consumeActionBEvents(@Payload GenericAction event,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received Action B GenericAction (JSON) - Topic: {}, Key: {}, ActionType: {}, Details: {}", 
                       topic, key, event.getActionType(), event.getDetails());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("GenericAction{actionType=%s, details=%s}", 
                                                event.getActionType(), event.getDetails());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "GENERIC_ACTION_TYPE_B",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted Action B GenericAction with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing Action B GenericAction from topic: {}, event: {}", topic, event, e);
        }
    }

    /**
     * Consume from create-chat-topic (Use Case 4: Create Chat Events)
     */
    @KafkaListener(topics = "${app.kafka.topics.create-chat-topic}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "createChatEventKafkaListenerContainerFactory")
    public void consumeCreateChatEvents(@Payload CreateChatEvent event,
                                      @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received CreateChatEvent (JSON) - Topic: {}, Key: {}, ChatId: {}, UserName: {}, UserPhone: {}", 
                       topic, key, event.getChatId(), event.getUserName(), event.getUserPhone());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("CreateChatEvent{chatId=%s, userName=%s, userPhone=%s, countryCode=%s, dialCode=%s, createdAt=%d}", 
                                                event.getChatId(), event.getUserName(), event.getUserPhone(), 
                                                event.getCountryCode(), event.getDialCode(), event.getCreatedAt());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "CREATE_CHAT_EVENT",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted CreateChatEvent with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing CreateChatEvent from topic: {}, event: {}", topic, event, e);
        }
    }

    /**
     * Consume from create-message-topic (Use Case 4: Create Message Events)
     */
    @KafkaListener(topics = "${app.kafka.topics.create-message-topic}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "createMessageEventKafkaListenerContainerFactory")
    public void consumeCreateMessageEvents(@Payload CreateMessageEvent event,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Received CreateMessageEvent (JSON) - Topic: {}, Key: {}, MessageId: {}, ChatId: {}, Content: {}", 
                       topic, key, event.getMessageId(), event.getChatId(), event.getContent());
            
            // Find the original event by key to link them
            String originalEventId = originalEventService.getOriginalEventByEventId(key)
                .map(OriginalEvent::getEventId)
                .orElse(null);
            
            // Convert DTO object to string representation for storage
            String eventMessage = String.format("CreateMessageEvent{messageId=%s, senderPhone=%s, chatId=%s, messageType=%s, content=%s, timestamp=%d}", 
                                                event.getMessageId(), event.getSenderPhone(), event.getChatId(), 
                                                event.getMessageType(), event.getContent(), event.getTimestamp());
            
            ProcessedEvent processedEvent = new ProcessedEvent(
                "CREATE_MESSAGE_EVENT",
                key,
                originalEventId,
                eventMessage,
                topic
            );
            
            processedEventService.saveProcessedEvent(processedEvent);
            logger.info("Successfully persisted CreateMessageEvent with ID: {}, linked to original event: {}", 
                       processedEvent.getId(), originalEventId);
            
        } catch (Exception e) {
            logger.error("Error processing CreateMessageEvent from topic: {}, event: {}", topic, event, e);
        }
    }
}