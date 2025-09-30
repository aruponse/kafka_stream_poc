package com.example.kafkastream.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA Entity to persist processed events in H2 database
 * Stores the final results from all Kafka Streams processing
 */
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "original_key")
    private String originalKey;
    
    @Column(name = "processed_data", columnDefinition = "TEXT")
    private String processedData;
    
    @Column(name = "source_topic", nullable = false)
    private String sourceTopic;
    
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedEvent() {
        this.processedAt = Instant.now();
    }

    public ProcessedEvent(String eventType, String originalKey, String processedData, String sourceTopic) {
        this.eventType = eventType;
        this.originalKey = originalKey;
        this.processedData = processedData;
        this.sourceTopic = sourceTopic;
        this.processedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public void setOriginalKey(String originalKey) {
        this.originalKey = originalKey;
    }

    public String getProcessedData() {
        return processedData;
    }

    public void setProcessedData(String processedData) {
        this.processedData = processedData;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "ProcessedEvent{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", originalKey='" + originalKey + '\'' +
                ", processedData='" + processedData + '\'' +
                ", sourceTopic='" + sourceTopic + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}