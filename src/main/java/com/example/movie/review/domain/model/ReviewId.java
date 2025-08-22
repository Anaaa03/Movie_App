package com.example.movie.review.domain.model;

import lombok.NonNull;

import java.util.UUID;

public record ReviewId(@NonNull UUID value) {
    public static ReviewId create() {
        return new ReviewId(UUID.randomUUID());
    }
}
