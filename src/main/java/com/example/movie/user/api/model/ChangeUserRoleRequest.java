package com.example.movie.user.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChangeUserRoleRequest {
    private UUID userId;
    private String newRole;

    public ChangeUserRoleRequest(UUID userId, String newRole) {
        this.userId = userId;
        this.newRole = newRole;
    }
}