package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Create Chat Event DTO - Generated from InboundMessageEvent
 * Represents the creation of a new chat session
 */
public class CreateChatEvent {
    
    @JsonProperty("chat_id")
    private String chatId;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("user_phone")
    private String userPhone;
    
    @JsonProperty("country_code")
    private String countryCode;
    
    @JsonProperty("dial_code")
    private String dialCode;
    
    @JsonProperty("created_at")
    private Long createdAt;
    
    public CreateChatEvent() {}
    
    public CreateChatEvent(String chatId, String userName, String userPhone, String countryCode, String dialCode, Long createdAt) {
        this.chatId = chatId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.countryCode = countryCode;
        this.dialCode = dialCode;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "CreateChatEvent{" +
                "chatId='" + chatId + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", dialCode='" + dialCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}