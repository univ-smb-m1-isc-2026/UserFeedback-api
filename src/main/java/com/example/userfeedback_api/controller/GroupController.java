package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.AddGroupMemberRequest;
import com.example.userfeedback_api.dto.CreateGroupRequest;
import com.example.userfeedback_api.entity.GroupMembership;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public UserGroup createGroup(@RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request.getName(), request.getOwnerId());
    }

    @GetMapping("/user/{userId}")
    public List<GroupMembership> getUserGroups(@PathVariable Long userId) {
        return groupService.getUserGroups(userId);
    }

    @GetMapping("/search-users")
    public List<User> searchUsers(@RequestParam String query) {
        return groupService.searchUsers(query);
    }

    @PostMapping("/{groupId}/members")
    public GroupMembership addUserToGroup(@PathVariable Long groupId,
                                          @RequestBody AddGroupMemberRequest request) {
        return groupService.addUserToGroup(groupId, request.getUserId());
    }

    @PostMapping("/{groupId}/leave/{userId}")
    public void leaveGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.leaveGroup(groupId, userId);
    }
}