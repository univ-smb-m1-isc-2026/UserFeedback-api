package com.example.userfeedback_api.dto;

public class DeletePostRequest {

    private Long userId;

    public DeletePostRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}