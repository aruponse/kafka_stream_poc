package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Inbound Message Event DTO for handling incoming WhatsApp-like messages
 * This event will be transformed into CreateChatEvent and CreateMessageEvent
 */
public class InboundMessageEvent {
    
    @JsonProperty("app")
    private String app;
    
    @JsonProperty("timestamp")
    private Long timestamp;
    
    @JsonProperty("version")
    private Integer version;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("payload")
    private MessagePayload payload;
    
    public InboundMessageEvent() {}
    
    public InboundMessageEvent(String app, Long timestamp, Integer version, String type, MessagePayload payload) {
        this.app = app;
        this.timestamp = timestamp;
        this.version = version;
        this.type = type;
        this.payload = payload;
    }
    
    // Getters and Setters
    public String getApp() {
        return app;
    }
    
    public void setApp(String app) {
        this.app = app;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public MessagePayload getPayload() {
        return payload;
    }
    
    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }
    
    @Override
    public String toString() {
        return "InboundMessageEvent{" +
                "app='" + app + '\'' +
                ", timestamp=" + timestamp +
                ", version=" + version +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
    
    /**
     * Nested class for message payload
     */
    public static class MessagePayload {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("source")
        private String source;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("payload")
        private MessageContent payload;
        
        @JsonProperty("sender")
        private Sender sender;
        
        public MessagePayload() {}
        
        public MessagePayload(String id, String source, String type, MessageContent payload, Sender sender) {
            this.id = id;
            this.source = source;
            this.type = type;
            this.payload = payload;
            this.sender = sender;
        }
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public MessageContent getPayload() {
            return payload;
        }
        
        public void setPayload(MessageContent payload) {
            this.payload = payload;
        }
        
        public Sender getSender() {
            return sender;
        }
        
        public void setSender(Sender sender) {
            this.sender = sender;
        }
        
        @Override
        public String toString() {
            return "MessagePayload{" +
                    "id='" + id + '\'' +
                    ", source='" + source + '\'' +
                    ", type='" + type + '\'' +
                    ", payload=" + payload +
                    ", sender=" + sender +
                    '}';
        }
    }
    
    /**
     * Nested class for message content
     */
    public static class MessageContent {
        @JsonProperty("text")
        private String text;
        
        public MessageContent() {}
        
        public MessageContent(String text) {
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return "MessageContent{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }
    
    /**
     * Nested class for sender information
     */
    public static class Sender {
        @JsonProperty("phone")
        private String phone;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("country_code")
        private String countryCode;
        
        @JsonProperty("dial_code")
        private String dialCode;
        
        public Sender() {}
        
        public Sender(String phone, String name, String countryCode, String dialCode) {
            this.phone = phone;
            this.name = name;
            this.countryCode = countryCode;
            this.dialCode = dialCode;
        }
        
        // Getters and Setters
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCountryCode() {
            return countryCode;
        }
        
        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
        
        public String getDialCode() {
            return dialCode;
        }
        
        public void setDialCode(String dialCode) {
            this.dialCode = dialCode;
        }
        
        @Override
        public String toString() {
            return "Sender{" +
                    "phone='" + phone + '\'' +
                    ", name='" + name + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    ", dialCode='" + dialCode + '\'' +
                    '}';
        }
    }
}