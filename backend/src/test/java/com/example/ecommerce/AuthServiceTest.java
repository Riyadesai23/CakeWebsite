package com.example.ecommerce;

import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }
        if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }

    @Test
    @Transactional
    public void testUserRegistrationSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser1");
        request.setEmail("testuser1@example.com");
        request.setPassword("securepass123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhone("1234567890");

        UserDto userDto = authService.register(request);

        assertNotNull(userDto);
        assertEquals("testuser1", userDto.getUsername());
        assertEquals("testuser1@example.com", userDto.getEmail());
        assertTrue(userRepository.findByUsername("testuser1").isPresent());

        User savedUser = userRepository.findByUsername("testuser1").get();
        assertTrue(passwordEncoder.matches("securepass123", savedUser.getPassword()));
    }

    @Test
    @Transactional
    public void testDuplicateUsernameRegistrationFails() {
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("duplicate_user");
        request1.setEmail("dup1@example.com");
        request1.setPassword("password123");
        request1.setFirstName("Dup");
        authService.register(request1);

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("duplicate_user");
        request2.setEmail("dup2@example.com");
        request2.setPassword("password123");
        request2.setFirstName("Dup2");

        assertThrows(BadRequestException.class, () -> {
            authService.register(request2);
        });
    }

    @Test
    @Transactional
    public void testLoginSuccess() {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("login_test_user");
        reg.setEmail("logintest@example.com");
        reg.setPassword("secret123");
        reg.setFirstName("Login");
        authService.register(reg);

        LoginRequest loginRequest = new LoginRequest("login_test_user", "secret123");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("login_test_user", response.getUser().getUsername());
    }
}
