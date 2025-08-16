package com.example.movie.user.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(force = true)
public class ChangeUserRoleRequest {
    private final UUID userId;
    private final String newRole;
}