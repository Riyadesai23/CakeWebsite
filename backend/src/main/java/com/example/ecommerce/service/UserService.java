package com.example.ecommerce.service;

import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.dto.UserUpdateRequest;

public interface UserService {
    PagedResponse<UserDto> getUsers(String keyword, int page, int size, String sortBy, String sortDir);
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserUpdateRequest request);
    UserDto toggleUserStatus(Long id, boolean active);
    UserDto getUserProfile(Long userId);
    UserDto updateUserProfile(Long userId, UserUpdateRequest request);
}
