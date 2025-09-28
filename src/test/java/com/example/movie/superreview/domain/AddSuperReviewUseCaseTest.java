package com.example.movie.superreview.domain;

import com.example.movie.superreview.api.model.AddSuperReviewRequest;
import com.example.movie.superreview.domain.model.InvalidSuperReviewAddRequestException;
import com.example.movie.superreview.domain.model.SuperReview;
import com.example.movie.superreview.domain.model.SuperReviewId;
import com.example.movie.superreview.persistence.SuperReviewRepository;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.domain.model.UserId;
import com.example.movie.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddSuperReviewUseCaseTest {

    private final SuperReviewRepository superReviewRepository = mock(SuperReviewRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AddSuperReviewUseCase tested = new AddSuperReviewUseCase(superReviewRepository, userRepository);

    private UUID movieId;
    private UUID superReviewerId;
    private UUID adminId;
    private UUID regularUserId;
    private UUID superReviewId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        superReviewerId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        regularUserId = UUID.randomUUID();
        superReviewId = UUID.randomUUID();

        when(superReviewRepository.save(any(SuperReview.class))).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldAddSuperReviewWhenUserIsSuperReviewer() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = createValidSuperReviewRequest();

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getMovieId()).isEqualTo(movieId);
        assertThat(result.getUserId()).isEqualTo(superReviewerId);
        assertThat(result.getOverallRating()).isEqualTo(8);
        assertThat(result.getScriptRating()).isEqualTo(7);
        assertThat(result.getActingRating()).isEqualTo(9);
        assertThat(result.getEffectsRating()).isEqualTo(8);
        assertThat(result.getMusicRating()).isEqualTo(6);
        assertThat(result.getTitle()).isEqualTo("Excellent Movie");
        assertThat(result.getDetailedComment()).isEqualTo("This is a detailed review...");
        assertThat(result.getPros()).isEqualTo("Great acting, good story");
        assertThat(result.getCons()).isEqualTo("Music could be better");
        assertThat(result.getRecommendation()).isTrue();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNull();

        verify(superReviewRepository).save(any(SuperReview.class));
    }

    @Test
    void shouldAddSuperReviewWhenUserIsAdmin() {
        // given
        User admin = createUserWithRole("ADMIN", adminId);
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        AddSuperReviewRequest request = createValidSuperReviewRequest();

        // when
        SuperReview result = tested.addSuperReview(request, adminId);

        // then
        assertThat(result.getMovieId()).isEqualTo(movieId);
        assertThat(result.getUserId()).isEqualTo(adminId);
        assertThat(result.getOverallRating()).isEqualTo(8);
        verify(superReviewRepository).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenRegularUserTriesToCreateSuperReview() {
        // given
        User regularUser = createUserWithRole("USER", regularUserId);
        when(userRepository.findById(regularUserId)).thenReturn(Optional.of(regularUser));

        AddSuperReviewRequest request = createValidSuperReviewRequest();

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, regularUserId));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Only SUPER_REVIEWER and ADMIN users can create super reviews");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        AddSuperReviewRequest request = createValidSuperReviewRequest();

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, nonExistentUserId));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldFindSuperReviewById() {
        // given
        SuperReview expectedSuperReview = createSuperReview();
        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(expectedSuperReview));

        // when
        Optional<SuperReview> result = tested.findById(superReviewId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedSuperReview);
    }

    @Test
    void shouldReturnEmptyWhenSuperReviewNotFound() {
        // given
        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.empty());

        // when
        Optional<SuperReview> result = tested.findById(superReviewId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteOwnSuperReview() {
        // given
        SuperReview superReview = createSuperReview();
        User owner = createUserWithRole("SUPER_REVIEWER", superReviewerId);

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(superReview));
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(owner));

        // when
        tested.deleteSuperReview(superReviewId, superReviewerId);

        // then
        verify(superReviewRepository).deleteById(superReviewId);
    }

    @Test
    void shouldDeleteSuperReviewWhenUserIsAdmin() {
        // given
        SuperReview superReview = createSuperReview();
        User admin = createUserWithRole("ADMIN", adminId);

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(superReview));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // when
        tested.deleteSuperReview(superReviewId, adminId);

        // then
        verify(superReviewRepository).deleteById(superReviewId);
    }

    @Test
    void shouldThrowExceptionWhenTryingToDeleteOtherUsersSuperReview() {
        // given
        SuperReview superReview = createSuperReview();
        User otherUser = createUserWithRole("SUPER_REVIEWER", UUID.randomUUID());

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(superReview));
        when(userRepository.findById(otherUser.getId().value())).thenReturn(Optional.of(otherUser));

        // when
        Throwable caught = catchThrowable(() -> tested.deleteSuperReview(superReviewId, otherUser.getId().value()));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("You can only delete your own super reviews");
        verify(superReviewRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingSuperReviewThatDoesNotExist() {
        // given
        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.empty());

        // when
        Throwable caught = catchThrowable(() -> tested.deleteSuperReview(superReviewId, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Super review not found");
        verify(superReviewRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingUserNotFound() {
        // given
        SuperReview superReview = createSuperReview();
        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(superReview));
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.empty());

        // when
        Throwable caught = catchThrowable(() -> tested.deleteSuperReview(superReviewId, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        verify(superReviewRepository, never()).deleteById(any());
    }

    @Test
    void shouldUpdateOwnSuperReview() {
        // given
        SuperReview originalSuperReview = createSuperReview();
        User owner = createUserWithRole("SUPER_REVIEWER", superReviewerId);

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(originalSuperReview));
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(owner));

        AddSuperReviewRequest updateRequest = createUpdatedSuperReviewRequest();

        // when
        SuperReview result = tested.updateSuperReview(superReviewId, superReviewerId, updateRequest);

        // then
        assertThat(result.getOverallRating()).isEqualTo(9);
        assertThat(result.getScriptRating()).isEqualTo(8);
        assertThat(result.getActingRating()).isEqualTo(10);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDetailedComment()).isEqualTo("Updated detailed comment");
        assertThat(result.getPros()).isEqualTo("Updated pros");
        assertThat(result.getCons()).isEqualTo("Updated cons");
        assertThat(result.getRecommendation()).isFalse();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isEqualTo(originalSuperReview.getCreatedAt());
        assertThat(result.getId()).isEqualTo(originalSuperReview.getId());
        assertThat(result.getMovieId()).isEqualTo(originalSuperReview.getMovieId());
        assertThat(result.getUserId()).isEqualTo(originalSuperReview.getUserId());

        verify(superReviewRepository).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToUpdateOtherUsersSuperReview() {
        // given
        SuperReview superReview = createSuperReview();
        User otherUser = createUserWithRole("SUPER_REVIEWER", UUID.randomUUID());

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(superReview));
        when(userRepository.findById(otherUser.getId().value())).thenReturn(Optional.of(otherUser));

        AddSuperReviewRequest updateRequest = createUpdatedSuperReviewRequest();

        // when
        Throwable caught = catchThrowable(() -> tested.updateSuperReview(superReviewId, otherUser.getId().value(), updateRequest));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("You can only edit your own super reviews");
        verify(superReviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSuperReviewThatDoesNotExist() {
        // given
        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.empty());

        AddSuperReviewRequest updateRequest = createUpdatedSuperReviewRequest();

        // when
        Throwable caught = catchThrowable(() -> tested.updateSuperReview(superReviewId, superReviewerId, updateRequest));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Super review not found");
        verify(superReviewRepository, never()).save(any());
    }

    @Test
    void shouldCreateSuperReviewWithAllNullOptionalFields() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, null, null, null, null, null
        );

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getTitle()).isNull();
        assertThat(result.getDetailedComment()).isNull();
        assertThat(result.getPros()).isNull();
        assertThat(result.getCons()).isNull();
        assertThat(result.getRecommendation()).isNull();
        assertThat(result.getOverallRating()).isEqualTo(8);
    }

    @Test
    void shouldCreateSuperReviewWithMinimumRatings() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 1, 1, 1, 1, 1, "Bad Movie", "Really bad", "None", "Everything", false
        );

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getOverallRating()).isEqualTo(1);
        assertThat(result.getScriptRating()).isEqualTo(1);
        assertThat(result.getActingRating()).isEqualTo(1);
        assertThat(result.getEffectsRating()).isEqualTo(1);
        assertThat(result.getMusicRating()).isEqualTo(1);
        assertThat(result.getRecommendation()).isFalse();
    }

    @Test
    void shouldCreateSuperReviewWithMaximumRatings() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 10, 10, 10, 10, 10, "Perfect Movie", "Absolutely perfect", "Everything", "Nothing", true
        );

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getOverallRating()).isEqualTo(10);
        assertThat(result.getScriptRating()).isEqualTo(10);
        assertThat(result.getActingRating()).isEqualTo(10);
        assertThat(result.getEffectsRating()).isEqualTo(10);
        assertThat(result.getMusicRating()).isEqualTo(10);
        assertThat(result.getRecommendation()).isTrue();
    }

    //VALIDATION TESTS

    @Test
    void shouldThrowExceptionWhenAddSuperReviewRequestIsNull() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(null, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Invalid request");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenMovieIdIsNull() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                null, 8, 7, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Invalid movie ID");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenOverallRatingTooLow() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 0, 7, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Overall rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenOverallRatingTooHigh() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 11, 7, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Overall rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenScriptRatingTooLow() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 0, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Script rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenScriptRatingTooHigh() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 11, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Script rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenActingRatingTooLow() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 0, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Acting rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenActingRatingTooHigh() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 11, 8, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Acting rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenEffectsRatingTooLow() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 0, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Effects rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenEffectsRatingTooHigh() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 11, 6, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Effects rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenMusicRatingTooLow() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 0, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Music rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenMusicRatingTooHigh() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 11, "Great Movie", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Music rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, "   ", "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Invalid title");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenTitleTooLong() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        String longTitle = "A".repeat(201); // 201 characters
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, longTitle, "Detailed comment", "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Title too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenDetailedCommentTooLong() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        String longComment = "A".repeat(5001); // 5001 characters
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, "Great Movie", longComment, "Pros", "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Detailed comment too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenProsTooLong() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        String longPros = "A".repeat(2001); // 2001 characters
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, "Great Movie", "Detailed comment", longPros, "Cons", true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Pros section too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenConsTooLong() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        String longCons = "A".repeat(2001); // 2001 characters
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, 7, 9, 8, 6, "Great Movie", "Detailed comment", "Pros", longCons, true
        );

        // when
        Throwable caught = catchThrowable(() -> tested.addSuperReview(request, superReviewerId));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Cons section too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldAcceptValidBoundaryValues() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        String maxTitle = "A".repeat(200); // Exactly 200 characters
        String maxDetailedComment = "B".repeat(5000); // Exactly 5000 characters
        String maxPros = "C".repeat(2000); // Exactly 2000 characters
        String maxCons = "D".repeat(2000); // Exactly 2000 characters

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 1, 1, 1, 1, 1, maxTitle, maxDetailedComment, maxPros, maxCons, false
        );

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getTitle()).hasSize(200);
        assertThat(result.getDetailedComment()).hasSize(5000);
        assertThat(result.getPros()).hasSize(2000);
        assertThat(result.getCons()).hasSize(2000);
        assertThat(result.getOverallRating()).isEqualTo(1);
        verify(superReviewRepository).save(any(SuperReview.class));
    }

    @Test
    void shouldAcceptNullOptionalRatings() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", superReviewerId);
        when(userRepository.findById(superReviewerId)).thenReturn(Optional.of(superReviewer));

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 8, null, null, null, null, "Great Movie", "Comment", "Pros", "Cons", true
        );

        // when
        SuperReview result = tested.addSuperReview(request, superReviewerId);

        // then
        assertThat(result.getOverallRating()).isEqualTo(8);
        assertThat(result.getScriptRating()).isNull();
        assertThat(result.getActingRating()).isNull();
        assertThat(result.getEffectsRating()).isNull();
        assertThat(result.getMusicRating()).isNull();
        verify(superReviewRepository).save(any(SuperReview.class));
    }

    //UPDATE VALIDATION TESTS

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidOverallRating() {
        // given
        SuperReview originalSuperReview = createSuperReview();

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(originalSuperReview));

        AddSuperReviewRequest updateRequest = new AddSuperReviewRequest(
                movieId, 11, 8, 10, 9, 7, "Updated Title", "Updated comment", "Pros", "Cons", false
        );

        // when
        Throwable caught = catchThrowable(() -> tested.updateSuperReview(superReviewId, superReviewerId, updateRequest));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Overall rating must be between 1 and 10");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithTooLongTitle() {
        // given
        SuperReview originalSuperReview = createSuperReview();

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(originalSuperReview));

        String tooLongTitle = "A".repeat(201);
        AddSuperReviewRequest updateRequest = new AddSuperReviewRequest(
                movieId, 9, 8, 10, 9, 7, tooLongTitle, "Updated comment", "Pros", "Cons", false
        );

        // when
        Throwable caught = catchThrowable(() -> tested.updateSuperReview(superReviewId, superReviewerId, updateRequest));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Title too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithTooLongDetailedComment() {
        // given
        SuperReview originalSuperReview = createSuperReview();

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(originalSuperReview));

        String tooLongComment = "A".repeat(5001);
        AddSuperReviewRequest updateRequest = new AddSuperReviewRequest(
                movieId, 9, 8, 10, 9, 7, "Updated Title", tooLongComment, "Pros", "Cons", false
        );

        // when
        Throwable caught = catchThrowable(() -> tested.updateSuperReview(superReviewId, superReviewerId, updateRequest));

        // then
        assertThat(caught).isInstanceOf(InvalidSuperReviewAddRequestException.class)
                .hasMessage("Detailed comment too long");
        verify(superReviewRepository, never()).save(any(SuperReview.class));
    }

    @Test
    void shouldAllowUpdatingWithNullValues() {
        // given
        SuperReview originalSuperReview = createSuperReview();

        when(superReviewRepository.findById(superReviewId)).thenReturn(Optional.of(originalSuperReview));

        AddSuperReviewRequest updateRequest = new AddSuperReviewRequest(
                movieId, null, null, null, null, null, null, null, null, null, null
        );

        // when
        SuperReview result = tested.updateSuperReview(superReviewId, superReviewerId, updateRequest);

        // then
        assertThat(result.getOverallRating()).isNull();
        assertThat(result.getScriptRating()).isNull();
        assertThat(result.getActingRating()).isNull();
        assertThat(result.getEffectsRating()).isNull();
        assertThat(result.getMusicRating()).isNull();
        assertThat(result.getTitle()).isNull();
        assertThat(result.getDetailedComment()).isNull();
        assertThat(result.getPros()).isNull();
        assertThat(result.getCons()).isNull();
        assertThat(result.getRecommendation()).isNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(superReviewRepository).save(any(SuperReview.class));
    }


    private User createUserWithRole(String role, UUID userId) {
        return User.builder()
                .withId(new UserId(userId))
                .withUsername("testuser")
                .withEmail("test@example.com")
                .withPasswordHash("hashedPassword")
                .withRole(role)
                .withCreatedAt(Instant.now())
                .build();
    }

    private AddSuperReviewRequest createValidSuperReviewRequest() {
        return new AddSuperReviewRequest(
                movieId,
                8,
                7,
                9,
                8,
                6,
                "Excellent Movie",
                "This is a detailed review...",
                "Great acting, good story",
                "Music could be better",
                true
        );
    }

    private AddSuperReviewRequest createUpdatedSuperReviewRequest() {
        return new AddSuperReviewRequest(
                movieId,
                9,
                8,
                10,
                9,
                7,
                "Updated Title",
                "Updated detailed comment",
                "Updated pros",
                "Updated cons",
                false
        );
    }

    private SuperReview createSuperReview() {
        return SuperReview.builder()
                .withId(new SuperReviewId(superReviewId))
                .withMovieId(movieId)
                .withUserId(superReviewerId)
                .withOverallRating(8)
                .withScriptRating(7)
                .withActingRating(9)
                .withEffectsRating(8)
                .withMusicRating(6)
                .withTitle("Test Review")
                .withDetailedComment("Test comment")
                .withPros("Test pros")
                .withCons("Test cons")
                .withRecommendation(true)
                .withCreatedAt(Instant.now().minusSeconds(3600))
                .withUpdatedAt(null)
                .build();
    }
}
