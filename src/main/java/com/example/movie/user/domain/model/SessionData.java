package com.example.movie.user.domain.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SessionData {
    private final UUID userId;
    private final Instant expiresAt;
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public static SessionData create(UUID userId, int hoursValid) {
        Instant expiresAt = Instant.now().plusSeconds(hoursValid * 3600L);
        return new SessionData(userId, expiresAt);
    }
} 