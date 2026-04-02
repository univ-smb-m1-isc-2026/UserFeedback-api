package com.example.userfeedback_api.dto;

public class UpdateReplyRequest {

    private String content;
    private Long userId;

    public UpdateReplyRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}