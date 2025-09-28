package com.example.movie.superreview.domain;

import com.example.movie.superreview.api.model.AddSuperReviewRequest;
import com.example.movie.superreview.domain.model.InvalidSuperReviewAddRequestException;
import com.example.movie.superreview.domain.model.SuperReview;
import com.example.movie.superreview.persistence.SuperReviewRepository;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddSuperReviewUseCase {
    private final SuperReviewRepository superReviewRepository;
    private final UserRepository userRepository;

    public AddSuperReviewUseCase(SuperReviewRepository superReviewRepository, UserRepository userRepository) {
        this.superReviewRepository = superReviewRepository;
        this.userRepository = userRepository;
    }

    public SuperReview addSuperReview(AddSuperReviewRequest request, UUID userId) {
        validateSuperReviewAddRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isSuperReviewer() && !user.isAdmin()) {
            throw new RuntimeException("Only SUPER_REVIEWER and ADMIN users can create super reviews");
        }

        SuperReview superReview = SuperReview.addNewSuperReview(
                request.getMovieId(),
                userId,
                request.getOverallRating(),
                request.getScriptRating(),
                request.getActingRating(),
                request.getEffectsRating(),
                request.getMusicRating(),
                request.getTitle(),
                request.getDetailedComment(),
                request.getPros(),
                request.getCons(),
                request.getRecommendation()
        );

        return superReviewRepository.save(superReview);
    }

    public Optional<SuperReview> findById(UUID superReviewId) {
        return superReviewRepository.findById(superReviewId);
    }

    public void deleteSuperReview(UUID superReviewId, UUID userId) {
        SuperReview superReview = superReviewRepository.findById(superReviewId)
                .orElseThrow(() -> new RuntimeException("Super review not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!superReview.getUserId().equals(userId) && !user.isAdmin()) {
            throw new RuntimeException("You can only delete your own super reviews");
        }

        superReviewRepository.deleteById(superReviewId);
    }

    public SuperReview updateSuperReview(UUID superReviewId, UUID userId, AddSuperReviewRequest request) {
        validateSuperReviewUpdateData(request);

        SuperReview superReview = superReviewRepository.findById(superReviewId)
                .orElseThrow(() -> new RuntimeException("Super review not found"));

        if (!superReview.getUserId().equals(userId)) {
            throw new RuntimeException("You can only edit your own super reviews");
        }

        SuperReview updatedSuperReview = superReview.updateSuperReview(
                request.getOverallRating(),
                request.getScriptRating(),
                request.getActingRating(),
                request.getEffectsRating(),
                request.getMusicRating(),
                request.getTitle(),
                request.getDetailedComment(),
                request.getPros(),
                request.getCons(),
                request.getRecommendation()
        );

        return superReviewRepository.save(updatedSuperReview);
    }

    private void validateSuperReviewAddRequest(AddSuperReviewRequest request) {
        if (request == null) {
            throw InvalidSuperReviewAddRequestException.invalidRequest();
        }

        if (request.getMovieId() == null) {
            throw InvalidSuperReviewAddRequestException.invalidMovieId();
        }

        validateRatings(request);
        validateTextFields(request);
    }

    private void validateSuperReviewUpdateData(AddSuperReviewRequest request) {
        if (request != null) {
            validateRatings(request);
            validateTextFields(request);
        }
    }

    private void validateRatings(AddSuperReviewRequest request) {
        if (request.getOverallRating() != null && (request.getOverallRating() < 1 || request.getOverallRating() > 10)) {
            throw InvalidSuperReviewAddRequestException.invalidOverallRating();
        }

        if (request.getScriptRating() != null && (request.getScriptRating() < 1 || request.getScriptRating() > 10)) {
            throw InvalidSuperReviewAddRequestException.invalidScriptRating();
        }

        if (request.getActingRating() != null && (request.getActingRating() < 1 || request.getActingRating() > 10)) {
            throw InvalidSuperReviewAddRequestException.invalidActingRating();
        }

        if (request.getEffectsRating() != null && (request.getEffectsRating() < 1 || request.getEffectsRating() > 10)) {
            throw InvalidSuperReviewAddRequestException.invalidEffectsRating();
        }

        if (request.getMusicRating() != null && (request.getMusicRating() < 1 || request.getMusicRating() > 10)) {
            throw InvalidSuperReviewAddRequestException.invalidMusicRating();
        }
    }

    private void validateTextFields(AddSuperReviewRequest request) {
        if (request.getTitle() != null) {
            if (request.getTitle().trim().isEmpty()) {
                throw InvalidSuperReviewAddRequestException.invalidTitle();
            }
            if (request.getTitle().length() > 200) {
                throw InvalidSuperReviewAddRequestException.titleTooLong();
            }
        }

        if (request.getDetailedComment() != null && request.getDetailedComment().length() > 5000) {
            throw InvalidSuperReviewAddRequestException.detailedCommentTooLong();
        }

        if (request.getPros() != null && request.getPros().length() > 2000) {
            throw InvalidSuperReviewAddRequestException.prosTooLong();
        }

        if (request.getCons() != null && request.getCons().length() > 2000) {
            throw InvalidSuperReviewAddRequestException.consTooLong();
        }
    }

}