package com.example.userfeedback_api.dto;

public class AddGroupMemberRequest {

    private Long userId;

    public AddGroupMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}