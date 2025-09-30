package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * New Format Event DTO used for Use Case 2 - JSON Schema Conversion
 * Represents the new format after converting from LegacyEvent
 */
public class NewFormatEvent {
    
    @JsonProperty("newFieldName")
    private String newFieldName;
    
    @JsonProperty("data")
    private String data;
    
    @JsonProperty("convertedAt")
    private Long convertedAt;

    public NewFormatEvent() {
        this.convertedAt = Instant.now().toEpochMilli();
    }

    public NewFormatEvent(String newFieldName, String data) {
        this.newFieldName = newFieldName;
        this.data = data;
        this.convertedAt = Instant.now().toEpochMilli();
    }

    public NewFormatEvent(String newFieldName, String data, Long convertedAt) {
        this.newFieldName = newFieldName;
        this.data = data;
        this.convertedAt = convertedAt;
    }

    public String getNewFieldName() {
        return newFieldName;
    }

    public void setNewFieldName(String newFieldName) {
        this.newFieldName = newFieldName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getConvertedAt() {
        return convertedAt;
    }

    public void setConvertedAt(Long convertedAt) {
        this.convertedAt = convertedAt;
    }

    @Override
    public String toString() {
        return "NewFormatEvent{" +
                "newFieldName='" + newFieldName + '\'' +
                ", data='" + data + '\'' +
                ", convertedAt=" + convertedAt +
                '}';
    }
}