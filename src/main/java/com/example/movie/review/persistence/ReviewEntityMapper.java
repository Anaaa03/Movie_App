package com.example.movie.review.persistence;

import com.example.movie.review.domain.model.Review;
import com.example.movie.review.domain.model.ReviewId;
import com.example.movie.review.persistence.model.ReviewEntity;

class ReviewEntityMapper {
    static Review toDomain(ReviewEntity entity) {
        ReviewId id = new ReviewId(entity.getId());
        return Review.builder()
                .withId(id)
                .withMovieId(entity.getMovieId())
                .withUserId(entity.getUserId())
                .withRating(entity.getRating())
                .withComment(entity.getComment())
                .withCreatedAt(entity.getCreatedAt())
                .withUpdatedAt(entity.getUpdatedAt())
                .build();
    }

    static ReviewEntity toEntity(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(review.getId() != null ? review.getId().value() : null);
        entity.setMovieId(review.getMovieId());
        entity.setUserId(review.getUserId());
        entity.setRating(review.getRating());
        entity.setComment(review.getComment());
        entity.setCreatedAt(review.getCreatedAt());
        entity.setUpdatedAt(review.getUpdatedAt());
        return entity;
    }
}
