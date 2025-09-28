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

    public AddSuperReviewRequest(UUID movieId, Integer overallRating, Integer scriptRating,
                                 Integer actingRating, Integer effectsRating, Integer musicRating,
                                 String title, String detailedComment, String pros, String cons,
                                 Boolean recommendation) {
        this.movieId = movieId;
        this.overallRating = overallRating;
        this.scriptRating = scriptRating;
        this.actingRating = actingRating;
        this.effectsRating = effectsRating;
        this.musicRating = musicRating;
        this.title = title;
        this.detailedComment = detailedComment;
        this.pros = pros;
        this.cons = cons;
        this.recommendation = recommendation;
    }
}