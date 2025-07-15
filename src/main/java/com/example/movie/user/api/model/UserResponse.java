package com.example.movie.user.api.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private final UUID id;
    private final String username;
    private final String email;
}
