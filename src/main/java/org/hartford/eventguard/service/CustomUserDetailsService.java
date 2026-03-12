package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Role;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // ⃣ Fetch user from DB using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        //  Convert roles to authorities (use as-is if already prefixed with ROLE_)
        String[] authorities = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().startsWith("ROLE_") 
                        ? role.getRoleName() 
                        : "ROLE_" + role.getRoleName())
                .toArray(String[]::new);

        //  Return Spring Security User
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}