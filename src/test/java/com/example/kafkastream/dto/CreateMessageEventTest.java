package com.example.kafkastream.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateMessageEvent DTO
 */
class CreateMessageEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateMessageEventCreation() {
        CreateMessageEvent event = new CreateMessageEvent(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", // messageId
                "593987699757",    // senderPhone
                "593987699757",    // chatId
                "text",            // messageType
                "3",               // content
                1747854609182L     // timestamp
        );
        
        assertEquals("wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", event.getMessageId());
        assertEquals("593987699757", event.getSenderPhone());
        assertEquals("593987699757", event.getChatId());
        assertEquals("text", event.getMessageType());
        assertEquals("3", event.getContent());
        assertEquals(1747854609182L, event.getTimestamp());
    }

    @Test
    void testCreateMessageEventJsonSerialization() throws Exception {
        CreateMessageEvent event = new CreateMessageEvent(
                "msg-123", "593987699757", "593987699757", "text", "Hello World", 1747854609182L);
        
        // Test serialization
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"message_id\":\"msg-123\""));
        assertTrue(json.contains("\"sender_phone\":\"593987699757\""));
        assertTrue(json.contains("\"chat_id\":\"593987699757\""));
        assertTrue(json.contains("\"message_type\":\"text\""));
        assertTrue(json.contains("\"content\":\"Hello World\""));
        assertTrue(json.contains("\"timestamp\":1747854609182"));
        
        // Test deserialization
        CreateMessageEvent deserializedEvent = objectMapper.readValue(json, CreateMessageEvent.class);
        assertEquals(event.getMessageId(), deserializedEvent.getMessageId());
        assertEquals(event.getSenderPhone(), deserializedEvent.getSenderPhone());
        assertEquals(event.getChatId(), deserializedEvent.getChatId());
        assertEquals(event.getMessageType(), deserializedEvent.getMessageType());
        assertEquals(event.getContent(), deserializedEvent.getContent());
        assertEquals(event.getTimestamp(), deserializedEvent.getTimestamp());
    }

    @Test
    void testCreateMessageEventToString() {
        CreateMessageEvent event = new CreateMessageEvent(
                "msg-123", "593987699757", "593987699757", "text", "Test message", 1747854609182L);
        
        String eventString = event.toString();
        assertTrue(eventString.contains("msg-123"));
        assertTrue(eventString.contains("593987699757"));
        assertTrue(eventString.contains("text"));
        assertTrue(eventString.contains("Test message"));
        assertTrue(eventString.contains("1747854609182"));
    }

    @Test
    void testCreateMessageEventSettersAndGetters() {
        CreateMessageEvent event = new CreateMessageEvent();
        
        event.setMessageId("test-message-id");
        event.setSenderPhone("1234567890");
        event.setChatId("test-chat-id");
        event.setMessageType("image");
        event.setContent("Test content");
        event.setTimestamp(System.currentTimeMillis());
        
        assertEquals("test-message-id", event.getMessageId());
        assertEquals("1234567890", event.getSenderPhone());
        assertEquals("test-chat-id", event.getChatId());
        assertEquals("image", event.getMessageType());
        assertEquals("Test content", event.getContent());
        assertNotNull(event.getTimestamp());
    }
}