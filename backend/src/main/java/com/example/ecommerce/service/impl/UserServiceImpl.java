package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.dto.UserUpdateRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserDto> getUsers(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<User> users = userRepository.searchUsers(trimmedKeyword, pageable);

        List<UserDto> content = users.getContent().stream()
                .map(AuthServiceImpl::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, users.getNumber(), users.getSize(),
                users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return AuthServiceImpl.mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName() != null ? request.getLastName().trim() : null);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().length() < 6) {
                throw new BadRequestException("Password must be at least 6 characters", "INVALID_PASSWORD");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleNameStr : request.getRoles()) {
                RoleName roleName = RoleName.valueOf(roleNameStr);
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleNameStr));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updated = userRepository.save(user);
        return AuthServiceImpl.mapToDto(updated);
    }

    @Override
    @Transactional
    public UserDto toggleUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setActive(active);
        User updated = userRepository.save(user);
        return AuthServiceImpl.mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        return getUserById(userId);
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName() != null ? request.getLastName().trim() : null);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().length() < 6) {
                throw new BadRequestException("Password must be at least 6 characters", "INVALID_PASSWORD");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        User updated = userRepository.save(user);
        return AuthServiceImpl.mapToDto(updated);
    }
}
