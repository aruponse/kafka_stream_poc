package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Legacy Event DTO used for Use Case 2 - JSON Schema Conversion
 * Represents the old format that will be converted to NewFormatEvent
 */
public class LegacyEvent {
    
    @JsonProperty("old_field_name")
    private String oldFieldName;
    
    @JsonProperty("value")
    private String value;

    public LegacyEvent() {}

    public LegacyEvent(String oldFieldName, String value) {
        this.oldFieldName = oldFieldName;
        this.value = value;
    }

    public String getOldFieldName() {
        return oldFieldName;
    }

    public void setOldFieldName(String oldFieldName) {
        this.oldFieldName = oldFieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LegacyEvent{" +
                "oldFieldName='" + oldFieldName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}