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
                "593987699757",    // chatId
                "Aruponse",        // userName
                "593987699757",    // userPhone
                "593",             // countryCode
                "987699757",       // dialCode
                1747854609182L     // createdAt
        );
        
        assertEquals("593987699757", event.getChatId());
        assertEquals("Aruponse", event.getUserName());
        assertEquals("593987699757", event.getUserPhone());
        assertEquals("593", event.getCountryCode());
        assertEquals("987699757", event.getDialCode());
        assertEquals(1747854609182L, event.getCreatedAt());
    }

    @Test
    void testCreateChatEventJsonSerialization() throws Exception {
        CreateChatEvent event = new CreateChatEvent(
                "593987699757", "Aruponse", "593987699757", "593", "987699757", 1747854609182L);
        
        // Test serialization
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"chat_id\":\"593987699757\""));
        assertTrue(json.contains("\"user_name\":\"Aruponse\""));
        assertTrue(json.contains("\"user_phone\":\"593987699757\""));
        assertTrue(json.contains("\"country_code\":\"593\""));
        assertTrue(json.contains("\"dial_code\":\"987699757\""));
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
                "593987699757", "Aruponse", "593987699757", "593", "987699757", 1747854609182L);
        
        String eventString = event.toString();
        assertTrue(eventString.contains("Aruponse"));
        assertTrue(eventString.contains("593987699757"));
        assertTrue(eventString.contains("593"));
        assertTrue(eventString.contains("987699757"));
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