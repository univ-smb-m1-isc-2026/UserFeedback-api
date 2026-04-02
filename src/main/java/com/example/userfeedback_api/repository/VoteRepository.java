package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndPostId(Long userId, Long postId);
    Optional<Vote> findByUserIdAndReplyId(Long userId, Long replyId);

    List<Vote> findByPostId(Long postId);
    List<Vote> findByReplyId(Long replyId);

    @Query("SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.post.id = :postId")
    int getPostScore(@Param("postId") Long postId);

    @Query("SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.reply.id = :replyId")
    int getReplyScore(@Param("replyId") Long replyId);
}