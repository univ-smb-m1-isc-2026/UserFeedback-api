package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
}