package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.UpdateCategoryRequest;
import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category, @RequestParam Long userId) {
        return categoryService.createCategory(category, userId);
    }

    @PutMapping("/{categoryId}")
    public Category updateCategory(@PathVariable Long categoryId,
                                   @RequestBody UpdateCategoryRequest request) {
        return categoryService.updateCategory(
                categoryId,
                request.getTitle(),
                request.getDescription(),
                request.getUserId()
        );
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId,
                              @RequestParam Long userId) {
        categoryService.deleteCategory(categoryId, userId);
    }
}