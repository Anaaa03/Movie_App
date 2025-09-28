package com.example.movie.superreview.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SuperReviewTest {

    @Test
    void shouldCreateNewSuperReviewWithAllFields() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer overallRating = 8;
        Integer scriptRating = 7;
        Integer actingRating = 9;
        Integer effectsRating = 8;
        Integer musicRating = 6;
        String title = "Excellent Movie Review";
        String detailedComment = "This is a very detailed comment about the movie...";
        String pros = "Great acting, amazing story, good effects";
        String cons = "Music could be better, some pacing issues";
        Boolean recommendation = true;

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, overallRating, scriptRating, actingRating,
                effectsRating, musicRating, title, detailedComment, pros, cons, recommendation
        );

        // then
        assertThat(superReview.getMovieId()).isEqualTo(movieId);
        assertThat(superReview.getUserId()).isEqualTo(userId);
        assertThat(superReview.getOverallRating()).isEqualTo(overallRating);
        assertThat(superReview.getScriptRating()).isEqualTo(scriptRating);
        assertThat(superReview.getActingRating()).isEqualTo(actingRating);
        assertThat(superReview.getEffectsRating()).isEqualTo(effectsRating);
        assertThat(superReview.getMusicRating()).isEqualTo(musicRating);
        assertThat(superReview.getTitle()).isEqualTo(title);
        assertThat(superReview.getDetailedComment()).isEqualTo(detailedComment);
        assertThat(superReview.getPros()).isEqualTo(pros);
        assertThat(superReview.getCons()).isEqualTo(cons);
        assertThat(superReview.getRecommendation()).isEqualTo(recommendation);
        assertThat(superReview.getId()).isNotNull();
        assertThat(superReview.getCreatedAt()).isNotNull();
        assertThat(superReview.getUpdatedAt()).isNull();
        assertThat(superReview.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldCreateNewSuperReviewWithNullOptionalFields() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer overallRating = 8;

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, overallRating, null, null, null, null,
                null, null, null, null, null
        );

        // then
        assertThat(superReview.getMovieId()).isEqualTo(movieId);
        assertThat(superReview.getUserId()).isEqualTo(userId);
        assertThat(superReview.getOverallRating()).isEqualTo(overallRating);
        assertThat(superReview.getScriptRating()).isNull();
        assertThat(superReview.getActingRating()).isNull();
        assertThat(superReview.getEffectsRating()).isNull();
        assertThat(superReview.getMusicRating()).isNull();
        assertThat(superReview.getTitle()).isNull();
        assertThat(superReview.getDetailedComment()).isNull();
        assertThat(superReview.getPros()).isNull();
        assertThat(superReview.getCons()).isNull();
        assertThat(superReview.getRecommendation()).isNull();
        assertThat(superReview.getId()).isNotNull();
        assertThat(superReview.getCreatedAt()).isNotNull();
        assertThat(superReview.getUpdatedAt()).isNull();
    }

    @Test
    void shouldCreateNewSuperReviewWithMinimumRatings() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 1, 1, 1, 1, 1,
                "Terrible Movie", "Very bad movie", "None", "Everything is bad", false
        );

        // then
        assertThat(superReview.getOverallRating()).isEqualTo(1);
        assertThat(superReview.getScriptRating()).isEqualTo(1);
        assertThat(superReview.getActingRating()).isEqualTo(1);
        assertThat(superReview.getEffectsRating()).isEqualTo(1);
        assertThat(superReview.getMusicRating()).isEqualTo(1);
        assertThat(superReview.getRecommendation()).isFalse();
    }

    @Test
    void shouldCreateNewSuperReviewWithMaximumRatings() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 10, 10, 10, 10, 10,
                "Perfect Movie", "Absolutely perfect in every way", "Everything", "Nothing", true
        );

        // then
        assertThat(superReview.getOverallRating()).isEqualTo(10);
        assertThat(superReview.getScriptRating()).isEqualTo(10);
        assertThat(superReview.getActingRating()).isEqualTo(10);
        assertThat(superReview.getEffectsRating()).isEqualTo(10);
        assertThat(superReview.getMusicRating()).isEqualTo(10);
        assertThat(superReview.getRecommendation()).isTrue();
    }

    @Test
    void shouldCreateSuperReviewWithUniqueId() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview1 = SuperReview.addNewSuperReview(
                movieId, userId, 8, 7, 9, 8, 6, "Title", "Comment", "Pros", "Cons", true
        );
        SuperReview superReview2 = SuperReview.addNewSuperReview(
                movieId, userId, 8, 7, 9, 8, 6, "Title", "Comment", "Pros", "Cons", true
        );

        // then
        assertThat(superReview1.getId()).isNotEqualTo(superReview2.getId());
        assertThat(superReview1.getId().value()).isNotEqualTo(superReview2.getId().value());
    }

    @Test
    void shouldCreateSuperReviewWithDifferentCreatedAtTimes() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview1 = SuperReview.addNewSuperReview(
                movieId, userId, 8, 7, 9, 8, 6, "Title", "Comment", "Pros", "Cons", true
        );

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SuperReview superReview2 = SuperReview.addNewSuperReview(
                movieId, userId, 8, 7, 9, 8, 6, "Title", "Comment", "Pros", "Cons", true
        );

        // then
        assertThat(superReview1.getCreatedAt()).isNotEqualTo(superReview2.getCreatedAt());
        assertThat(superReview1.getCreatedAt()).isBefore(superReview2.getCreatedAt());
    }

    @Test
    void shouldUpdateSuperReviewWithPartialFields() {
        // given
        SuperReview originalSuperReview = SuperReview.addNewSuperReview(
                UUID.randomUUID(), UUID.randomUUID(), 8, 7, 9, 8, 6,
                "Original Title", "Original comment", "Original pros", "Original cons", true
        );

        SuperReview updatedSuperReview = originalSuperReview.updateSuperReview(
                9, null, 10, null, null, "New Title", null, null, "New cons", false
        );

        // then
        assertThat(updatedSuperReview.getOverallRating()).isEqualTo(9);
        assertThat(updatedSuperReview.getScriptRating()).isNull();
        assertThat(updatedSuperReview.getActingRating()).isEqualTo(10);
        assertThat(updatedSuperReview.getEffectsRating()).isNull();
        assertThat(updatedSuperReview.getMusicRating()).isNull();
        assertThat(updatedSuperReview.getTitle()).isEqualTo("New Title");
        assertThat(updatedSuperReview.getDetailedComment()).isNull();
        assertThat(updatedSuperReview.getPros()).isNull();
        assertThat(updatedSuperReview.getCons()).isEqualTo("New cons");
        assertThat(updatedSuperReview.getRecommendation()).isFalse();
        assertThat(updatedSuperReview.getUpdatedAt()).isNotNull();

        assertThat(originalSuperReview.getScriptRating()).isEqualTo(7);
        assertThat(originalSuperReview.getDetailedComment()).isEqualTo("Original comment");
        assertThat(originalSuperReview.getUpdatedAt()).isNull();
    }

    @Test
    void shouldUpdateSuperReviewWithAllNullValues() {
        // given
        SuperReview originalSuperReview = SuperReview.addNewSuperReview(
                UUID.randomUUID(), UUID.randomUUID(), 8, 7, 9, 8, 6,
                "Original Title", "Original comment", "Original pros", "Original cons", true
        );

        // when
        SuperReview updatedSuperReview = originalSuperReview.updateSuperReview(
                null, null, null, null, null, null, null, null, null, null
        );

        // then
        assertThat(updatedSuperReview.getOverallRating()).isNull();
        assertThat(updatedSuperReview.getScriptRating()).isNull();
        assertThat(updatedSuperReview.getActingRating()).isNull();
        assertThat(updatedSuperReview.getEffectsRating()).isNull();
        assertThat(updatedSuperReview.getMusicRating()).isNull();
        assertThat(updatedSuperReview.getTitle()).isNull();
        assertThat(updatedSuperReview.getDetailedComment()).isNull();
        assertThat(updatedSuperReview.getPros()).isNull();
        assertThat(updatedSuperReview.getCons()).isNull();
        assertThat(updatedSuperReview.getRecommendation()).isNull();
        assertThat(updatedSuperReview.getUpdatedAt()).isNotNull();

        assertThat(originalSuperReview.getOverallRating()).isEqualTo(8);
        assertThat(originalSuperReview.getTitle()).isEqualTo("Original Title");
        assertThat(originalSuperReview.getRecommendation()).isTrue();
    }

    @Test
    void shouldPreserveAllFieldsWhenUpdating() {
        // given
        SuperReviewId superReviewId = SuperReviewId.create();
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(3600);

        SuperReview originalSuperReview = SuperReview.builder()
                .withId(superReviewId)
                .withMovieId(movieId)
                .withUserId(userId)
                .withOverallRating(7)
                .withScriptRating(6)
                .withActingRating(8)
                .withEffectsRating(7)
                .withMusicRating(5)
                .withTitle("Original Title")
                .withDetailedComment("Original comment")
                .withPros("Original pros")
                .withCons("Original cons")
                .withRecommendation(true)
                .withCreatedAt(createdAt)
                .withUpdatedAt(null)
                .build();

        // when
        SuperReview updatedSuperReview = originalSuperReview.updateSuperReview(
                9, 8, 10, 9, 7, "Updated Title", "Updated comment", "Updated pros", "Updated cons", false
        );

        // then
        assertThat(updatedSuperReview.getId()).isEqualTo(superReviewId);
        assertThat(updatedSuperReview.getMovieId()).isEqualTo(movieId);
        assertThat(updatedSuperReview.getUserId()).isEqualTo(userId);
        assertThat(updatedSuperReview.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedSuperReview.getOverallRating()).isEqualTo(9);
        assertThat(updatedSuperReview.getScriptRating()).isEqualTo(8);
        assertThat(updatedSuperReview.getActingRating()).isEqualTo(10);
        assertThat(updatedSuperReview.getEffectsRating()).isEqualTo(9);
        assertThat(updatedSuperReview.getMusicRating()).isEqualTo(7);
        assertThat(updatedSuperReview.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedSuperReview.getDetailedComment()).isEqualTo("Updated comment");
        assertThat(updatedSuperReview.getPros()).isEqualTo("Updated pros");
        assertThat(updatedSuperReview.getCons()).isEqualTo("Updated cons");
        assertThat(updatedSuperReview.getRecommendation()).isFalse();
        assertThat(updatedSuperReview.getUpdatedAt()).isNotNull();
        assertThat(updatedSuperReview.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void shouldCreateSuperReviewWithLongTexts() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String longTitle = "A".repeat(200);
        String longDetailedComment = "B".repeat(5000);
        String longPros = "C".repeat(2000);
        String longCons = "D".repeat(2000);

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 8, 7, 9, 8, 6,
                longTitle, longDetailedComment, longPros, longCons, true
        );

        // then
        assertThat(superReview.getTitle()).isEqualTo(longTitle);
        assertThat(superReview.getTitle()).hasSize(200);
        assertThat(superReview.getDetailedComment()).isEqualTo(longDetailedComment);
        assertThat(superReview.getDetailedComment()).hasSize(5000);
        assertThat(superReview.getPros()).isEqualTo(longPros);
        assertThat(superReview.getPros()).hasSize(2000);
        assertThat(superReview.getCons()).isEqualTo(longCons);
        assertThat(superReview.getCons()).hasSize(2000);
    }

    @Test
    void shouldCreateSuperReviewWithSpecialCharacters() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String specialTitle = "Awesome Movie: The Sequel! (2023) - Director's Cut àáâãäåæçèéêë 🎬";
        String specialComment = "Great movie with special chars: @#$%^&*() and emojis 🍿🎭";
        String specialPros = "Amazing effects & sound! 100% recommended ⭐⭐⭐⭐⭐";
        String specialCons = "Maybe too long... but that's okay 😅";

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 9, 8, 10, 9, 7,
                specialTitle, specialComment, specialPros, specialCons, true
        );

        // then
        assertThat(superReview.getTitle()).isEqualTo(specialTitle);
        assertThat(superReview.getDetailedComment()).isEqualTo(specialComment);
        assertThat(superReview.getPros()).isEqualTo(specialPros);
        assertThat(superReview.getCons()).isEqualTo(specialCons);
    }

    @Test
    void shouldCreateSuperReviewWithEmptyStrings() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 5, 5, 5, 5, 5,
                "", "", "", "", false
        );

        // then
        assertThat(superReview.getTitle()).isEqualTo("");
        assertThat(superReview.getDetailedComment()).isEqualTo("");
        assertThat(superReview.getPros()).isEqualTo("");
        assertThat(superReview.getCons()).isEqualTo("");
    }

    @Test
    void shouldCreateSuperReviewWithWhitespaceStrings() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 6, 6, 6, 6, 6,
                "   ", "   ", "   ", "   ", null
        );

        // then
        assertThat(superReview.getTitle()).isEqualTo("   ");
        assertThat(superReview.getDetailedComment()).isEqualTo("   ");
        assertThat(superReview.getPros()).isEqualTo("   ");
        assertThat(superReview.getCons()).isEqualTo("   ");
        assertThat(superReview.getRecommendation()).isNull();
    }

    @Test
    void shouldCreateSuperReviewWithMixedRatings() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 7, 10, 3, 8, null,
                "Mixed Review", "Some aspects great, others not so much",
                "Great script and effects", "Poor acting", true
        );

        // then
        assertThat(superReview.getOverallRating()).isEqualTo(7);
        assertThat(superReview.getScriptRating()).isEqualTo(10);
        assertThat(superReview.getActingRating()).isEqualTo(3);
        assertThat(superReview.getEffectsRating()).isEqualTo(8);
        assertThat(superReview.getMusicRating()).isNull();
        assertThat(superReview.getRecommendation()).isTrue();
    }

    @Test
    void shouldNotBeSameObjectAfterUpdate() {
        // given
        SuperReview originalSuperReview = SuperReview.addNewSuperReview(
                UUID.randomUUID(), UUID.randomUUID(), 8, 7, 9, 8, 6,
                "Original Title", "Original comment", "Original pros", "Original cons", true
        );

        // when
        SuperReview updatedSuperReview = originalSuperReview.updateSuperReview(
                9, 8, 10, 9, 7, "New Title", "New comment", "New pros", "New cons", false
        );

        // then
        assertThat(updatedSuperReview).isNotSameAs(originalSuperReview);
        assertThat(updatedSuperReview).isNotEqualTo(originalSuperReview);
    }

    @Test
    void shouldCreateSuperReviewWithExtremeValues() {
        // given
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String extremelyLongTitle = "A".repeat(1000);
        String extremelyLongComment = "B".repeat(10000);
        String extremelyLongPros = "C".repeat(5000);
        String extremelyLongCons = "D".repeat(5000);

        // when
        SuperReview superReview = SuperReview.addNewSuperReview(
                movieId, userId, 1, 10, 1, 10, 1,
                extremelyLongTitle, extremelyLongComment, extremelyLongPros, extremelyLongCons, true
        );

        // then
        assertThat(superReview.getTitle()).hasSize(1000);
        assertThat(superReview.getDetailedComment()).hasSize(10000);
        assertThat(superReview.getPros()).hasSize(5000);
        assertThat(superReview.getCons()).hasSize(5000);
        assertThat(superReview.getOverallRating()).isEqualTo(1);
        assertThat(superReview.getScriptRating()).isEqualTo(10);
    }
}
