package com.example.kafkastream.service;

import com.example.kafkastream.model.ProcessedEvent;
import com.example.kafkastream.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing ProcessedEvent entities
 * Handles business logic for processed events and database operations
 */
@Service
@Transactional
public class ProcessedEventService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedEventService.class);

    private final ProcessedEventRepository processedEventRepository;

    @Autowired
    public ProcessedEventService(ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    /**
     * Save a processed event to the database
     */
    public ProcessedEvent saveProcessedEvent(ProcessedEvent processedEvent) {
        logger.info("Saving processed event: {}", processedEvent);
        return processedEventRepository.save(processedEvent);
    }

    /**
     * Save a processed event with specified parameters
     */
    public ProcessedEvent saveProcessedEvent(String eventType, String originalKey, 
                                           String processedData, String sourceTopic) {
        ProcessedEvent event = new ProcessedEvent(eventType, originalKey, processedData, sourceTopic);
        return saveProcessedEvent(event);
    }

    /**
     * Get all processed events ordered by processing date (newest first)
     */
    @Transactional(readOnly = true)
    public List<ProcessedEvent> getAllProcessedEvents() {
        logger.info("Retrieving all processed events");
        return processedEventRepository.findAllOrderedByProcessedAtDesc();
    }

    /**
     * Get processed events by event type
     */
    @Transactional(readOnly = true)
    public List<ProcessedEvent> getProcessedEventsByType(String eventType) {
        logger.info("Retrieving processed events by type: {}", eventType);
        return processedEventRepository.findByEventType(eventType);
    }

    /**
     * Get processed events by source topic
     */
    @Transactional(readOnly = true)
    public List<ProcessedEvent> getProcessedEventsBySourceTopic(String sourceTopic) {
        logger.info("Retrieving processed events by source topic: {}", sourceTopic);
        return processedEventRepository.findBySourceTopic(sourceTopic);
    }

    /**
     * Get count of processed events by type
     */
    @Transactional(readOnly = true)
    public long getCountByEventType(String eventType) {
        return processedEventRepository.countByEventType(eventType);
    }

    /**
     * Delete all processed events (useful for testing)
     */
    public void deleteAllProcessedEvents() {
        logger.warn("Deleting all processed events");
        processedEventRepository.deleteAll();
    }
}