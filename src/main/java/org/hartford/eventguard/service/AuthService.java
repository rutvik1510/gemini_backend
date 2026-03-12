package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.RegisterRequest;
import org.hartford.eventguard.dto.UserResponse;
import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.RoleRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerCustomer(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidRequestException("Email already registered. Please use a different email");
        }

        // Validate request
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new InvalidRequestException("Password must be at least 6 characters long");
        }

        // Create user
        User user = new User();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign CUSTOMER role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("CUSTOMER role not found in system"));

        user.getRoles().add(customerRole);

        userRepository.save(user);

        return "Customer registered successfully";
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // Admin method to get all users
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setIsActive(user.isActive());

        // Get primary role name
        String roleName = user.getRoles().isEmpty() ? "UNKNOWN" :
                         user.getRoles().iterator().next().getRoleName();
        response.setRole(roleName);

        return response;
    }
}
