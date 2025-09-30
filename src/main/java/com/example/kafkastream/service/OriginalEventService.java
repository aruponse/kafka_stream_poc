package com.example.kafkastream.service;

import com.example.kafkastream.model.OriginalEvent;
import com.example.kafkastream.repository.OriginalEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service to manage original events published to Kafka topics
 * Provides business logic for tracking and retrieving original events
 */
@Service
public class OriginalEventService {

    private static final Logger logger = LoggerFactory.getLogger(OriginalEventService.class);

    private final OriginalEventRepository originalEventRepository;

    public OriginalEventService(OriginalEventRepository originalEventRepository) {
        this.originalEventRepository = originalEventRepository;
    }

    /**
     * Save an original event to the database
     * @param originalEvent The original event to save
     * @return The saved original event
     */
    public OriginalEvent saveOriginalEvent(OriginalEvent originalEvent) {
        try {
            OriginalEvent saved = originalEventRepository.save(originalEvent);
            logger.info("Saved original event: id={}, eventId={}, eventType={}", 
                       saved.getId(), saved.getEventId(), saved.getEventType());
            return saved;
        } catch (Exception e) {
            logger.error("Error saving original event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save original event", e);
        }
    }

    /**
     * Get all original events
     * @return List of all original events
     */
    public List<OriginalEvent> getAllOriginalEvents() {
        return originalEventRepository.findAllOrderByPublishedAtDesc();
    }

    /**
     * Find original event by event ID
     * @param eventId The event ID to search for
     * @return Optional containing the original event if found
     */
    public Optional<OriginalEvent> getOriginalEventByEventId(String eventId) {
        return originalEventRepository.findByEventId(eventId);
    }

    /**
     * Get original events by event type
     * @param eventType The event type to filter by
     * @return List of original events of the specified type
     */
    public List<OriginalEvent> getOriginalEventsByType(String eventType) {
        return originalEventRepository.findByEventType(eventType);
    }

    /**
     * Get original events by source topic
     * @param sourceTopic The source topic to filter by
     * @return List of original events from the specified topic
     */
    public List<OriginalEvent> getOriginalEventsBySourceTopic(String sourceTopic) {
        return originalEventRepository.findBySourceTopic(sourceTopic);
    }

    /**
     * Get count of original events by type
     * @param eventType The event type to count
     * @return Count of events of the specified type
     */
    public long getCountByEventType(String eventType) {
        return originalEventRepository.countByEventType(eventType);
    }

    /**
     * Check if an event ID already exists
     * @param eventId The event ID to check
     * @return True if exists, false otherwise
     */
    public boolean existsByEventId(String eventId) {
        return originalEventRepository.existsByEventId(eventId);
    }

    /**
     * Delete all original events (useful for testing)
     */
    public void deleteAllOriginalEvents() {
        originalEventRepository.deleteAll();
        logger.info("Deleted all original events");
    }
}