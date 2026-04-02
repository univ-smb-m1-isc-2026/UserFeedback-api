package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldReturnAllPosts() {
        User user = new User();
        user.setUsername("benji");
        user.setEmail("benji@test.com");
        user.setPassword("1234");
        user.setRole("USER");

        Category category = new Category();
        category.setTitle("Bug");
        category.setDescription("Signalement de bug");

        Post post = new Post();
        post.setTitle("Bouton cassé");
        post.setContent("Le bouton ne répond pas");
        post.setPublic(false);
        post.setAuthor(user);

        when(postRepository.findAll()).thenReturn(List.of(post));

        List<Post> result = postService.getAllPosts();

        assertEquals(1, result.size());
        assertEquals("Bouton cassé", result.get(0).getTitle());
    }

    @Test
    void shouldCreatePostSuccessfully() {
        User author = new User();
        author.setUsername("benji");
        author.setEmail("benji@test.com");
        author.setPassword("1234");
        author.setRole("USER");

        Category category = new Category();
        category.setTitle("Bug");
        category.setDescription("Signalement de bug");

        Post savedPost = new Post();
        savedPost.setTitle("Bouton cassé");
        savedPost.setContent("Le bouton ne répond pas");
        savedPost.setPublic(true);
        savedPost.setAuthor(author);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

        Post result = postService.createPost(
                "Bouton cassé",
                "Le bouton ne répond pas",
                true,
                1L
        );

        assertEquals("Bouton cassé", result.getTitle());
        assertEquals(true, result.isPublic());
        assertEquals("benji", result.getAuthor().getUsername());
    }

    @Test
    void shouldThrowIfAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                postService.createPost("Titre", "Contenu", true, 1L)
        );

        assertEquals("Author not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfCategoryNotFound() {
        User author = new User();
        author.setUsername("benji");

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                postService.createPost("Titre", "Contenu", true, 1L)
        );

        assertEquals("Category not found", exception.getMessage());
    }
}