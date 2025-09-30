package com.example.kafkastream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic Action DTO used for Use Case 3 - Routing and Division
 * Events will be routed to different topics based on actionType field
 */
public class GenericAction {
    
    @JsonProperty("actionType")
    private String actionType;
    
    @JsonProperty("details")
    private String details;

    public GenericAction() {}

    public GenericAction(String actionType, String details) {
        this.actionType = actionType;
        this.details = details;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "GenericAction{" +
                "actionType='" + actionType + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}