package com.example.userfeedback_api.dto;

public class DeleteReplyRequest {

    private Long userId;

    public DeleteReplyRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}