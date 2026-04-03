package com.example.userfeedback_api.service;

import com.example.userfeedback_api.entity.Category;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.CategoryRepository;
import com.example.userfeedback_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnAllCategories() {
        Category c1 = new Category();
        setId(c1, 1L);
        c1.setTitle("Bug");
        c1.setDescription("Signalement de bug");

        Category c2 = new Category();
        setId(c2, 2L);
        c2.setTitle("Idea");
        c2.setDescription("Nouvelle idee");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Bug", result.get(0).getTitle());
        assertEquals("Idea", result.get(1).getTitle());
    }

    @Test
    void shouldCreateCategoryWhenUserIsAdmin() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        category.setTitle("Feature");
        category.setDescription("Demande de fonctionnalite");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category, 1L);

        assertEquals("Feature", result.getTitle());
        assertEquals("Demande de fonctionnalite", result.getDescription());
    }

    @Test
    void shouldUpdateCategoryWhenUserIsAdmin() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        setId(category, 2L);
        category.setTitle("Old");
        category.setDescription("Old desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.updateCategory(2L, "New", "New desc", 1L);

        assertEquals("New", result.getTitle());
        assertEquals("New desc", result.getDescription());
    }

    @Test
    void shouldDeleteCategoryWhenUserIsAdmin() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        setId(category, 2L);
        category.setTitle("Bug");
        category.setDescription("Desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(2L, 1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowIfUserIsNotAdminOnCreate() {
        User user = new User();
        setId(user, 1L);
        user.setRole("USER");

        Category category = new Category();
        category.setTitle("Feature");
        category.setDescription("Desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(category, 1L));

        assertEquals("Only admin can perform this action", exception.getMessage());
    }

    @Test
    void shouldThrowIfTitleIsEmptyOnCreate() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        category.setTitle(" ");
        category.setDescription("Desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(category, 1L));

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowIfDescriptionIsEmptyOnCreate() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        category.setTitle("Bug");
        category.setDescription(" ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(category, 1L));

        assertEquals("Description cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowIfCategoryNotFoundOnUpdate() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(99L, "New", "New desc", 1L));

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfTitleIsEmptyOnUpdate() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        setId(category, 2L);
        category.setTitle("Old");
        category.setDescription("Old desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(2L, " ", "New desc", 1L));

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowIfDescriptionIsEmptyOnUpdate() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        Category category = new Category();
        setId(category, 2L);
        category.setTitle("Old");
        category.setDescription("Old desc");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(2L, "New", " ", 1L));

        assertEquals("Description cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowIfCategoryNotFoundOnDelete() {
        User admin = new User();
        setId(admin, 1L);
        admin.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.deleteCategory(99L, 1L));

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Category category = new Category();
        category.setTitle("Bug");
        category.setDescription("Desc");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.createCategory(category, 1L));

        assertEquals("User not found", exception.getMessage());
    }

    private void setId(Object target, Long value) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}