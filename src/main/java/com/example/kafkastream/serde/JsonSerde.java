package com.example.kafkastream.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Custom JSON Serde implementation using Jackson ObjectMapper
 * Provides serialization and deserialization for any Java objects to/from JSON strings
 */
public class JsonSerde<T> implements Serde<T> {
    
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public JsonSerde(Class<T> type) {
        this.type = type;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Serializer<T> serializer() {
        return new JsonSerializer<>(objectMapper);
    }

    @Override
    public Deserializer<T> deserializer() {
        return new JsonDeserializer<>(objectMapper, type);
    }

    public static class JsonSerializer<T> implements Serializer<T> {
        private final ObjectMapper objectMapper;
        private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

        public JsonSerializer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            // No configuration needed
        }

        @Override
        public byte[] serialize(String topic, T data) {
            if (data == null) {
                return null;
            }
            
            try {
                String jsonString = objectMapper.writeValueAsString(data);
                logger.debug("Serialized object to JSON: {}", jsonString);
                return jsonString.getBytes();
            } catch (JsonProcessingException e) {
                logger.error("Error serializing object to JSON: {}", e.getMessage(), e);
                throw new SerializationException("Error serializing object to JSON", e);
            }
        }

        @Override
        public void close() {
            // Nothing to close
        }
    }

    public static class JsonDeserializer<T> implements Deserializer<T> {
        private final ObjectMapper objectMapper;
        private final Class<T> type;
        private static final Logger logger = LoggerFactory.getLogger(JsonDeserializer.class);

        public JsonDeserializer(ObjectMapper objectMapper, Class<T> type) {
            this.objectMapper = objectMapper;
            this.type = type;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            // No configuration needed
        }

        @Override
        public T deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }
            
            try {
                String jsonString = new String(data);
                logger.debug("Deserializing JSON string: {}", jsonString);
                T result = objectMapper.readValue(jsonString, type);
                logger.debug("Deserialized JSON to object: {}", result);
                return result;
            } catch (IOException e) {
                logger.error("Error deserializing JSON to object: {}", e.getMessage(), e);
                throw new SerializationException("Error deserializing JSON to object", e);
            }
        }

        @Override
        public void close() {
            // Nothing to close
        }
    }
}