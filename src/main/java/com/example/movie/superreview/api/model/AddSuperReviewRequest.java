package com.example.movie.superreview.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(force = true)
public class AddSuperReviewRequest {
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
}