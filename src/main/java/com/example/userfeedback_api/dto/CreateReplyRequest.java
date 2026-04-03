package com.example.userfeedback_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateReplyRequest {

    private String content;

    @JsonProperty("isPublic")
    private boolean isPublic;

    private Long groupId;
    private Long authorId;
    private Long postId;

    public CreateReplyRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @JsonProperty("isPublic")
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}