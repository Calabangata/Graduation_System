package com.example.backend.data.repository;

import com.example.backend.data.entity.Role;
import com.example.backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by role name
    Optional<Role> findByName(UserRole name);
}
