package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category, Long userId) {
        User user = getAdmin(userId);

        if (category.getTitle() == null || category.getTitle().isBlank()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (category.getDescription() == null || category.getDescription().isBlank()) {
            throw new RuntimeException("Description cannot be empty");
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long categoryId, String title, String description, Long userId) {
        User user = getAdmin(userId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (title == null || title.isBlank()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (description == null || description.isBlank()) {
            throw new RuntimeException("Description cannot be empty");
        }

        category.setTitle(title);
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        User user = getAdmin(userId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

    private User getAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Only admin can perform this action");
        }

        return user;
    }
}