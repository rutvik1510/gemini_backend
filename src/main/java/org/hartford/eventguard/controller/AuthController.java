package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.AuthRequest;
import org.hartford.eventguard.dto.LoginResponse;
import org.hartford.eventguard.dto.RegisterRequest;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.service.AuthService;
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

        // Extract role name (get first role, users typically have one primary role)
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getRoleName())
                .orElse("CUSTOMER"); // Default to CUSTOMER if no role found

        // Generate token with role
        String token = jwtUtil.generateToken(request.getEmail(), roleName);
        LoginResponse loginResponse = new LoginResponse(token);

        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    //  REGISTER CUSTOMER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        String message = authService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}

