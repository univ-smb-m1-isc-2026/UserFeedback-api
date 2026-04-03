package com.example.userfeedback_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePostRequest {

    private String title;
    private String content;

    @JsonProperty("isPublic")
    private boolean publicFlag;

    private Long groupId;
    private Long categoryId;
    private Long authorId;

    public CreatePostRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublic() {
        return publicFlag;
    }

    @JsonProperty("isPublic")
    public void setPublic(boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}