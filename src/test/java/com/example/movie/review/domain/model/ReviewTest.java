package com.example.movie.review.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Test
    void shouldCreateNewReviewWithAllFields() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String comment = "Great movie!";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getMovieId()).isEqualTo(movieId);
        assertThat(review.getUserId()).isEqualTo(userId);
        assertThat(review.getRating()).isEqualTo(rating);
        assertThat(review.getComment()).isEqualTo(comment);
        assertThat(review.getId()).isNotNull();
        assertThat(review.getCreatedAt()).isNotNull();
        assertThat(review.getUpdatedAt()).isNull();
        assertThat(review.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldCreateNewReviewWithNullComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 5;

        // when
        Review review = Review.addNewReview(movieId, userId, rating, null);

        // then
        assertThat(review.getMovieId()).isEqualTo(movieId);
        assertThat(review.getUserId()).isEqualTo(userId);
        assertThat(review.getRating()).isEqualTo(rating);
        assertThat(review.getComment()).isNull();
        assertThat(review.getId()).isNotNull();
        assertThat(review.getCreatedAt()).isNotNull();
        assertThat(review.getUpdatedAt()).isNull();
    }

    @Test
    void shouldCreateNewReviewWithEmptyComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 7;
        String comment = "";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getComment()).isEqualTo("");
        assertThat(review.getRating()).isEqualTo(rating);
    }

    @Test
    void shouldCreateNewReviewWithMinimumRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 1;
        String comment = "Terrible movie";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getRating()).isEqualTo(1);
        assertThat(review.getComment()).isEqualTo(comment);
    }

    @Test
    void shouldCreateNewReviewWithMaximumRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 10;
        String comment = "Perfect movie!";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getRating()).isEqualTo(10);
        assertThat(review.getComment()).isEqualTo(comment);
    }

    @Test
    void shouldCreateReviewWithUniqueId() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String comment = "Good movie";

        // when
        Review review1 = Review.addNewReview(movieId, userId, rating, comment);
        Review review2 = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review1.getId()).isNotEqualTo(review2.getId());
        assertThat(review1.getId().value()).isNotEqualTo(review2.getId().value());
    }

    @Test
    void shouldCreateReviewWithDifferentCreatedAtTimes() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String comment = "Good movie";

        // when
        Review review1 = Review.addNewReview(movieId, userId, rating, comment);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Review review2 = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review1.getCreatedAt()).isNotEqualTo(review2.getCreatedAt());
        assertThat(review1.getCreatedAt()).isBefore(review2.getCreatedAt());
    }

    @Test
    void shouldUpdateReviewCommentAndRating() {
        // given
        Review originalReview = Review.addNewReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                8,
                "Original comment"
        );
        String newComment = "Updated comment";
        Integer newRating = 9;

        // when
        Review updatedReview = originalReview.updateReview(newComment, newRating);

        // then
        assertThat(updatedReview.getComment()).isEqualTo(newComment);
        assertThat(updatedReview.getRating()).isEqualTo(newRating);
        assertThat(updatedReview.getUpdatedAt()).isNotNull();

        assertThat(originalReview.getComment()).isEqualTo("Original comment");
        assertThat(originalReview.getRating()).isEqualTo(8);
        assertThat(originalReview.getUpdatedAt()).isNull();
    }

    @Test
    void shouldUpdateReviewWithNullValues() {
        // given
        Review originalReview = Review.addNewReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                8,
                "Original comment"
        );

        // when
        Review updatedReview = originalReview.updateReview(null, null);

        // then
        assertThat(updatedReview.getComment()).isNull();
        assertThat(updatedReview.getRating()).isNull();
        assertThat(updatedReview.getUpdatedAt()).isNotNull();

        assertThat(originalReview.getComment()).isEqualTo("Original comment");
        assertThat(originalReview.getRating()).isEqualTo(8);
    }

    @Test
    void shouldPreserveAllFieldsWhenUpdating() {
        // given
        ReviewId reviewId = ReviewId.create();
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 7;
        String comment = "Good movie";
        Instant createdAt = Instant.now().minusSeconds(3600);

        Review originalReview = Review.builder()
                .withId(reviewId)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(rating)
                .withComment(comment)
                .withCreatedAt(createdAt)
                .withUpdatedAt(null)
                .build();

        String newComment = "Updated comment";
        Integer newRating = 9;

        // when
        Review updatedReview = originalReview.updateReview(newComment, newRating);

        // then
        assertThat(updatedReview.getId()).isEqualTo(reviewId);
        assertThat(updatedReview.getMovieId()).isEqualTo(movieId);
        assertThat(updatedReview.getUserId()).isEqualTo(userId);
        assertThat(updatedReview.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedReview.getComment()).isEqualTo(newComment);
        assertThat(updatedReview.getRating()).isEqualTo(newRating);
        assertThat(updatedReview.getUpdatedAt()).isNotNull();
        assertThat(updatedReview.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void shouldCreateReviewWithLongComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String longComment = "A".repeat(1000); // 1000 characters

        // when
        Review review = Review.addNewReview(movieId, userId, rating, longComment);

        // then
        assertThat(review.getComment()).isEqualTo(longComment);
        assertThat(review.getComment()).hasSize(1000);
    }

    @Test
    void shouldCreateReviewWithSpecialCharactersInComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String specialComment = "Great movie! àáâãäåæçèéêë & symbols: @#$%^&*() 🎬🍿";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, specialComment);

        // then
        assertThat(review.getComment()).isEqualTo(specialComment);
    }

    @Test
    void shouldCreateReviewWithWhitespaceComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 5;
        String whitespaceComment = "   ";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, whitespaceComment);

        // then
        assertThat(review.getComment()).isEqualTo("   ");
    }

    @Test
    void shouldCreateReviewWithNullRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String comment = "No rating given";

        // when
        Review review = Review.addNewReview(movieId, userId, null, comment);

        // then
        assertThat(review.getRating()).isNull();
        assertThat(review.getComment()).isEqualTo(comment);
    }

    @Test
    void shouldCreateReviewWithZeroRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 0;
        String comment = "Zero rating";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getRating()).isEqualTo(0);
    }

    @Test
    void shouldCreateReviewWithNegativeRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = -5;
        String comment = "Negative rating";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getRating()).isEqualTo(-5);
    }

    @Test
    void shouldCreateReviewWithHighRating() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 100;
        String comment = "Very high rating";

        // when
        Review review = Review.addNewReview(movieId, userId, rating, comment);

        // then
        assertThat(review.getRating()).isEqualTo(100);
    }

    @Test
    void shouldNotBeSameObjectAfterUpdate() {
        // given
        Review originalReview = Review.addNewReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                8,
                "Original comment"
        );

        // when
        Review updatedReview = originalReview.updateReview("New comment", 9);

        // then
        assertThat(updatedReview).isNotSameAs(originalReview);
        assertThat(updatedReview).isNotEqualTo(originalReview);
    }

    @Test
    void shouldCreateReviewWithExtremelyLongComment() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 8;
        String extremelyLongComment = "A".repeat(10000); // 10,000 characters

        // when
        Review review = Review.addNewReview(movieId, userId, rating, extremelyLongComment);

        // then
        assertThat(review.getComment()).isEqualTo(extremelyLongComment);
        assertThat(review.getComment()).hasSize(10000);
    }

    @Test
    void shouldUpdateReviewWithEmptyComment() {
        // given
        Review originalReview = Review.addNewReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                8,
                "Original comment"
        );

        // when
        Review updatedReview = originalReview.updateReview("", 9);

        // then
        assertThat(updatedReview.getComment()).isEqualTo("");
        assertThat(updatedReview.getRating()).isEqualTo(9);
    }
}
