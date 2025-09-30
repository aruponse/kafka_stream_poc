package com.example.kafkastream.repository;

import com.example.kafkastream.model.OriginalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing OriginalEvent entities
 * Provides methods to track and query original events published to Kafka
 */
@Repository
public interface OriginalEventRepository extends JpaRepository<OriginalEvent, Long> {

    /**
     * Find an original event by its event ID
     * @param eventId The unique event identifier
     * @return Optional containing the original event if found
     */
    Optional<OriginalEvent> findByEventId(String eventId);

    /**
     * Find all original events by event type
     * @param eventType The type of event (SimpleEvent, LegacyEvent, GenericAction)
     * @return List of original events of the specified type
     */
    List<OriginalEvent> findByEventType(String eventType);

    /**
     * Find all original events by source topic
     * @param sourceTopic The topic where the event was published
     * @return List of original events from the specified topic
     */
    List<OriginalEvent> findBySourceTopic(String sourceTopic);

    /**
     * Count original events by event type
     * @param eventType The type of event
     * @return Count of events of the specified type
     */
    @Query("SELECT COUNT(o) FROM OriginalEvent o WHERE o.eventType = :eventType")
    long countByEventType(@Param("eventType") String eventType);

    /**
     * Get all original events ordered by published date (newest first)
     * @return List of all original events ordered by publication time
     */
    @Query("SELECT o FROM OriginalEvent o ORDER BY o.publishedAt DESC")
    List<OriginalEvent> findAllOrderByPublishedAtDesc();

    /**
     * Check if an event ID already exists
     * @param eventId The event ID to check
     * @return True if exists, false otherwise
     */
    boolean existsByEventId(String eventId);
}