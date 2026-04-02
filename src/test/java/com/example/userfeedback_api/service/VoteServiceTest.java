package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.GroupMembership;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.entity.Vote;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @InjectMocks
    private VoteService voteService;

    @Test
    void shouldReturnAllVotes() {
        User user = new User();
        setId(user, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(user);

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
    void shouldCreateVoteForPublicPostSuccessfully() {
        User user = new User();
        setId(user, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(user);

        Vote savedVote = new Vote();
        savedVote.setValue(1);
        savedVote.setUser(user);
        savedVote.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(voteRepository.findByUserIdAndPostId(1L, 2L)).thenReturn(Optional.empty());
        when(voteRepository.save(ArgumentMatchers.any(Vote.class))).thenReturn(savedVote);

        Vote result = voteService.createVote(1, 1L, 2L, null);

        assertEquals(1, result.getValue());
        assertNull(result.getReply());
    }

    @Test
    void shouldToggleVoteOffOnSamePostVote() {
        User user = new User();
        setId(user, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(user);

        Vote existingVote = new Vote();
        existingVote.setValue(1);
        existingVote.setUser(user);
        existingVote.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(voteRepository.findByUserIdAndPostId(1L, 2L)).thenReturn(Optional.of(existingVote));

        Vote result = voteService.createVote(1, 1L, 2L, null);

        assertNull(result);
    }

    @Test
    void shouldSwitchVoteValueOnPost() {
        User user = new User();
        setId(user, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(user);

        Vote existingVote = new Vote();
        existingVote.setValue(1);
        existingVote.setUser(user);
        existingVote.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(voteRepository.findByUserIdAndPostId(1L, 2L)).thenReturn(Optional.of(existingVote));
        when(voteRepository.save(existingVote)).thenReturn(existingVote);

        Vote result = voteService.createVote(-1, 1L, 2L, null);

        assertEquals(-1, result.getValue());
    }

    @Test
    void shouldCreateVoteForPublicReplySuccessfully() {
        User user = new User();
        setId(user, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(user);

        Reply reply = new Reply();
        setId(reply, 3L);
        reply.setPublic(true);
        reply.setDeleted(false);
        reply.setAuthor(user);
        reply.setPost(post);

        Vote savedVote = new Vote();
        savedVote.setValue(-1);
        savedVote.setUser(user);
        savedVote.setReply(reply);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(replyRepository.findById(3L)).thenReturn(Optional.of(reply));
        when(voteRepository.findByUserIdAndReplyId(1L, 3L)).thenReturn(Optional.empty());
        when(voteRepository.save(ArgumentMatchers.any(Vote.class))).thenReturn(savedVote);

        Vote result = voteService.createVote(-1, 1L, null, 3L);

        assertEquals(-1, result.getValue());
        assertNull(result.getPost());
    }

    @Test
    void shouldThrowIfVoteValueIsInvalid() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voteService.createVote(2, 1L, 1L, null));

        assertEquals("Vote value must be 1 or -1", exception.getMessage());
    }

    @Test
    void shouldThrowIfTargetIsInvalid() {
        RuntimeException exception1 = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 1L, null, null));

        assertEquals("A vote must target either a post or a reply", exception1.getMessage());

        RuntimeException exception2 = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 1L, 1L, 1L));

        assertEquals("A vote must target either a post or a reply", exception2.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 1L, 2L, null));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfPostNotFound() {
        User user = new User();
        setId(user, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 1L, 2L, null));

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfReplyNotFound() {
        User user = new User();
        setId(user, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(replyRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 1L, null, 3L));

        assertEquals("Reply not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfUserCannotSeePrivatePost() {
        User author = new User();
        setId(author, 1L);

        User otherUser = new User();
        setId(otherUser, 2L);

        UserGroup group = new UserGroup();
        setId(group, 10L);

        Post post = new Post();
        setId(post, 3L);
        post.setPublic(false);
        post.setDeleted(false);
        post.setAuthor(author);
        post.setGroup(group);

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(3L)).thenReturn(Optional.of(post));
        when(groupMembershipRepository.findByUserIdAndGroupId(2L, 10L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voteService.createVote(1, 2L, 3L, null));

        assertEquals("User cannot vote on a post they cannot access", exception.getMessage());
    }

    @Test
    void shouldAllowUserToVoteOnPrivatePostWhenMember() {
        User author = new User();
        setId(author, 1L);

        User otherUser = new User();
        setId(otherUser, 2L);

        UserGroup group = new UserGroup();
        setId(group, 10L);

        Post post = new Post();
        setId(post, 3L);
        post.setPublic(false);
        post.setDeleted(false);
        post.setAuthor(author);
        post.setGroup(group);

        GroupMembership membership = new GroupMembership();
        membership.setActive(true);

        Vote savedVote = new Vote();
        savedVote.setValue(1);
        savedVote.setUser(otherUser);
        savedVote.setPost(post);

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(3L)).thenReturn(Optional.of(post));
        when(groupMembershipRepository.findByUserIdAndGroupId(2L, 10L)).thenReturn(Optional.of(membership));
        when(voteRepository.findByUserIdAndPostId(2L, 3L)).thenReturn(Optional.empty());
        when(voteRepository.save(ArgumentMatchers.any(Vote.class))).thenReturn(savedVote);

        Vote result = voteService.createVote(1, 2L, 3L, null);

        assertEquals(1, result.getValue());
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