package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.CreatePostRequest;
import com.example.userfeedback_api.dto.DeletePostRequest;
import com.example.userfeedback_api.dto.UpdatePostRequest;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/visible/{userId}")
    public List<Post> getVisiblePosts(@PathVariable Long userId) {
        return postService.getVisiblePosts(userId);
    }

    @PostMapping
    public Post createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(
                request.getTitle(),
                request.getContent(),
                request.getAuthorId(),
                request.isPublic(),
                request.getGroupId(),
                request.getCategoryId()
        );
    }

    @PutMapping("/{postId}")
    public Post updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequest request) {
        return postService.updatePost(
                postId,
                request.getTitle(),
                request.getContent(),
                request.getUserId()
        );
    }

    @PutMapping("/{postId}/delete")
    public Post deletePost(@PathVariable Long postId, @RequestBody DeletePostRequest request) {
        return postService.deletePost(postId, request.getUserId());
    }
}