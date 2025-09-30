package com.example.kafkastream.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InboundMessageEvent DTO and its nested classes
 */
class InboundMessageEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testInboundMessageEventCreation() {
        // Create sender
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        
        // Create message content
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        
        // Create message payload
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "123456789011",
                "text",
                messageContent,
                sender
        );
        
        // Create inbound message event
        InboundMessageEvent event = new InboundMessageEvent(
                "TestApp", 1747854609182L, 2, "message", messagePayload);
        
        // Assertions
        assertEquals("TestApp", event.getApp());
        assertEquals(1747854609182L, event.getTimestamp());
        assertEquals(2, event.getVersion());
        assertEquals("message", event.getType());
        assertNotNull(event.getPayload());
        assertEquals("wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", 
                     event.getPayload().getId());
        assertEquals("123456789011", event.getPayload().getSource());
        assertEquals("text", event.getPayload().getType());
        assertEquals("3", event.getPayload().getPayload().getText());
        assertEquals("Online UserName", event.getPayload().getSender().getName());
        assertEquals("123456789011", event.getPayload().getSender().getPhone());
        assertEquals("1", event.getPayload().getSender().getCountryCode());
        assertEquals("23456789011", event.getPayload().getSender().getDialCode());
    }

    @Test
    void testInboundMessageEventJsonSerialization() throws Exception {
        // Create test event
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "123456789011", "text", messageContent, sender);
        InboundMessageEvent event = new InboundMessageEvent(
                "TestApp", 1747854609182L, 2, "message", messagePayload);
        
        // Test serialization
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"app\":\"TestApp\""));
        assertTrue(json.contains("\"timestamp\":1747854609182"));
        assertTrue(json.contains("\"version\":2"));
        assertTrue(json.contains("\"type\":\"message\""));
        assertTrue(json.contains("\"name\":\"Online UserName\""));
        assertTrue(json.contains("\"text\":\"3\""));
        
        // Test deserialization
        InboundMessageEvent deserializedEvent = objectMapper.readValue(json, InboundMessageEvent.class);
        assertEquals(event.getApp(), deserializedEvent.getApp());
        assertEquals(event.getTimestamp(), deserializedEvent.getTimestamp());
        assertEquals(event.getVersion(), deserializedEvent.getVersion());
        assertEquals(event.getType(), deserializedEvent.getType());
        assertEquals(event.getPayload().getId(), deserializedEvent.getPayload().getId());
        assertEquals(event.getPayload().getSender().getName(), deserializedEvent.getPayload().getSender().getName());
        assertEquals(event.getPayload().getPayload().getText(), deserializedEvent.getPayload().getPayload().getText());
    }

    @Test
    void testSenderCreation() {
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        
        assertEquals("123456789011", sender.getPhone());
        assertEquals("Online UserName", sender.getName());
        assertEquals("1", sender.getCountryCode());
        assertEquals("23456789011", sender.getDialCode());
        
        // Test toString
        String senderString = sender.toString();
        assertTrue(senderString.contains("Online UserName"));
        assertTrue(senderString.contains("123456789011"));
    }

    @Test
    void testMessageContentCreation() {
        InboundMessageEvent.MessageContent content = new InboundMessageEvent.MessageContent("Hello World");
        
        assertEquals("Hello World", content.getText());
        
        // Test toString
        String contentString = content.toString();
        assertTrue(contentString.contains("Hello World"));
    }

    @Test
    void testMessagePayloadCreation() {
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "123456789011", "Online UserName", "1", "23456789011");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("Test message");
        
        InboundMessageEvent.MessagePayload payload = new InboundMessageEvent.MessagePayload(
                "msg-123", "123456789011", "text", messageContent, sender);
        
        assertEquals("msg-123", payload.getId());
        assertEquals("123456789011", payload.getSource());
        assertEquals("text", payload.getType());
        assertEquals("Test message", payload.getPayload().getText());
        assertEquals("Online UserName", payload.getSender().getName());
        
        // Test toString
        String payloadString = payload.toString();
        assertTrue(payloadString.contains("msg-123"));
        assertTrue(payloadString.contains("Test message"));
    }
}