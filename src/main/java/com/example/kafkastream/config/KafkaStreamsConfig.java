package com.example.kafkastream.config;

import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.LegacyEvent;
import com.example.kafkastream.dto.NewFormatEvent;
import com.example.kafkastream.dto.SimpleEvent;
import com.example.kafkastream.dto.InboundMessageEvent;
import com.example.kafkastream.dto.CreateChatEvent;
import com.example.kafkastream.dto.CreateMessageEvent;
import com.example.kafkastream.serde.JsonSerde;
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
    
    @Value("${app.kafka.topics.legacy-events-topic}")
    private String legacyEventsTopic;

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

    @Value("${app.kafka.topics.inbound-message-topic}")
    private String inboundMessageTopic;

    @Value("${app.kafka.topics.create-chat-topic}")
    private String createChatTopic;

    @Value("${app.kafka.topics.create-message-topic}")
    private String createMessageTopic;

    @Bean
    KStream<String, SimpleEvent> kStream(StreamsBuilder streamsBuilder) {
        logger.info("Initializing Kafka Streams topology with JSON serialization");

        // Configure JSON Serdes
        final JsonSerde<SimpleEvent> simpleEventSerde = new JsonSerde<>(SimpleEvent.class);
        final JsonSerde<LegacyEvent> legacyEventSerde = new JsonSerde<>(LegacyEvent.class);
        final JsonSerde<NewFormatEvent> newFormatEventSerde = new JsonSerde<>(NewFormatEvent.class);
        final JsonSerde<GenericAction> genericActionSerde = new JsonSerde<>(GenericAction.class);

        // =====================================
        // USE CASE 1: Content Transformation
        // =====================================
        KStream<String, SimpleEvent> inputStream = streamsBuilder
                .stream(inputTopic, Consumed.with(Serdes.String(), simpleEventSerde))
                .peek((key, value) -> logger.info("Processing input-topic SimpleEvent: key={}, id={}, payload={}", 
                      key, value.getId(), value.getPayload()));

        // Transform SimpleEvent by adding processed timestamp and modifying payload
        inputStream
                .mapValues(event -> {
                    try {
                        logger.info("Processing SimpleEvent transformation: id={}, payload={}", event.getId(), event.getPayload());
                        
                        // Transform the content using regular constructor
                        String originalPayload = event.getPayload();
                        SimpleEvent transformedEvent = new SimpleEvent(
                                event.getId(),
                                "TRANSFORMED: " + originalPayload,
                                System.currentTimeMillis()
                        );
                        
                        logger.info("Transformed SimpleEvent from '{}' to: {}", originalPayload, transformedEvent.getPayload());
                        return transformedEvent;
                    } catch (Exception e) {
                        logger.error("Error transforming SimpleEvent: {}", e.getMessage(), e);
                        return event; // Return original on error
                    }
                }, Named.as("transform-simple-event"))
                .to(outputTopicTransformed, Produced.with(Serdes.String(), simpleEventSerde));

        // =====================================
        // USE CASE 2: JSON Schema Conversion
        // =====================================
        // Convert legacy format to new format (using separate topic to avoid serde conflicts)
        KStream<String, LegacyEvent> legacyStream = streamsBuilder
                .stream(legacyEventsTopic, Consumed.with(Serdes.String(), legacyEventSerde))
                .peek((key, value) -> logger.info("Processing legacy-events-topic LegacyEvent: key={}, value={}", 
                      key, value.getValue()));

        legacyStream
                .mapValues(legacyEvent -> {
                    try {
                        logger.info("Processing LegacyEvent conversion: value={}", legacyEvent.getValue());
                        
                        // Convert to new format using regular constructor
                        NewFormatEvent newEvent = new NewFormatEvent(
                                legacyEvent.getValue(),
                                "legacy-system",
                                System.currentTimeMillis()
                        );
                        
                        logger.info("Converted LegacyEvent '{}' to NewFormatEvent: {}", 
                                  legacyEvent.getValue(), newEvent.getNewFieldName());
                        return newEvent;
                    } catch (Exception e) {
                        logger.error("Error converting legacy event: {}", e.getMessage(), e);
                        // Return a default NewFormatEvent on error
                        return new NewFormatEvent(
                                "ERROR_PROCESSING",
                                "error-occurred",
                                System.currentTimeMillis()
                        );
                    }
                }, Named.as("convert-legacy-event"))
                .to(outputTopicJsonConverted, Produced.with(Serdes.String(), newFormatEventSerde));

        // =====================================
        // USE CASE 3: Process actions-topic for Routing and Division
        // =====================================
        KStream<String, GenericAction> actionsStream = streamsBuilder
                .stream(actionsTopic, Consumed.with(Serdes.String(), genericActionSerde))
                .peek((key, value) -> logger.info("Processing actions-topic GenericAction: key={}, actionType={}, details={}", 
                      key, value.getActionType(), value.getDetails()));

        // Split stream based on actionType and route to different topics
        actionsStream
                .filter((key, value) -> {
                    boolean isActionA = "A".equalsIgnoreCase(value.getActionType());
                    logger.debug("Checking Action Type A filter: isActionA={}, actionType={}", isActionA, value.getActionType());
                    return isActionA;
                })
                .mapValues(action -> {
                    try {
                        logger.info("Processing Action Type A transformation: actionType={}, details={}", 
                                  action.getActionType(), action.getDetails());
                        
                        // Transform for Action A using regular constructor
                        String originalDetails = action.getDetails();
                        GenericAction processedAction = new GenericAction(
                                action.getActionType(),
                                "ACTION_A_PROCESSED: " + originalDetails
                        );
                        
                        logger.info("Transformed Action A from '{}' to: {}", originalDetails, processedAction.getDetails());
                        return processedAction;
                    } catch (Exception e) {
                        logger.error("Error processing Action A: {}", e.getMessage(), e);
                        return action;
                    }
                }, Named.as("process-action-a"))
                .to(outputTopicActionA, Produced.with(Serdes.String(), genericActionSerde));

        // Process Action Type B
        actionsStream
                .filter((key, value) -> {
                    boolean isActionB = "B".equalsIgnoreCase(value.getActionType());
                    logger.debug("Checking Action Type B filter: isActionB={}, actionType={}", isActionB, value.getActionType());
                    return isActionB;
                })
                .mapValues(action -> {
                    try {
                        logger.info("Processing Action Type B transformation: actionType={}, details={}", 
                                  action.getActionType(), action.getDetails());
                        
                        // Transform for Action B using regular constructor
                        String originalDetails = action.getDetails();
                        GenericAction processedAction = new GenericAction(
                                action.getActionType(),
                                "ACTION_B_PROCESSED: " + originalDetails
                        );
                        
                        logger.info("Transformed Action B from '{}' to: {}", originalDetails, processedAction.getDetails());
                        return processedAction;
                    } catch (Exception e) {
                        logger.error("Error processing Action B: {}", e.getMessage(), e);
                        return action;
                    }
                }, Named.as("process-action-b"))
                .to(outputTopicActionB, Produced.with(Serdes.String(), genericActionSerde));

        // =====================================
        // USE CASE 4: Inbound Message Processing
        // =====================================
        final JsonSerde<InboundMessageEvent> inboundMessageEventSerde = new JsonSerde<>(InboundMessageEvent.class);
        final JsonSerde<CreateChatEvent> createChatEventSerde = new JsonSerde<>(CreateChatEvent.class);
        final JsonSerde<CreateMessageEvent> createMessageEventSerde = new JsonSerde<>(CreateMessageEvent.class);

        KStream<String, InboundMessageEvent> inboundMessageStream = streamsBuilder
                .stream(inboundMessageTopic, Consumed.with(Serdes.String(), inboundMessageEventSerde))
                .peek((key, value) -> logger.info("Processing inbound-message-topic InboundMessageEvent: key={}, app={}, type={}", 
                      key, value.getApp(), value.getType()));

        // Transform InboundMessageEvent into CreateChatEvent
        inboundMessageStream
                .mapValues(inboundEvent -> {
                    try {
                        logger.info("Processing InboundMessageEvent to CreateChatEvent transformation: messageId={}, senderName={}", 
                                  inboundEvent.getPayload().getId(), inboundEvent.getPayload().getSender().getName());
                        
                        // Extract data from inbound message
                        InboundMessageEvent.MessagePayload payload = inboundEvent.getPayload();
                        InboundMessageEvent.Sender sender = payload.getSender();
                        
                        // Create CreateChatEvent
                        CreateChatEvent chatEvent = new CreateChatEvent(
                                payload.getSource(),           // chat_id
                                sender.getName(),              // user_name
                                sender.getPhone(),             // user_phone
                                sender.getCountryCode(),       // country_code
                                sender.getDialCode(),          // dial_code
                                inboundEvent.getTimestamp()    // created_at
                        );
                        
                        logger.info("Created CreateChatEvent: chatId={}, userName={}, userPhone={}", 
                                  chatEvent.getChatId(), chatEvent.getUserName(), chatEvent.getUserPhone());
                        return chatEvent;
                    } catch (Exception e) {
                        logger.error("Error transforming InboundMessageEvent to CreateChatEvent: {}", e.getMessage(), e);
                        // Return a default CreateChatEvent on error
                        return new CreateChatEvent(
                                "ERROR_CHAT_ID",
                                "ERROR_USER",
                                "ERROR_PHONE",
                                "ERROR_COUNTRY",
                                "ERROR_DIAL",
                                System.currentTimeMillis()
                        );
                    }
                }, Named.as("transform-to-chat-event"))
                .to(createChatTopic, Produced.with(Serdes.String(), createChatEventSerde));

        // Transform InboundMessageEvent into CreateMessageEvent
        inboundMessageStream
                .mapValues(inboundEvent -> {
                    try {
                        logger.info("Processing InboundMessageEvent to CreateMessageEvent transformation: messageId={}, content={}", 
                                  inboundEvent.getPayload().getId(), inboundEvent.getPayload().getPayload().getText());
                        
                        // Extract data from inbound message
                        InboundMessageEvent.MessagePayload payload = inboundEvent.getPayload();
                        InboundMessageEvent.Sender sender = payload.getSender();
                        
                        // Create CreateMessageEvent
                        CreateMessageEvent messageEvent = new CreateMessageEvent(
                                payload.getId(),                       // message_id
                                sender.getPhone(),                     // sender_phone
                                payload.getSource(),                   // chat_id
                                payload.getType(),                     // message_type
                                payload.getPayload().getText(),       // content
                                inboundEvent.getTimestamp()           // timestamp
                        );
                        
                        logger.info("Created CreateMessageEvent: messageId={}, chatId={}, content={}", 
                                  messageEvent.getMessageId(), messageEvent.getChatId(), messageEvent.getContent());
                        return messageEvent;
                    } catch (Exception e) {
                        logger.error("Error transforming InboundMessageEvent to CreateMessageEvent: {}", e.getMessage(), e);
                        // Return a default CreateMessageEvent on error
                        return new CreateMessageEvent(
                                "ERROR_MESSAGE_ID",
                                "ERROR_SENDER",
                                "ERROR_CHAT_ID",
                                "error",
                                "ERROR_CONTENT",
                                System.currentTimeMillis()
                        );
                    }
                }, Named.as("transform-to-message-event"))
                .to(createMessageTopic, Produced.with(Serdes.String(), createMessageEventSerde));

        logger.info("Kafka Streams topology configured successfully with JSON serialization");
        return inputStream; // Return the main stream
    }
}