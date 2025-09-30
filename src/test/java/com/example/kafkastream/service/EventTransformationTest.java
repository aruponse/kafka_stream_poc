package com.example.kafkastream.service;

import com.example.kafkastream.dto.InboundMessageEvent;
import com.example.kafkastream.dto.CreateChatEvent;
import com.example.kafkastream.dto.CreateMessageEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for event transformation logic
 * Tests the conversion from InboundMessageEvent to CreateChatEvent and CreateMessageEvent
 */
class EventTransformationTest {

    @Test
    void testInboundMessageEventToCreateChatEventTransformation() {
        // Create test inbound message event
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "123456789011", "text", messageContent, sender);
        InboundMessageEvent inboundEvent = new InboundMessageEvent(
                "TestApp", 1747854609182L, 2, "message", messagePayload);

        // Transform to CreateChatEvent (simulating Kafka Streams transformation)
        CreateChatEvent chatEvent = new CreateChatEvent(
                inboundEvent.getPayload().getSource(),           // chat_id
                inboundEvent.getPayload().getSender().getName(), // user_name
                inboundEvent.getPayload().getSender().getPhone(), // user_phone
                inboundEvent.getPayload().getSender().getCountryCode(), // country_code
                inboundEvent.getPayload().getSender().getDialCode(), // dial_code
                inboundEvent.getTimestamp()                      // created_at
        );

        // Verify transformation
        assertEquals("123456789011", chatEvent.getChatId());
        assertEquals("Online UserName", chatEvent.getUserName());
        assertEquals("123456789011", chatEvent.getUserPhone());
        assertEquals("1", chatEvent.getCountryCode());
        assertEquals("23456789011", chatEvent.getDialCode());
        assertEquals(1747854609182L, chatEvent.getCreatedAt());
    }

    @Test
    void testInboundMessageEventToCreateMessageEventTransformation() {
        // Create test inbound message event
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "123456789011", "text", messageContent, sender);
        InboundMessageEvent inboundEvent = new InboundMessageEvent(
                "TestApp", 1747854609182L, 2, "message", messagePayload);

        // Transform to CreateMessageEvent (simulating Kafka Streams transformation)
        CreateMessageEvent messageEvent = new CreateMessageEvent(
                inboundEvent.getPayload().getId(),                       // message_id
                inboundEvent.getPayload().getSender().getPhone(),       // sender_phone
                inboundEvent.getPayload().getSource(),                  // chat_id
                inboundEvent.getPayload().getType(),                    // message_type
                inboundEvent.getPayload().getPayload().getText(),       // content
                inboundEvent.getTimestamp()                             // timestamp
        );

        // Verify transformation
        assertEquals("wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", messageEvent.getMessageId());
        assertEquals("123456789011", messageEvent.getSenderPhone());
        assertEquals("123456789011", messageEvent.getChatId());
        assertEquals("text", messageEvent.getMessageType());
        assertEquals("3", messageEvent.getContent());
        assertEquals(1747854609182L, messageEvent.getTimestamp());
    }

    @Test
    void testCompleteEventTransformationWorkflow() {
        // Create comprehensive test event
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "1234567890", "John Doe", "1", "234567890");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("Hello World!");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "unique-message-id-123", "1234567890", "text", messageContent, sender);
        InboundMessageEvent inboundEvent = new InboundMessageEvent(
                "TestApp", System.currentTimeMillis(), 1, "message", messagePayload);

        // Transform to both events
        CreateChatEvent chatEvent = new CreateChatEvent(
                inboundEvent.getPayload().getSource(),
                inboundEvent.getPayload().getSender().getName(),
                inboundEvent.getPayload().getSender().getPhone(),
                inboundEvent.getPayload().getSender().getCountryCode(),
                inboundEvent.getPayload().getSender().getDialCode(),
                inboundEvent.getTimestamp()
        );

        CreateMessageEvent messageEvent = new CreateMessageEvent(
                inboundEvent.getPayload().getId(),
                inboundEvent.getPayload().getSender().getPhone(),
                inboundEvent.getPayload().getSource(),
                inboundEvent.getPayload().getType(),
                inboundEvent.getPayload().getPayload().getText(),
                inboundEvent.getTimestamp()
        );

        // Verify both transformations preserve consistent data
        assertEquals(chatEvent.getChatId(), messageEvent.getChatId()); // Both should have same chat_id
        assertEquals(chatEvent.getUserPhone(), messageEvent.getSenderPhone()); // Same phone
        assertEquals(chatEvent.getCreatedAt(), messageEvent.getTimestamp()); // Same timestamp
        
        // Verify specific transformations
        assertEquals("TestApp", inboundEvent.getApp());
        assertEquals("unique-message-id-123", messageEvent.getMessageId());
        assertEquals("John Doe", chatEvent.getUserName());
        assertEquals("Hello World!", messageEvent.getContent());
        assertEquals("1", chatEvent.getCountryCode());
        assertEquals("234567890", chatEvent.getDialCode());
    }

    @Test
    void testTransformationWithDifferentMessageTypes() {
        // Test with image message type
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "9876543210", "Jane Smith", "44", "876543210");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("image-url-here");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "image-msg-id", "9876543210", "image", messageContent, sender);
        InboundMessageEvent inboundEvent = new InboundMessageEvent(
                "ImageApp", 1234567890L, 3, "message", messagePayload);

        // Transform events
        CreateChatEvent chatEvent = new CreateChatEvent(
                messagePayload.getSource(),
                sender.getName(),
                sender.getPhone(),
                sender.getCountryCode(),
                sender.getDialCode(),
                inboundEvent.getTimestamp()
        );

        CreateMessageEvent messageEvent = new CreateMessageEvent(
                messagePayload.getId(),
                sender.getPhone(),
                messagePayload.getSource(),
                messagePayload.getType(),
                messageContent.getText(),
                inboundEvent.getTimestamp()
        );

        // Verify image-specific transformations
        assertEquals("image", messageEvent.getMessageType());
        assertEquals("image-url-here", messageEvent.getContent());
        assertEquals("Jane Smith", chatEvent.getUserName());
        assertEquals("44", chatEvent.getCountryCode());
    }
}