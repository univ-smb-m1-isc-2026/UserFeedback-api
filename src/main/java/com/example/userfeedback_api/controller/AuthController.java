package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.RegisterRequest;
import com.example.userfeedback_api.entity.User;
import com.example.userfeedback_api.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}