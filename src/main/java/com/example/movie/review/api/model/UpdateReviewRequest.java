package com.example.movie.review.api.model;

import lombok.Data;

@Data
public class UpdateReviewRequest {
    private final String comment;
    private final Integer rating;
} 