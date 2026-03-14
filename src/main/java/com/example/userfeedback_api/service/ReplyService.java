package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.ReplyRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ReplyService(ReplyRepository replyRepository, UserRepository userRepository, PostRepository postRepository) {
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<Reply> getAllReplies() {
        return replyRepository.findAll();
    }

    public Reply createReply(String content, String visibility, Long authorId, Long postId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Reply reply = new Reply();
        reply.setContent(content);
        reply.setVisibility(visibility);
        reply.setAuthor(author);
        reply.setPost(post);

        return replyRepository.save(reply);
    }
}