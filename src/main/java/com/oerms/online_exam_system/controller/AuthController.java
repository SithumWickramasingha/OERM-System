package com.oerms.online_exam_system.controller;

import com.oerms.online_exam_system.dto.AuthResponse;
import com.oerms.online_exam_system.dto.LoginRequest;
import com.oerms.online_exam_system.entity.User;
import com.oerms.online_exam_system.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — exposes public authentication endpoints.
 *
 * POST /api/auth/register  → Register a new user (STUDENT or TEACHER)
 * POST /api/auth/login     → Authenticate and receive a JWT token
 *
 * Both endpoints are explicitly permitted in SecurityConfig (no token required).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     * Request body must include: fullName, email, password, role (STUDENT | TEACHER)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        AuthResponse response = authService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login with email and password.
     * Returns a JWT token on success.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
