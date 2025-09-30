package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Create Message Event DTO - Generated from InboundMessageEvent
 * Represents the creation of a new message within a chat
 */
public class CreateMessageEvent {
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("sender_phone")
    private String senderPhone;
    
    @JsonProperty("chat_id")
    private String chatId;
    
    @JsonProperty("message_type")
    private String messageType;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("timestamp")
    private Long timestamp;
    
    public CreateMessageEvent() {}
    
    public CreateMessageEvent(String messageId, String senderPhone, String chatId, String messageType, String content, Long timestamp) {
        this.messageId = messageId;
        this.senderPhone = senderPhone;
        this.chatId = chatId;
        this.messageType = messageType;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getSenderPhone() {
        return senderPhone;
    }
    
    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "CreateMessageEvent{" +
                "messageId='" + messageId + '\'' +
                ", senderPhone='" + senderPhone + '\'' +
                ", chatId='" + chatId + '\'' +
                ", messageType='" + messageType + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}