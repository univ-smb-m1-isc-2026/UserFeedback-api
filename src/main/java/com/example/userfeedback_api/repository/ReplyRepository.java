package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}