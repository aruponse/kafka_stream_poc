package com.example.kafkastream.repository;

import com.example.kafkastream.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for ProcessedEvent entity
 * Provides CRUD operations and custom queries for processed events
 */
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    /**
     * Find all processed events by event type
     */
    List<ProcessedEvent> findByEventType(String eventType);

    /**
     * Find all processed events by source topic
     */
    List<ProcessedEvent> findBySourceTopic(String sourceTopic);

    /**
     * Find all processed events ordered by processed date (newest first)
     */
    @Query("SELECT pe FROM ProcessedEvent pe ORDER BY pe.processedAt DESC")
    List<ProcessedEvent> findAllOrderedByProcessedAtDesc();

    /**
     * Count events by event type
     */
    @Query("SELECT COUNT(pe) FROM ProcessedEvent pe WHERE pe.eventType = :eventType")
    long countByEventType(@Param("eventType") String eventType);
}