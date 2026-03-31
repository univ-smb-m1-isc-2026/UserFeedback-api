package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndPostId(Long userId, Long postId);
    Optional<Vote> findByUserIdAndReplyId(Long userId, Long replyId);
    List<Vote> findByPostId(Long postId);
    List<Vote> findByReplyId(Long replyId);
}