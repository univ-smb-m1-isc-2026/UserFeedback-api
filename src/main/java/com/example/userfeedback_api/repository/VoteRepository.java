package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}