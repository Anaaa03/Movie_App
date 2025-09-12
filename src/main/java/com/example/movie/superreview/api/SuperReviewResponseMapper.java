package com.example.movie.superreview.api;

import com.example.movie.superreview.api.model.SuperReviewResponse;
import com.example.movie.superreview.domain.model.SuperReview;

class SuperReviewResponseMapper {
    static SuperReviewResponse mapToSuperReviewResponse(SuperReview superReview) {
        return SuperReviewResponse.builder()
                .id(superReview.getId().value())
                .movieId(superReview.getMovieId())
                .overallRating(superReview.getOverallRating())
                .scriptRating(superReview.getScriptRating())
                .actingRating(superReview.getActingRating())
                .effectsRating(superReview.getEffectsRating())
                .musicRating(superReview.getMusicRating())
                .title(superReview.getTitle())
                .detailedComment(superReview.getDetailedComment())
                .pros(superReview.getPros())
                .cons(superReview.getCons())
                .recommendation(superReview.getRecommendation())
                .createdAt(superReview.getCreatedAt())
                .updatedAt(superReview.getUpdatedAt())
                .build();
    }
}