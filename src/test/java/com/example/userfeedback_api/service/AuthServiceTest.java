package com.example.userfeedback_api.service;

import com.example.userfeedback_api.dto.LoginRequest;
import com.example.userfeedback_api.dto.RegisterRequest;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("benji");
        request.setEmail("benji@test.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("benji@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("benji")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setUsername("benji");
        savedUser.setEmail("benji@test.com");
        savedUser.setPassword("1234");
        savedUser.setRole("USER");

        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        User result = authService.register(request);

        assertEquals("benji", result.getUsername());
        assertEquals("benji@test.com", result.getEmail());
        assertEquals("USER", result.getRole());
    }

    @Test
    void shouldThrowIfEmailAlreadyUsed() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("benji");
        request.setEmail("benji@test.com");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setEmail("benji@test.com");

        when(userRepository.findByEmail("benji@test.com")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));

        assertEquals("Email already used", exception.getMessage());
    }

    @Test
    void shouldThrowIfUsernameAlreadyUsed() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("benji");
        request.setEmail("benji@test.com");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setUsername("benji");

        when(userRepository.findByEmail("benji@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("benji")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));

        assertEquals("Username already used", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("benji@test.com");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setUsername("benji");
        existingUser.setEmail("benji@test.com");
        existingUser.setPassword("1234");
        existingUser.setRole("USER");

        when(userRepository.findByEmail("benji@test.com")).thenReturn(Optional.of(existingUser));

        User result = authService.login(request);

        assertEquals("benji", result.getUsername());
        assertEquals("benji@test.com", result.getEmail());
    }

    @Test
    void shouldThrowIfPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("benji@test.com");
        request.setPassword("wrong-password");

        User existingUser = new User();
        existingUser.setEmail("benji@test.com");
        existingUser.setPassword("1234");

        when(userRepository.findByEmail("benji@test.com")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void shouldThrowIfEmailDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
    }
}