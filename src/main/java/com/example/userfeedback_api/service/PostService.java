package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post createPost(String title, String content, String visibility, Long authorId, Long categoryId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setVisibility(visibility);
        post.setAuthor(author);
        post.setCategory(category);

        return postRepository.save(post);
    }
}