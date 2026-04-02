package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.Vote;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
import com.example.userfeedback_api.repository.UserRepository;
import com.example.userfeedback_api.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public VoteService(VoteRepository voteRepository,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       ReplyRepository replyRepository,
                       GroupMembershipRepository groupMembershipRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public Vote createVote(Integer value, Long userId, Long postId, Long replyId) {
        if (value == null || (value != 1 && value != -1)) {
            throw new RuntimeException("Vote value must be 1 or -1");
        }

        if ((postId == null && replyId == null) || (postId != null && replyId != null)) {
            throw new RuntimeException("A vote must target either a post or a reply");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (postId != null) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!canUserSeePost(userId, post)) {
                throw new RuntimeException("User cannot vote on a post they cannot access");
            }

            Optional<Vote> existingVoteOpt = voteRepository.findByUserIdAndPostId(userId, postId);

            if (existingVoteOpt.isPresent()) {
                Vote existingVote = existingVoteOpt.get();

                if (existingVote.getValue().equals(value)) {
                    voteRepository.delete(existingVote);
                    return null;
                }

                existingVote.setValue(value);
                return voteRepository.save(existingVote);
            }

            Vote vote = new Vote();
            vote.setValue(value);
            vote.setUser(user);
            vote.setPost(post);
            vote.setReply(null);

            return voteRepository.save(vote);
        }

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!canUserSeeReply(userId, reply)) {
            throw new RuntimeException("User cannot vote on a reply they cannot access");
        }

        Optional<Vote> existingVoteOpt = voteRepository.findByUserIdAndReplyId(userId, replyId);

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();

            if (existingVote.getValue().equals(value)) {
                voteRepository.delete(existingVote);
                return null;
            }

            existingVote.setValue(value);
            return voteRepository.save(existingVote);
        }

        Vote vote = new Vote();
        vote.setValue(value);
        vote.setUser(user);
        vote.setPost(null);
        vote.setReply(reply);

        return voteRepository.save(vote);
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
}