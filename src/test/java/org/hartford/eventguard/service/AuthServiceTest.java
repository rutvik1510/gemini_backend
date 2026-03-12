package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.RegisterRequest;
import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.repo.RoleRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCustomer_savesEncodedPasswordAndCustomerRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul");
        request.setEmail("rahul@gmail.com");
        request.setPassword("rahul123");

        Role customerRole = new Role();
        customerRole.setRoleName("CUSTOMER");

        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("rahul123")).thenReturn("encoded-password");

        String result = authService.registerCustomer(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Customer registered successfully", result);
        assertEquals("Rahul", savedUser.getFullName());
        assertEquals("rahul@gmail.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(customerRole));
    }
}
