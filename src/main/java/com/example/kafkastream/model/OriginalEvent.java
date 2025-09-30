package com.example.kafkastream.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA Entity to track original events published to Kafka topics
 * This allows us to correlate processed events with their original source
 */
@Entity
@Table(name = "original_events")
public class OriginalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId; // UUID or key used when publishing to Kafka

    @Column(name = "event_type", nullable = false)
    private String eventType; // SimpleEvent, LegacyEvent, GenericAction

    @Column(name = "source_topic", nullable = false)
    private String sourceTopic; // input-topic, actions-topic

    @Column(name = "original_data", nullable = false, columnDefinition = "TEXT")
    private String originalData; // JSON data as published

    @Column(name = "published_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant publishedAt;

    // Constructors
    public OriginalEvent() {
        this.publishedAt = Instant.now();
    }

    public OriginalEvent(String eventId, String eventType, String sourceTopic, String originalData) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceTopic = sourceTopic;
        this.originalData = originalData;
        this.publishedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public String getOriginalData() {
        return originalData;
    }

    public void setOriginalData(String originalData) {
        this.originalData = originalData;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "OriginalEvent{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", sourceTopic='" + sourceTopic + '\'' +
                ", originalData='" + originalData + '\'' +
                ", publishedAt=" + publishedAt +
                '}';
    }
}