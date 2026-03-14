package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.CreatePostRequest;
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

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping
    public Post createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(
                request.getTitle(),
                request.getContent(),
                request.getVisibility(),
                request.getAuthorId(),
                request.getCategoryId()
        );
    }
}