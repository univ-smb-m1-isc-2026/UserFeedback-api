package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.GroupMembership;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
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
public class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ReplyService replyService;

    @Test
    void shouldCreatePublicReplySuccessfully() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(author);

        Reply savedReply = new Reply();
        setId(savedReply, 10L);
        savedReply.setContent("Je confirme le bug");
        savedReply.setPublic(true);
        savedReply.setAuthor(author);
        savedReply.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(replyRepository.save(ArgumentMatchers.any(Reply.class))).thenReturn(savedReply);

        Reply result = replyService.createReply("Je confirme le bug", true, null, 1L, 2L);

        assertEquals("Je confirme le bug", result.getContent());
        assertEquals(true, result.isPublic());
    }

    @Test
    void shouldCreatePrivateReplySuccessfully() {
        User author = new User();
        setId(author, 1L);

        UserGroup group = new UserGroup();
        setId(group, 5L);
        group.setName("Groupe 1");

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(false);
        post.setDeleted(false);
        post.setAuthor(author);
        post.setGroup(group);

        GroupMembership membership = new GroupMembership();
        membership.setActive(true);

        Reply savedReply = new Reply();
        setId(savedReply, 10L);
        savedReply.setContent("Réponse privée");
        savedReply.setPublic(false);
        savedReply.setAuthor(author);
        savedReply.setPost(post);
        savedReply.setGroup(group);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(userGroupRepository.findById(5L)).thenReturn(Optional.of(group));
        when(groupMembershipRepository.findByUserIdAndGroupId(1L, 5L)).thenReturn(Optional.of(membership));
        when(replyRepository.save(ArgumentMatchers.any(Reply.class))).thenReturn(savedReply);

        Reply result = replyService.createReply("Réponse privée", false, 5L, 1L, 2L);

        assertEquals("Réponse privée", result.getContent());
        assertEquals(false, result.isPublic());
        assertEquals("Groupe 1", result.getGroup().getName());
    }

    @Test
    void shouldThrowIfPrivateReplyHasNoGroup() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(author);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> replyService.createReply("Contenu", false, null, 1L, 2L));

        assertEquals("Private reply must have a group", exception.getMessage());
    }

    @Test
    void shouldThrowIfReplyToPrivatePostIsPublic() {
        User author = new User();
        setId(author, 1L);

        UserGroup group = new UserGroup();
        setId(group, 5L);

        Post post = new Post();
        setId(post, 2L);
        post.setPublic(false);
        post.setDeleted(false);
        post.setAuthor(author);
        post.setGroup(group);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> replyService.createReply("Contenu", true, null, 1L, 2L));

        assertEquals("A reply to a private post must also be private", exception.getMessage());
    }

    @Test
    void shouldReturnVisibleRepliesSortedByScore() {
        User author = new User();
        setId(author, 1L);

        Post post = new Post();
        setId(post, 1L);
        post.setPublic(true);
        post.setDeleted(false);
        post.setAuthor(author);

        Reply reply1 = new Reply();
        setId(reply1, 10L);
        reply1.setContent("Reply 1");
        reply1.setPublic(true);
        reply1.setDeleted(false);
        reply1.setAuthor(author);
        reply1.setPost(post);

        Reply reply2 = new Reply();
        setId(reply2, 20L);
        reply2.setContent("Reply 2");
        reply2.setPublic(true);
        reply2.setDeleted(false);
        reply2.setAuthor(author);
        reply2.setPost(post);

        when(replyRepository.findByPostId(1L)).thenReturn(List.of(reply1, reply2));
        when(voteRepository.getReplyScore(10L)).thenReturn(1);
        when(voteRepository.getReplyScore(20L)).thenReturn(4);

        List<Reply> result = replyService.getVisibleReplies(1L, 1L);

        assertEquals(2, result.size());
        assertEquals("Reply 2", result.get(0).getContent());
        assertEquals("Reply 1", result.get(1).getContent());
    }

    @Test
    void shouldUpdateReply() {
        User author = new User();
        setId(author, 1L);

        Reply reply = new Reply();
        setId(reply, 10L);
        reply.setAuthor(author);
        reply.setDeleted(false);
        reply.setContent("Old");

        when(replyRepository.findById(1L)).thenReturn(Optional.of(reply));
        when(replyRepository.save(reply)).thenReturn(reply);

        Reply result = replyService.updateReply(1L, "New", 1L);

        assertEquals("New", result.getContent());
        assertEquals(true, result.isEdited());
    }

    @Test
    void shouldDeleteReply() {
        User author = new User();
        setId(author, 1L);

        Reply reply = new Reply();
        setId(reply, 10L);
        reply.setAuthor(author);
        reply.setDeleted(false);

        when(replyRepository.findById(1L)).thenReturn(Optional.of(reply));
        when(replyRepository.save(reply)).thenReturn(reply);

        Reply result = replyService.deleteReply(1L, 1L);

        assertEquals(true, result.isDeleted());
    }

    @Test
    void shouldThrowIfAnotherUserTriesToDeleteReply() {
        User author = new User();
        setId(author, 1L);

        Reply reply = new Reply();
        setId(reply, 10L);
        reply.setAuthor(author);
        reply.setDeleted(false);

        when(replyRepository.findById(1L)).thenReturn(Optional.of(reply));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> replyService.deleteReply(1L, 2L));

        assertEquals("Only the author can delete this reply", exception.getMessage());
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