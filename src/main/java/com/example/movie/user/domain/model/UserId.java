package com.example.movie.user.domain.model;

import lombok.NonNull;

import java.util.UUID;

public record UserId(@NonNull UUID value) {
    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }
}
