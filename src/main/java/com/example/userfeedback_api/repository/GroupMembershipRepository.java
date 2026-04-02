package com.example.userfeedback_api.repository;

import com.example.userfeedback_api.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    List<GroupMembership> findByUserIdAndActiveTrue(Long userId);
    List<GroupMembership> findByGroupIdAndActiveTrue(Long groupId);
    Optional<GroupMembership> findByUserIdAndGroupId(Long userId, Long groupId);
}