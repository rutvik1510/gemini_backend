package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.*;
import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.service.AuthService;
import org.hartford.eventguard.service.NotificationService;
import org.hartford.eventguard.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthService authService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_Success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("test@test.com");
        user.setFullName("Test User");
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        user.setRoles(Collections.singleton(role));

        when(authService.getUserByEmail("test@test.com")).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), anyString())).thenReturn("mock-token");

        ResponseEntity<ApiResponse<LoginResponse>> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Login successful", result.getBody().getMessage());
        assertEquals("mock-token", result.getBody().getData().getToken());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setName("New User");
        request.setPassword("password123");

        when(authService.registerCustomer(any(RegisterRequest.class))).thenReturn("Customer registered successfully");

        ResponseEntity<ApiResponse<String>> result = authController.register(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Customer registered successfully", result.getBody().getMessage());
    }
}
