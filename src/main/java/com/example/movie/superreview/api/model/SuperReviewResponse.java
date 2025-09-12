package com.example.movie.superreview.api.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class SuperReviewResponse {
    private final UUID id;
    private final UUID movieId;
    private final Integer overallRating;
    private final Integer scriptRating;
    private final Integer actingRating;
    private final Integer effectsRating;
    private final Integer musicRating;
    private final String title;
    private final String detailedComment;
    private final String pros;
    private final String cons;
    private final Boolean recommendation;
    private final Instant createdAt;
    private final Instant updatedAt;
}