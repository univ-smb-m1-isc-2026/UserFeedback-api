package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.GroupMembership;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
import com.example.userfeedback_api.repository.UserGroupRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupService(UserGroupRepository userGroupRepository,
                        UserRepository userRepository,
                        GroupMembershipRepository groupMembershipRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public UserGroup createGroup(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        UserGroup group = new UserGroup();
        group.setName(name);
        group.setOwner(owner);

        UserGroup savedGroup = userGroupRepository.save(group);

        GroupMembership membership = new GroupMembership();
        membership.setUser(owner);
        membership.setGroup(savedGroup);
        membership.setActive(true);
        membership.setHasLeft(false);

        groupMembershipRepository.save(membership);

        return savedGroup;
    }

    public List<GroupMembership> getUserGroups(Long userId) {
        return groupMembershipRepository.findByUserIdAndActiveTrue(userId);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public GroupMembership addUserToGroup(Long groupId, Long userId) {
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMembership existingMembership = groupMembershipRepository.findByUserIdAndGroupId(userId, groupId)
                .orElse(null);

        if (existingMembership != null) {
            if (existingMembership.isHasLeft()) {
                throw new RuntimeException("User cannot rejoin this group");
            }

            if (existingMembership.isActive()) {
                throw new RuntimeException("User is already in this group");
            }
        }

        GroupMembership membership = new GroupMembership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setActive(true);
        membership.setHasLeft(false);

        return groupMembershipRepository.save(membership);
    }

    public void leaveGroup(Long groupId, Long userId) {
        GroupMembership membership = groupMembershipRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        membership.setActive(false);
        membership.setHasLeft(true);

        groupMembershipRepository.save(membership);
    }
}