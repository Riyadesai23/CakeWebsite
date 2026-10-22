package com.example.ecommerce.service;

import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.security.UserPrincipal;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserDto register(RegisterRequest request);
    UserDto getCurrentUser(UserPrincipal principal);
}
