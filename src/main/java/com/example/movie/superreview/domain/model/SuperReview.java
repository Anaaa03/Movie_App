package com.example.movie.superreview.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder(toBuilder = true, setterPrefix = "with")
@Data
@RequiredArgsConstructor(access = PRIVATE)
public class SuperReview {
    private final SuperReviewId id;
    private final UUID movieId;
    private final UUID userId;
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

    public static SuperReview addNewSuperReview(
            UUID movieId,
            UUID userId,
            Integer overallRating,
            Integer scriptRating,
            Integer actingRating,
            Integer effectsRating,
            Integer musicRating,
            String title,
            String detailedComment,
            String pros,
            String cons,
            Boolean recommendation) {

        SuperReviewId id = SuperReviewId.create();
        Instant createdAt = Instant.now();

        return SuperReview.builder()
                .withId(id)
                .withMovieId(movieId)
                .withUserId(userId)
                .withOverallRating(overallRating)
                .withScriptRating(scriptRating)
                .withActingRating(actingRating)
                .withEffectsRating(effectsRating)
                .withMusicRating(musicRating)
                .withTitle(title)
                .withDetailedComment(detailedComment)
                .withPros(pros)
                .withCons(cons)
                .withRecommendation(recommendation)
                .withCreatedAt(createdAt)
                .withUpdatedAt(null)
                .build();
    }

    public SuperReview updateSuperReview(
            Integer overallRating,
            Integer scriptRating,
            Integer actingRating,
            Integer effectsRating,
            Integer musicRating,
            String title,
            String detailedComment,
            String pros,
            String cons,
            Boolean recommendation) {

        return this.toBuilder()
                .withOverallRating(overallRating)
                .withScriptRating(scriptRating)
                .withActingRating(actingRating)
                .withEffectsRating(effectsRating)
                .withMusicRating(musicRating)
                .withTitle(title)
                .withDetailedComment(detailedComment)
                .withPros(pros)
                .withCons(cons)
                .withRecommendation(recommendation)
                .withUpdatedAt(Instant.now())
                .build();
    }
}