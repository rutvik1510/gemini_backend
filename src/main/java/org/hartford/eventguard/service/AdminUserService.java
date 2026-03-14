package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.AdminCreateUserRequest;
import org.hartford.eventguard.dto.AdminUserResponse;
import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.repo.RoleRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, 
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createUnderwriter(AdminCreateUserRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Get UNDERWRITER role
        Role underwriterRole = roleRepository.findByRoleName("UNDERWRITER")
                .orElseThrow(() -> new RuntimeException("UNDERWRITER role not found"));

        // Create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(new HashSet<>(Collections.singletonList(underwriterRole)));

        userRepository.save(user);

        return "Underwriter created successfully";
    }

    public String createClaimsOfficer(AdminCreateUserRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Get CLAIMS_OFFICER role
        Role claimsOfficerRole = roleRepository.findByRoleName("CLAIMS_OFFICER")
                .orElseThrow(() -> new RuntimeException("CLAIMS_OFFICER role not found"));

        // Create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(new HashSet<>(Collections.singletonList(claimsOfficerRole)));

        userRepository.save(user);

        return "Claims Officer created successfully";
    }

    public String updateUser(Long userId, AdminCreateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return "User updated successfully";
    }

    public List<AdminUserResponse> getUnderwriters() {
        Role underwriterRole = roleRepository.findByRoleName("UNDERWRITER")
                .orElseThrow(() -> new RuntimeException("UNDERWRITER role not found"));

        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(underwriterRole))
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());
    }

    public List<AdminUserResponse> getClaimsOfficers() {
        Role claimsOfficerRole = roleRepository.findByRoleName("CLAIMS_OFFICER")
                .orElseThrow(() -> new RuntimeException("CLAIMS_OFFICER role not found"));

        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(claimsOfficerRole))
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());
    }

    private AdminUserResponse convertToAdminUserResponse(User user) {
        AdminUserResponse response = new AdminUserResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCompanyName(user.getCompanyName());
        response.setActive(user.isActive());
        response.setRoles(user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList()));
        return response;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());
    }
}
