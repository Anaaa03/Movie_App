package com.example.movie.review.api;

import com.example.movie.review.api.model.ReviewResponse;
import com.example.movie.review.domain.model.Review;

class ReviewResponseMapper {
    static ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId().value())
                .movieId(review.getMovieId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
} 