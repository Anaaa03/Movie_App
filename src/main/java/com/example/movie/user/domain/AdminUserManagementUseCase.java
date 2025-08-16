package com.example.movie.user.domain;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminUserManagementUseCase {
    private final UserRepository userRepository;

    public AdminUserManagementUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User changeUserRole(UUID adminUserId, UUID targetUserId, String newRole) {
        
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.isAdmin()) {
            throw new RuntimeException("Only admins can change user roles");
        }

        if (!isValidRole(newRole)) {
            throw new RuntimeException("Invalid role: " + newRole);
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        User updatedUser = targetUser.changeRole(newRole);
        
        return userRepository.save(updatedUser);
    }

    private boolean isValidRole(String role) {
        return "USER".equals(role) || "ADMIN".equals(role);
    }
}