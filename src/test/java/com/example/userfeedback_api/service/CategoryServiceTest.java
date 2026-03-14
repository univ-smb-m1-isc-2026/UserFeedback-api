package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnAllCategories() {
        Category c1 = new Category();
        c1.setTitle("Bug");
        c1.setDescription("Signalement de bug");

        Category c2 = new Category();
        c2.setTitle("Idea");
        c2.setDescription("Nouvelle idée");

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Bug", result.get(0).getTitle());
        assertEquals("Idea", result.get(1).getTitle());
    }

    @Test
    void shouldCreateCategory() {
        Category category = new Category();
        category.setTitle("Feature");
        category.setDescription("Demande de fonctionnalité");

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertEquals("Feature", result.getTitle());
        assertEquals("Demande de fonctionnalité", result.getDescription());
    }
}