package com.example.movie.review.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder(toBuilder = true, setterPrefix = "with")
@Data
@RequiredArgsConstructor(access = PRIVATE)
public class Review {
    private final ReviewId id;
    private final UUID movieId;
    private final UUID userId;
    private final Integer rating;
    private final String comment;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static Review addNewReview(UUID movieId, UUID userId, Integer rating, String comment) {
        ReviewId id = ReviewId.create();
        Instant createdAt = Instant.now();
        return Review.builder()
                .withId(id)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(rating)
                .withComment(comment)
                .withCreatedAt(createdAt)
                .withUpdatedAt(null)
                .build();
    }

    public Review updateReview(String newComment, Integer newRating) {
        return this.toBuilder()
                .withComment(newComment)
                .withRating(newRating)
                .withUpdatedAt(Instant.now())
                .build();
    }
}
