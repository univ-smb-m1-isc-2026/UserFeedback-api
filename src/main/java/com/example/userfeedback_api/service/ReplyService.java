package com.example.userfeedback_api.service;

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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final VoteRepository voteRepository;

    public ReplyService(ReplyRepository replyRepository,
                        UserRepository userRepository,
                        PostRepository postRepository,
                        UserGroupRepository userGroupRepository,
                        GroupMembershipRepository groupMembershipRepository,
                        VoteRepository voteRepository) {
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.voteRepository = voteRepository;
    }

    public List<Reply> getVisibleReplies(Long userId, Long postId) {
        List<Reply> replies = replyRepository.findByPostId(postId);

        return replies.stream()
                .filter(reply -> canUserSeeReply(userId, reply))
                .sorted(Comparator.comparingInt((Reply reply) -> voteRepository.getReplyScore(reply.getId())).reversed())
                .toList();
    }

    public Reply createReply(String content, boolean isPublic, Long groupId, Long authorId, Long postId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isDeleted()) {
            throw new RuntimeException("Cannot reply to a deleted post");
        }

        if (!canUserSeePost(authorId, post)) {
            throw new RuntimeException("Author cannot reply to a post they cannot access");
        }

        Reply reply = new Reply();
        reply.setContent(content);
        reply.setAuthor(author);
        reply.setPost(post);
        reply.setEdited(false);
        reply.setDeleted(false);

        if (!post.isPublic() && isPublic) {
            throw new RuntimeException("A reply to a private post must also be private");
        }

        if (!isPublic) {
            if (groupId == null) {
                throw new RuntimeException("Private reply must have a group");
            }

            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            boolean isActiveMember = groupMembershipRepository
                    .findByUserIdAndGroupId(authorId, groupId)
                    .map(membership -> membership.isActive())
                    .orElse(false);

            if (!isActiveMember) {
                throw new RuntimeException("Author must be an active member of the group");
            }

            if (!post.isPublic() && post.getGroup() != null && !post.getGroup().getId().equals(groupId)) {
                throw new RuntimeException("A private reply on a private post must use the same group as the post");
            }

            reply.setPublic(false);
            reply.setGroup(group);
        } else {
            reply.setPublic(true);
            reply.setGroup(null);
        }

        return replyRepository.save(reply);
    }

    public Reply updateReply(Long replyId, String content, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (reply.isDeleted()) {
            throw new RuntimeException("Cannot edit a deleted reply");
        }

        if (!reply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Only the author can edit this reply");
        }

        if (content == null || content.isBlank()) {
            throw new RuntimeException("Content cannot be empty");
        }

        reply.setContent(content);
        reply.setEdited(true);

        return replyRepository.save(reply);
    }

    public Reply deleteReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (reply.isDeleted()) {
            throw new RuntimeException("Reply is already deleted");
        }

        if (!reply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this reply");
        }

        reply.setDeleted(true);

        return replyRepository.save(reply);
    }

    private boolean canUserSeeReply(Long userId, Reply reply) {
        if (reply.isDeleted()) {
            return false;
        }

        if (!canUserSeePost(userId, reply.getPost())) {
            return false;
        }

        if (reply.isPublic()) {
            return true;
        }

        if (reply.getAuthor().getId().equals(userId)) {
            return true;
        }

        if (reply.getGroup() == null) {
            return false;
        }

        return groupMembershipRepository
                .findByUserIdAndGroupId(userId, reply.getGroup().getId())
                .map(membership -> membership.isActive())
                .orElse(false);
    }

    private boolean canUserSeePost(Long userId, Post post) {
        if (post.isDeleted()) {
            return false;
        }

        if (post.isPublic()) {
            return true;
        }

        if (post.getAuthor().getId().equals(userId)) {
            return true;
        }

        if (post.getGroup() == null) {
            return false;
        }

        return groupMembershipRepository
                .findByUserIdAndGroupId(userId, post.getGroup().getId())
                .map(membership -> membership.isActive())
                .orElse(false);
    }
}