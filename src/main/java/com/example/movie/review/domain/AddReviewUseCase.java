package com.example.movie.review.domain;

import com.example.movie.review.api.model.AddReviewRequest;
import com.example.movie.review.domain.model.Review;
import com.example.movie.review.persistence.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddReviewUseCase {
    private final ReviewRepository reviewRepository;

    public AddReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(AddReviewRequest addReviewRequest, UUID userId) {
        UUID movieId = addReviewRequest.getMovieId();
        Integer rating = addReviewRequest.getRating();
        String comment = addReviewRequest.getComment();

        Review review = Review.addNewReview(movieId, userId, rating, comment);

        return reviewRepository.save(review);
    }

    public Optional<Review> findById(UUID reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public List<Review> findByMovieId(UUID movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        reviewRepository.deleteById(reviewId);
    }

    public Review updateReview(UUID reviewId, UUID userId, String newComment, Integer newRating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only edit your own reviews");
        }

        Review updatedReview = review.updateReview(newComment, newRating);
        return reviewRepository.save(updatedReview);
    }
}
