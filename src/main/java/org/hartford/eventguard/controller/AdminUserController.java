package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.AdminCreateUserRequest;
import org.hartford.eventguard.dto.AdminUserResponse;
import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        List<AdminUserResponse> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @PostMapping("/create-underwriter")
    public ResponseEntity<ApiResponse<String>> createUnderwriter(@RequestBody AdminCreateUserRequest request) {
        String message = adminUserService.createUnderwriter(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message));
    }

    @PostMapping("/create-claims-officer")
    public ResponseEntity<ApiResponse<String>> createClaimsOfficer(@RequestBody AdminCreateUserRequest request) {
        String message = adminUserService.createClaimsOfficer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message));
    }
}
