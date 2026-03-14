package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.AuthRequest;
import org.hartford.eventguard.dto.LoginResponse;
import org.hartford.eventguard.dto.RegisterRequest;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.service.AuthService;
import org.hartford.eventguard.service.NotificationService;
import org.hartford.eventguard.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody AuthRequest request) {

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user to get role
        User user = authService.getUserByEmail(request.getEmail());

        // Notify user
        notificationService.createNotification(user, "New login detected from: " + request.getEmail(), "INFO");

        // Extract all roles
        java.util.List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(java.util.stream.Collectors.toList());
        
        if (roles.isEmpty()) roles.add("CUSTOMER");

        // Primary role for backward compatibility in DTO
        String primaryRole = roles.get(0);

        // Generate token with roles and name
        String token = jwtUtil.generateToken(request.getEmail(), roles, user.getFullName());
        LoginResponse loginResponse = new LoginResponse(token, user.getFullName(), user.getEmail(), primaryRole);

        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    //  REGISTER CUSTOMER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@jakarta.validation.Valid @RequestBody RegisterRequest request) {
        String message = authService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
