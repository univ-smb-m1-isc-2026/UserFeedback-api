package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.entity.Post;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.entity.UserGroup;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.GroupMembershipRepository;
import com.example.userfeedback_api.repository.PostRepository;
import com.example.userfeedback_api.repository.UserGroupRepository;
import com.example.userfeedback_api.repository.UserRepository;
import com.example.userfeedback_api.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final VoteRepository voteRepository;
    private final CategoryRepository categoryRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       UserGroupRepository userGroupRepository,
                       GroupMembershipRepository groupMembershipRepository,
                       VoteRepository voteRepository,
                       CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.voteRepository = voteRepository;
        this.categoryRepository = categoryRepository;
    }

    public Post createPost(String title,
                           String content,
                           Long authorId,
                           boolean isPublic,
                           Long groupId,
                           Long categoryId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (title == null || title.isBlank()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (content == null || content.isBlank()) {
            throw new RuntimeException("Content cannot be empty");
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setPublic(isPublic);
        post.setEdited(false);
        post.setDeleted(false);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }

        if (!isPublic) {
            if (groupId == null) {
                throw new RuntimeException("Private post must have a group");
            }

            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            boolean isActiveMember = groupMembershipRepository
                    .findByUserIdAndGroupId(authorId, groupId)
                    .map(membership -> membership.isActive())
                    .orElse(false);

            if (!isActiveMember) {
                throw new RuntimeException("Author must be an active member of the group");
            }

            post.setGroup(group);
        } else {
            post.setGroup(null);
        }

        return postRepository.save(post);
    }

    public List<Post> getVisiblePosts(Long userId) {
        List<Post> allPosts = postRepository.findAll();

        return allPosts.stream()
                .filter(post -> canUserSeePost(userId, post))
                .sorted(Comparator.comparingInt((Post post) -> voteRepository.getPostScore(post.getId())).reversed())
                .toList();
    }

    public Post updatePost(Long postId, String title, String content, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isDeleted()) {
            throw new RuntimeException("Cannot edit a deleted post");
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Only the author can edit this post");
        }

        if (title == null || title.isBlank()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (content == null || content.isBlank()) {
            throw new RuntimeException("Content cannot be empty");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setEdited(true);

        return postRepository.save(post);
    }

    public Post deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isDeleted()) {
            throw new RuntimeException("Post is already deleted");
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this post");
        }

        post.setDeleted(true);

        return postRepository.save(post);
    }

    private boolean canUserSeePost(Long userId, Post post) {
        if (post.isDeleted()) {
            return false;
        }

        if (post.isPublic()) {
            return true;
        }

        if (post.getAuthor().getId().equals(userId)) {
            return true;
        }

        if (post.getGroup() == null) {
            return false;
        }

        return groupMembershipRepository
                .findByUserIdAndGroupId(userId, post.getGroup().getId())
                .map(membership -> membership.isActive())
                .orElse(false);
    }
}