package com.example.movie.superreview.domain;

import com.example.movie.superreview.api.model.AddSuperReviewRequest;
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
        SuperReview superReview = superReviewRepository.findById(superReviewId)
                .orElseThrow(() -> new RuntimeException("Super review not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
}