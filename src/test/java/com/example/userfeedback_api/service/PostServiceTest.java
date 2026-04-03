package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.GroupMembership;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.UserGroupRepository;
import com.example.userfeedback_api.repository.UserRepository;
import com.example.userfeedback_api.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldCreatePublicPostSuccessfully() {
        User author = new User();
        setId(author, 1L);
        author.setUsername("benji");

        Post savedPost = new Post();
        setId(savedPost, 10L);
        savedPost.setTitle("Bouton cassé");
        savedPost.setContent("Le bouton ne répond pas");
        savedPost.setPublic(true);
        savedPost.setAuthor(author);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

        Post result = postService.createPost(
                "Bouton cassé",
                "Le bouton ne répond pas",
                1L,
                true,
                null,
                null
        );

        assertEquals("Bouton cassé", result.getTitle());
        assertEquals(true, result.isPublic());
        assertEquals("benji", result.getAuthor().getUsername());
    }

    @Test
    void shouldCreatePrivatePostSuccessfullyWhenAuthorIsMember() {
        User author = new User();
        setId(author, 1L);
        author.setUsername("benji");

        UserGroup group = new UserGroup();
        setId(group, 2L);
        group.setName("Groupe 1");

        GroupMembership membership = new GroupMembership();
        membership.setActive(true);

        Post savedPost = new Post();
        setId(savedPost, 11L);
        savedPost.setTitle("Privé");
        savedPost.setContent("Contenu privé");
        savedPost.setPublic(false);
        savedPost.setAuthor(author);
        savedPost.setGroup(group);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.findByUserIdAndGroupId(1L, 2L)).thenReturn(Optional.of(membership));
        when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

        Post result = postService.createPost("Privé", "Contenu privé", 1L, false, 2L, null);

        assertEquals("Privé", result.getTitle());
        assertEquals(false, result.isPublic());
        assertEquals("Groupe 1", result.getGroup().getName());
    }

    @Test
    void shouldThrowIfPrivatePostHasNoGroup() {
        User author = new User();
        setId(author, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.createPost("Titre", "Contenu", 1L, false, null, null));

        assertEquals("Private post must have a group", exception.getMessage());
    }

    @Test
    void shouldThrowIfAuthorIsNotActiveMemberForPrivatePost() {
        User author = new User();
        setId(author, 1L);

        UserGroup group = new UserGroup();
        setId(group, 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.findByUserIdAndGroupId(1L, 2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.createPost("Titre", "Contenu", 1L, false, 2L, null));

        assertEquals("Author must be an active member of the group", exception.getMessage());
    }

    @Test
    void shouldReturnVisiblePostsSortedByScore() {
        User author = new User();
        setId(author, 1L);
        author.setUsername("benji");

        UserGroup group = new UserGroup();
        setId(group, 100L);

        Post publicPost = new Post();
        setId(publicPost, 10L);
        publicPost.setTitle("Public");
        publicPost.setPublic(true);
        publicPost.setDeleted(false);
        publicPost.setAuthor(author);

        Post privatePost = new Post();
        setId(privatePost, 20L);
        privatePost.setTitle("Private");
        privatePost.setPublic(false);
        privatePost.setDeleted(false);
        privatePost.setAuthor(author);
        privatePost.setGroup(group);

        when(postRepository.findAll()).thenReturn(List.of(publicPost, privatePost));
        when(voteRepository.getPostScore(10L)).thenReturn(1);
        when(voteRepository.getPostScore(20L)).thenReturn(5);

        List<Post> result = postService.getVisiblePosts(1L);

        assertEquals(2, result.size());
        assertEquals("Private", result.get(0).getTitle());
        assertEquals("Public", result.get(1).getTitle());
    }

    @Test
    void shouldUpdatePost() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 10L);
        post.setAuthor(author);
        post.setDeleted(false);
        post.setTitle("Old title");
        post.setContent("Old content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.updatePost(1L, "New title", "New content", 1L);

        assertEquals("New title", result.getTitle());
        assertEquals("New content", result.getContent());
        assertEquals(true, result.isEdited());
    }

    @Test
    void shouldDeletePost() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 10L);
        post.setAuthor(author);
        post.setDeleted(false);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.deletePost(1L, 1L);

        assertEquals(true, result.isDeleted());
    }

    @Test
    void shouldThrowIfAnotherUserTriesToEditPost() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 10L);
        post.setAuthor(author);
        post.setDeleted(false);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.updatePost(1L, "Titre", "Contenu", 2L));

        assertEquals("Only the author can edit this post", exception.getMessage());
    }

    private void setId(Object target, Long value) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}