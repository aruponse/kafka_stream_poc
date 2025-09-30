package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Simple Event DTO used for Use Case 1 - Content Transformation
 * Receives events that will have their payload transformed (e.g., to uppercase or add prefix)
 */
public class SimpleEvent {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("payload")
    private String payload;
    
    @JsonProperty("timestamp")
    private Long timestamp;

    public SimpleEvent() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public SimpleEvent(String id, String payload) {
        this.id = id;
        this.payload = payload;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public SimpleEvent(String id, String payload, Long timestamp) {
        this.id = id;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SimpleEvent{" +
                "id='" + id + '\'' +
                ", payload='" + payload + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}