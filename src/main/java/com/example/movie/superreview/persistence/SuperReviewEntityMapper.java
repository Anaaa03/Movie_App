package com.example.movie.superreview.persistence;

import com.example.movie.superreview.domain.model.SuperReview;
import com.example.movie.superreview.domain.model.SuperReviewId;
import com.example.movie.superreview.persistence.model.SuperReviewEntity;

class SuperReviewEntityMapper {
    static SuperReview toDomain(SuperReviewEntity entity) {
        return SuperReview.builder()
                .withId(new SuperReviewId(entity.getId()))
                .withMovieId(entity.getMovieId())
                .withUserId(entity.getUserId())
                .withOverallRating(entity.getOverallRating())
                .withScriptRating(entity.getScriptRating())
                .withActingRating(entity.getActingRating())
                .withEffectsRating(entity.getEffectsRating())
                .withMusicRating(entity.getMusicRating())
                .withTitle(entity.getTitle())
                .withDetailedComment(entity.getDetailedComment())
                .withPros(entity.getPros())
                .withCons(entity.getCons())
                .withRecommendation(entity.getRecommendation())
                .withCreatedAt(entity.getCreatedAt())
                .withUpdatedAt(entity.getUpdatedAt())
                .build();
    }

    static SuperReviewEntity toEntity(SuperReview superReview) {
        SuperReviewEntity entity = new SuperReviewEntity();
        entity.setId(superReview.getId().value());
        entity.setMovieId(superReview.getMovieId());
        entity.setUserId(superReview.getUserId());
        entity.setOverallRating(superReview.getOverallRating());
        entity.setScriptRating(superReview.getScriptRating());
        entity.setActingRating(superReview.getActingRating());
        entity.setEffectsRating(superReview.getEffectsRating());
        entity.setMusicRating(superReview.getMusicRating());
        entity.setTitle(superReview.getTitle());
        entity.setDetailedComment(superReview.getDetailedComment());
        entity.setPros(superReview.getPros());
        entity.setCons(superReview.getCons());
        entity.setRecommendation(superReview.getRecommendation());
        entity.setCreatedAt(superReview.getCreatedAt());
        entity.setUpdatedAt(superReview.getUpdatedAt());
        return entity;
    }
}