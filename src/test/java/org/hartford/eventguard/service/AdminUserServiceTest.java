package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.AdminCreateUserRequest;
import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.repo.RoleRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void createUnderwriter_Success() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setFullName("Test Underwriter");
        request.setEmail("underwriter@test.com");
        request.setPassword("password123");

        Role role = new Role();
        role.setRoleName("UNDERWRITER");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("UNDERWRITER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        String response = adminUserService.createUnderwriter(request);

        assertEquals("Underwriter created successfully", response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUnderwriter_AlreadyExists_ThrowsException() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setEmail("underwriter@test.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminUserService.createUnderwriter(request);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
