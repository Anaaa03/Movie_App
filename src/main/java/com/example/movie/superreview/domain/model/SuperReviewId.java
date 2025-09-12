package com.example.movie.superreview.domain.model;

import lombok.NonNull;

import java.util.UUID;

public record SuperReviewId(@NonNull UUID value) {
    public static SuperReviewId create() {
        return new SuperReviewId(UUID.randomUUID());
    }
}