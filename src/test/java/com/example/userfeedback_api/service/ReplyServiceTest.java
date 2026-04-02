package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
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
public class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ReplyService replyService;

    @Test
    void shouldReturnAllReplies() {
        User user = new User();
        user.setUsername("benji");

        Post post = new Post();
        post.setTitle("Bouton cassé");

        Reply reply = new Reply();
        reply.setContent("Je confirme le bug");
        reply.setPublic(true);
        reply.setAuthor(user);
        reply.setPost(post);

        when(replyRepository.findAll()).thenReturn(List.of(reply));

        List<Reply> result = replyService.getAllReplies();

        assertEquals(1, result.size());
        assertEquals("Je confirme le bug", result.get(0).getContent());
    }

    @Test
    void shouldCreateReplySuccessfully() {
        User author = new User();
        author.setUsername("benji");

        Post post = new Post();
        post.setTitle("Bouton cassé");

        Reply savedReply = new Reply();
        savedReply.setContent("Je confirme le bug");
        savedReply.setPublic(true);
        savedReply.setAuthor(author);
        savedReply.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(replyRepository.save(ArgumentMatchers.any(Reply.class))).thenReturn(savedReply);

        Reply result = replyService.createReply(
                "Je confirme le bug",
                true,
                1L,
                1L
        );

        assertEquals("Je confirme le bug", result.getContent());
        assertEquals(true, result.isPublic());
        assertEquals("benji", result.getAuthor().getUsername());
        assertEquals("Bouton cassé", result.getPost().getTitle());
    }

    @Test
    void shouldThrowIfAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                replyService.createReply("Contenu", true, 1L, 1L)
        );

        assertEquals("Author not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfPostNotFound() {
        User author = new User();
        author.setUsername("benji");

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                replyService.createReply("Contenu", true, 1L, 1L)
        );

        assertEquals("Post not found", exception.getMessage());
    }
}