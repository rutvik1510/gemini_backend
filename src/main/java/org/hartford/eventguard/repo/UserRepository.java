package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    java.util.List<User> findByRoles_RoleName(String roleName);
    boolean existsByEmail(String email);

}