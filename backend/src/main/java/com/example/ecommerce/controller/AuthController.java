package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok("Registration successful. You can now log in.", userDto), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"), HttpStatus.UNAUTHORIZED);
        }
        UserDto userDto = authService.getCurrentUser(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(userDto));
    }
}
