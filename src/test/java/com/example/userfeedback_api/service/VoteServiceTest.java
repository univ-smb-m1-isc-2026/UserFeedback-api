package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.Vote;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
import com.example.userfeedback_api.repository.UserRepository;
import com.example.userfeedback_api.repository.VoteRepository;
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
public class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private VoteService voteService;

    @Test
    void shouldReturnAllVotes() {
        User user = new User();
        user.setUsername("benji");

        Post post = new Post();
        post.setTitle("Bouton cassé");

        Vote vote = new Vote();
        vote.setValue(1);
        vote.setUser(user);
        vote.setPost(post);

        when(voteRepository.findAll()).thenReturn(List.of(vote));

        List<Vote> result = voteService.getAllVotes();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getValue());
    }

    @Test
    void shouldCreateVoteForPostSuccessfully() {
        User user = new User();
        user.setUsername("benji");

        Post post = new Post();
        post.setTitle("Bouton cassé");

        Vote savedVote = new Vote();
        savedVote.setValue(1);
        savedVote.setUser(user);
        savedVote.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(voteRepository.save(ArgumentMatchers.any(Vote.class))).thenReturn(savedVote);

        Vote result = voteService.createVote(1, 1L, 1L, null);

        assertEquals(1, result.getValue());
        assertEquals("benji", result.getUser().getUsername());
        assertEquals("Bouton cassé", result.getPost().getTitle());
        assertNull(result.getReply());
    }

    @Test
    void shouldCreateVoteForReplySuccessfully() {
        User user = new User();
        user.setUsername("benji");

        Reply reply = new Reply();
        reply.setContent("Je confirme");

        Vote savedVote = new Vote();
        savedVote.setValue(-1);
        savedVote.setUser(user);
        savedVote.setReply(reply);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(replyRepository.findById(1L)).thenReturn(Optional.of(reply));
        when(voteRepository.save(ArgumentMatchers.any(Vote.class))).thenReturn(savedVote);

        Vote result = voteService.createVote(-1, 1L, null, 1L);

        assertEquals(-1, result.getValue());
        assertEquals("benji", result.getUser().getUsername());
        assertEquals("Je confirme", result.getReply().getContent());
        assertNull(result.getPost());
    }

    @Test
    void shouldThrowIfVoteValueIsInvalid() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                voteService.createVote(2, 1L, 1L, null)
        );

        assertEquals("Vote value must be 1 or -1", exception.getMessage());
    }

    @Test
    void shouldThrowIfTargetIsInvalid() {
        RuntimeException exception1 = assertThrows(RuntimeException.class, () ->
                voteService.createVote(1, 1L, null, null)
        );

        assertEquals("A vote must target either a post or a reply", exception1.getMessage());

        RuntimeException exception2 = assertThrows(RuntimeException.class, () ->
                voteService.createVote(1, 1L, 1L, 1L)
        );

        assertEquals("A vote must target either a post or a reply", exception2.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                voteService.createVote(1, 1L, 1L, null)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfPostNotFound() {
        User user = new User();
        user.setUsername("benji");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                voteService.createVote(1, 1L, 1L, null)
        );

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfReplyNotFound() {
        User user = new User();
        user.setUsername("benji");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(replyRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                voteService.createVote(1, 1L, null, 1L)
        );

        assertEquals("Reply not found", exception.getMessage());
    }
}