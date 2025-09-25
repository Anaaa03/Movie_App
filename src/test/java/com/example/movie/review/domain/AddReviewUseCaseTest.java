package com.example.movie.review.domain;

import com.example.movie.review.api.model.AddReviewRequest;
import com.example.movie.review.domain.model.Review;
import com.example.movie.review.domain.model.ReviewId;
import com.example.movie.review.persistence.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddReviewUseCaseTest {
    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
    private final AddReviewUseCase tested = new AddReviewUseCase(reviewRepository);

    private UUID movieId;
    private UUID userId;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        userId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        when(reviewRepository.save(any(Review.class))).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldAddReviewWithValidData() {
        //given
        AddReviewRequest request = new AddReviewRequest(movieId, 8, "Great movie!");

        //when
        Review actual = tested.addReview(request, userId);

        //then
        assertThat(actual.getMovieId()).isEqualTo(movieId);
        assertThat(actual.getUserId()).isEqualTo(userId);
        assertThat(actual.getRating()).isEqualTo(8);
        assertThat(actual.getComment()).isEqualTo("Great movie!");
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNull();
    }

    @Test
    void shouldAddReviewWithMinimumRating() {
        //given
        AddReviewRequest request = new AddReviewRequest(movieId, 1, "Terrible movie");

        //when
        Review actual = tested.addReview(request, userId);

        //then
        assertThat(actual.getRating()).isEqualTo(1);
        assertThat(actual.getComment()).isEqualTo("Terrible movie");
    }

    @Test
    void shouldAddReviewWithMaximumRating() {
        //given
        AddReviewRequest request = new AddReviewRequest(movieId, 10, "Perfect movie!");

        //when
        Review actual = tested.addReview(request, userId);

        //then
        assertThat(actual.getRating()).isEqualTo(10);
        assertThat(actual.getComment()).isEqualTo("Perfect movie!");
    }

    @Test
    void shouldAddReviewWithEmptyComment() {
        //given
        AddReviewRequest request = new AddReviewRequest(movieId, 5, "");

        //when
        Review actual = tested.addReview(request, userId);

        //then
        assertThat(actual.getComment()).isEqualTo("");
    }

    @Test
    void shouldAddReviewWithNullComment() {
        //given
        AddReviewRequest request = new AddReviewRequest(movieId, 5, null);

        //when
        Review actual = tested.addReview(request, userId);

        //then
        assertThat(actual.getComment()).isNull();
    }

    @Test
    void shouldFindReviewById() {
        //given
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review expectedReview = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(expectedReview));

        //when
        Optional<Review> actual = tested.findById(reviewId);

        //then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId().value()).isEqualTo(reviewId);
        assertThat(actual.get().getMovieId()).isEqualTo(movieId);
        assertThat(actual.get().getUserId()).isEqualTo(userId);
        assertThat(actual.get().getRating()).isEqualTo(8);
        assertThat(actual.get().getComment()).isEqualTo("Great movie!");
    }

    @Test
    void shouldReturnEmptyWhenReviewNotFound() {
        //given
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        //when
        Optional<Review> actual = tested.findById(reviewId);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindReviewsByMovieId() {
        //given
        UUID anotherUserId = UUID.randomUUID();
        ReviewId reviewId1 = new ReviewId(UUID.randomUUID());
        ReviewId reviewId2 = new ReviewId(UUID.randomUUID());

        Review review1 = Review.builder()
                .withId(reviewId1)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();

        Review review2 = Review.builder()
                .withId(reviewId2)
                .withMovieId(movieId)
                .withUserId(anotherUserId)
                .withRating(6)
                .withComment("Good movie")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();

        List<Review> expectedReviews = List.of(review1, review2);
        when(reviewRepository.findByMovieId(movieId)).thenReturn(expectedReviews);

        //when
        List<Review> actual = tested.findByMovieId(movieId);

        //then
        assertThat(actual).hasSize(2);
        assertThat(actual).containsExactlyInAnyOrder(review1, review2);
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsForMovie() {
        //given
        when(reviewRepository.findByMovieId(movieId)).thenReturn(List.of());

        //when
        List<Review> actual = tested.findByMovieId(movieId);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldDeleteReviewWhenUserIsOwner() {
        //given
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review review = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        tested.deleteReview(reviewId, userId);

        //then
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentReview() {
        //given
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        //when
        Throwable caught = catchThrowable(() -> tested.deleteReview(reviewId, userId));

        //then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found");
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingReviewOfAnotherUser() {
        //given
        UUID anotherUserId = UUID.randomUUID();
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review review = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(anotherUserId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        Throwable caught = catchThrowable(() -> tested.deleteReview(reviewId, userId));

        //then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("You can only delete your own reviews");
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void shouldUpdateReviewWhenUserIsOwner() {
        //given
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review originalReview = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(originalReview));

        String newComment = "Updated comment";
        Integer newRating = 9;

        //when
        Review actual = tested.updateReview(reviewId, userId, newComment, newRating);

        //then
        assertThat(actual.getComment()).isEqualTo(newComment);
        assertThat(actual.getRating()).isEqualTo(newRating);
        assertThat(actual.getUpdatedAt()).isNotNull();
        assertThat(actual.getMovieId()).isEqualTo(movieId);
        assertThat(actual.getUserId()).isEqualTo(userId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentReview() {
        //given
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        //when
        Throwable caught = catchThrowable(() -> tested.updateReview(reviewId, userId, "New comment", 9));

        //then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingReviewOfAnotherUser() {
        //given
        UUID anotherUserId = UUID.randomUUID();
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review review = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(anotherUserId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        Throwable caught = catchThrowable(() -> tested.updateReview(reviewId, userId, "New comment", 9));

        //then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("You can only edit your own reviews");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldUpdateReviewWithNullComment() {
        //given
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review originalReview = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(originalReview));

        //when
        Review actual = tested.updateReview(reviewId, userId, null, 9);

        //then
        assertThat(actual.getComment()).isNull();
        assertThat(actual.getRating()).isEqualTo(9);
        assertThat(actual.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateReviewWithEmptyComment() {
        //given
        ReviewId reviewIdObj = new ReviewId(reviewId);
        Review originalReview = Review.builder()
                .withId(reviewIdObj)
                .withMovieId(movieId)
                .withUserId(userId)
                .withRating(8)
                .withComment("Great movie!")
                .withCreatedAt(Instant.now())
                .withUpdatedAt(null)
                .build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(originalReview));

        //when
        Review actual = tested.updateReview(reviewId, userId, "", 9);

        //then
        assertThat(actual.getComment()).isEqualTo("");
        assertThat(actual.getRating()).isEqualTo(9);
        assertThat(actual.getUpdatedAt()).isNotNull();
    }
}