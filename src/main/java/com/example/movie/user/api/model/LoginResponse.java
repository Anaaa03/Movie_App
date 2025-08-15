package com.example.movie.user.api.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class LoginResponse {
    private final String sessionId;
    private final Instant expiresAt;
    private final String message;
} 