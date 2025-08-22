package com.example.movie.review.api.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class ReviewResponse {
    private final UUID id;
    private final UUID movieId;
    private final Integer rating;
    private final String comment;
    private final Instant createdAt;
} 