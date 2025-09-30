package com.example.kafkastream.config;

import com.example.kafkastream.dto.CreateChatEvent;
import com.example.kafkastream.dto.CreateMessageEvent;
import com.example.kafkastream.dto.GenericAction;
import com.example.kafkastream.dto.NewFormatEvent;
import com.example.kafkastream.dto.SimpleEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration with specific type mappings for different topics
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer factory for CreateChatEvent
     */
    @Bean
    public ConsumerFactory<String, CreateChatEvent> createChatEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastream.dto");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CreateChatEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Listener container factory for CreateChatEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateChatEvent> createChatEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CreateChatEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createChatEventConsumerFactory());
        return factory;
    }

    /**
     * Consumer factory for CreateMessageEvent
     */
    @Bean
    public ConsumerFactory<String, CreateMessageEvent> createMessageEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastream.dto");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CreateMessageEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Listener container factory for CreateMessageEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateMessageEvent> createMessageEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CreateMessageEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createMessageEventConsumerFactory());
        return factory;
    }

    /**
     * Consumer factory for SimpleEvent (existing use cases)
     */
    @Bean
    public ConsumerFactory<String, SimpleEvent> simpleEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastream.dto");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SimpleEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Listener container factory for SimpleEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SimpleEvent> simpleEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SimpleEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(simpleEventConsumerFactory());
        return factory;
    }

    /**
     * Consumer factory for NewFormatEvent (existing use cases)
     */
    @Bean
    public ConsumerFactory<String, NewFormatEvent> newFormatEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastream.dto");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NewFormatEvent.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Listener container factory for NewFormatEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewFormatEvent> newFormatEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NewFormatEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(newFormatEventConsumerFactory());
        return factory;
    }

    /**
     * Consumer factory for GenericAction (existing use cases)
     */
    @Bean
    public ConsumerFactory<String, GenericAction> genericActionConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastream.dto");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, GenericAction.class.getName());
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Listener container factory for GenericAction
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GenericAction> genericActionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GenericAction> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(genericActionConsumerFactory());
        return factory;
    }
}