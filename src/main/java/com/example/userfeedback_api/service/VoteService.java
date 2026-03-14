package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.Vote;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
import com.example.userfeedback_api.repository.UserRepository;
import com.example.userfeedback_api.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public VoteService(VoteRepository voteRepository,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       ReplyRepository replyRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
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

        Vote vote = new Vote();
        vote.setValue(value);
        vote.setUser(user);

        if (postId != null) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            vote.setPost(post);
        }

        if (replyId != null) {
            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new RuntimeException("Reply not found"));
            vote.setReply(reply);
        }

        return voteRepository.save(vote);
    }
}