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
                "593987699757", "Aruponse", "593", "987699757");
        
        // Create message content
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        
        // Create message payload
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "593987699757",
                "text",
                messageContent,
                sender
        );
        
        // Create inbound message event
        InboundMessageEvent event = new InboundMessageEvent(
                "NoeSushi", 1747854609182L, 2, "message", messagePayload);
        
        // Assertions
        assertEquals("NoeSushi", event.getApp());
        assertEquals(1747854609182L, event.getTimestamp());
        assertEquals(2, event.getVersion());
        assertEquals("message", event.getType());
        assertNotNull(event.getPayload());
        assertEquals("wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", 
                     event.getPayload().getId());
        assertEquals("593987699757", event.getPayload().getSource());
        assertEquals("text", event.getPayload().getType());
        assertEquals("3", event.getPayload().getPayload().getText());
        assertEquals("Aruponse", event.getPayload().getSender().getName());
        assertEquals("593987699757", event.getPayload().getSender().getPhone());
        assertEquals("593", event.getPayload().getSender().getCountryCode());
        assertEquals("987699757", event.getPayload().getSender().getDialCode());
    }

    @Test
    void testInboundMessageEventJsonSerialization() throws Exception {
        // Create test event
        InboundMessageEvent.Sender sender = new InboundMessageEvent.Sender(
                "593987699757", "Aruponse", "593", "987699757");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("3");
        InboundMessageEvent.MessagePayload messagePayload = new InboundMessageEvent.MessagePayload(
                "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
                "593987699757", "text", messageContent, sender);
        InboundMessageEvent event = new InboundMessageEvent(
                "NoeSushi", 1747854609182L, 2, "message", messagePayload);
        
        // Test serialization
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"app\":\"NoeSushi\""));
        assertTrue(json.contains("\"timestamp\":1747854609182"));
        assertTrue(json.contains("\"version\":2"));
        assertTrue(json.contains("\"type\":\"message\""));
        assertTrue(json.contains("\"name\":\"Aruponse\""));
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
                "593987699757", "Aruponse", "593", "987699757");
        
        assertEquals("593987699757", sender.getPhone());
        assertEquals("Aruponse", sender.getName());
        assertEquals("593", sender.getCountryCode());
        assertEquals("987699757", sender.getDialCode());
        
        // Test toString
        String senderString = sender.toString();
        assertTrue(senderString.contains("Aruponse"));
        assertTrue(senderString.contains("593987699757"));
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
                "593987699757", "Aruponse", "593", "987699757");
        InboundMessageEvent.MessageContent messageContent = new InboundMessageEvent.MessageContent("Test message");
        
        InboundMessageEvent.MessagePayload payload = new InboundMessageEvent.MessagePayload(
                "msg-123", "593987699757", "text", messageContent, sender);
        
        assertEquals("msg-123", payload.getId());
        assertEquals("593987699757", payload.getSource());
        assertEquals("text", payload.getType());
        assertEquals("Test message", payload.getPayload().getText());
        assertEquals("Aruponse", payload.getSender().getName());
        
        // Test toString
        String payloadString = payload.toString();
        assertTrue(payloadString.contains("msg-123"));
        assertTrue(payloadString.contains("Test message"));
    }
}