package com.example.movie.review.api;

import com.example.movie.review.api.model.AddReviewRequest;
import com.example.movie.review.api.model.ReviewResponse;
import com.example.movie.review.api.model.UpdateReviewRequest;
import com.example.movie.review.domain.AddReviewUseCase;
import com.example.movie.review.domain.model.Review;
import com.example.movie.user.domain.SessionService;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.movie.review.api.ReviewResponseMapper.mapToReviewResponse;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final AddReviewUseCase addReviewUseCase;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public ReviewController(AddReviewUseCase addReviewUseCase, SessionService sessionService, UserRepository userRepository) {
        this.addReviewUseCase = addReviewUseCase;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody AddReviewRequest request, @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);
            var review = addReviewUseCase.addReview(request, userId);
            ReviewResponse response = mapToReviewResponse(review);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable UUID reviewId) {
        try {
            return addReviewUseCase.findById(reviewId)
                    .map(review -> ResponseEntity.ok(mapToReviewResponse(review)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovie(@PathVariable UUID movieId) {
        try {
            List<Review> reviews = addReviewUseCase.findByMovieId(movieId);
            List<ReviewResponse> responses = reviews.stream()
                    .map(ReviewResponseMapper::mapToReviewResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable UUID reviewId,
                                                       @RequestBody UpdateReviewRequest request,
                                                       @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);

            if (userId == null) {
                return ResponseEntity.status(401).body(null);
            }

            Review updatedReview = addReviewUseCase.updateReview(reviewId, userId, request.getComment(), request.getRating());
            ReviewResponse response = mapToReviewResponse(updatedReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("You can only edit your own reviews")) {
                return ResponseEntity.status(403).body(null);
            }
            if (e.getMessage().contains("Review not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId,
                                             @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);

            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            addReviewUseCase.deleteReview(reviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("You can only delete your own reviews")) {
                return ResponseEntity.status(403).build();
            }
            if (e.getMessage().contains("Review not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable UUID reviewId,
                                                  @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);

            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isAdmin()) {
                return ResponseEntity.status(403).build();
            }

            addReviewUseCase.deleteReview(reviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Review not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
