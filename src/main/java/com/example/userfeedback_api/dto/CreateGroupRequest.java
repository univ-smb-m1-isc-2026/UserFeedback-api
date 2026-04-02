package com.example.userfeedback_api.dto;

public class CreateGroupRequest {

    private String name;
    private Long ownerId;

    public CreateGroupRequest() {
    }

    public String getName() {
        return name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}