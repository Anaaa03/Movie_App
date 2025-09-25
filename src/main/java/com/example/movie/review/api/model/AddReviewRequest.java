package com.example.movie.review.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(force = true)
public class AddReviewRequest {
    private final UUID movieId;
    private final Integer rating;
    private final String comment;

    public AddReviewRequest(UUID movieId, Integer rating, String comment) {
        this.movieId = movieId;
        this.rating = rating;
        this.comment = comment;
    }
}
