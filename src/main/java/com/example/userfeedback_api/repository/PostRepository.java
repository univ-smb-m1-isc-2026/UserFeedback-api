package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}