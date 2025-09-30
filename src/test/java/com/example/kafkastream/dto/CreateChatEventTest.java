package com.example.kafkastream.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateChatEvent DTO
 */
class CreateChatEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateChatEventCreation() {
        CreateChatEvent event = new CreateChatEvent(
                "123456789011",    // chatId
                "Online UserName",        // userName
                "123456789011",    // userPhone
                "1",             // countryCode
                "23456789011",       // dialCode
                1747854609182L     // createdAt
        );
        
        assertEquals("123456789011", event.getChatId());
        assertEquals("Online UserName", event.getUserName());
        assertEquals("123456789011", event.getUserPhone());
        assertEquals("1", event.getCountryCode());
        assertEquals("23456789011", event.getDialCode());
        assertEquals(1747854609182L, event.getCreatedAt());
    }

    @Test
    void testCreateChatEventJsonSerialization() throws Exception {
        CreateChatEvent event = new CreateChatEvent(
                "123456789011", "Online UserName", "123456789011", "1", "23456789011", 1747854609182L);
        
        // Test serialization
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"chat_id\":\"123456789011\""));
        assertTrue(json.contains("\"user_name\":\"Online UserName\""));
        assertTrue(json.contains("\"user_phone\":\"123456789011\""));
        assertTrue(json.contains("\"country_code\":\"1\""));
        assertTrue(json.contains("\"dial_code\":\"23456789011\""));
        assertTrue(json.contains("\"created_at\":1747854609182"));
        
        // Test deserialization
        CreateChatEvent deserializedEvent = objectMapper.readValue(json, CreateChatEvent.class);
        assertEquals(event.getChatId(), deserializedEvent.getChatId());
        assertEquals(event.getUserName(), deserializedEvent.getUserName());
        assertEquals(event.getUserPhone(), deserializedEvent.getUserPhone());
        assertEquals(event.getCountryCode(), deserializedEvent.getCountryCode());
        assertEquals(event.getDialCode(), deserializedEvent.getDialCode());
        assertEquals(event.getCreatedAt(), deserializedEvent.getCreatedAt());
    }

    @Test
    void testCreateChatEventToString() {
        CreateChatEvent event = new CreateChatEvent(
                "123456789011", "Online UserName", "123456789011", "1", "23456789011", 1747854609182L);
        
        String eventString = event.toString();
        assertTrue(eventString.contains("Online UserName"));
        assertTrue(eventString.contains("123456789011"));
        assertTrue(eventString.contains("1"));
        assertTrue(eventString.contains("23456789011"));
        assertTrue(eventString.contains("1747854609182"));
    }

    @Test
    void testCreateChatEventSettersAndGetters() {
        CreateChatEvent event = new CreateChatEvent();
        
        event.setChatId("test-chat-id");
        event.setUserName("Test User");
        event.setUserPhone("1234567890");
        event.setCountryCode("1");
        event.setDialCode("234567890");
        event.setCreatedAt(System.currentTimeMillis());
        
        assertEquals("test-chat-id", event.getChatId());
        assertEquals("Test User", event.getUserName());
        assertEquals("1234567890", event.getUserPhone());
        assertEquals("1", event.getCountryCode());
        assertEquals("234567890", event.getDialCode());
        assertNotNull(event.getCreatedAt());
    }
}