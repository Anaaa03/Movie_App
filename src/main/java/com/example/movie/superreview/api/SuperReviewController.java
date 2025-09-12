package com.example.movie.superreview.api;

import com.example.movie.superreview.api.model.AddSuperReviewRequest;
import com.example.movie.superreview.api.model.SuperReviewResponse;
import com.example.movie.superreview.domain.AddSuperReviewUseCase;
import com.example.movie.user.domain.SessionService;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.movie.superreview.api.SuperReviewResponseMapper.mapToSuperReviewResponse;

@RestController
@RequestMapping("/api/super-reviews")
public class SuperReviewController {
    private final AddSuperReviewUseCase addSuperReviewUseCase;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public SuperReviewController(AddSuperReviewUseCase addSuperReviewUseCase,
                                 SessionService sessionService,
                                 UserRepository userRepository) {
        this.addSuperReviewUseCase = addSuperReviewUseCase;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<SuperReviewResponse> addSuperReview(@RequestBody AddSuperReviewRequest request,
                                                              @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);
            var superReview = addSuperReviewUseCase.addSuperReview(request, userId);
            SuperReviewResponse response = mapToSuperReviewResponse(superReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Only SUPER_REVIEWER and ADMIN users can create super reviews")) {
                return ResponseEntity.status(403).build();
            }
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{superReviewId}")
    public ResponseEntity<SuperReviewResponse> getSuperReview(@PathVariable UUID superReviewId) {
        return addSuperReviewUseCase.findById(superReviewId)
                .map(SuperReviewResponseMapper::mapToSuperReviewResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{superReviewId}")
    public ResponseEntity<SuperReviewResponse> updateSuperReview(@PathVariable UUID superReviewId,
                                                                 @RequestBody AddSuperReviewRequest request,
                                                                 @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);
            var updatedSuperReview = addSuperReviewUseCase.updateSuperReview(superReviewId, userId, request);
            SuperReviewResponse response = mapToSuperReviewResponse(updatedSuperReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("You can only edit your own super reviews")) {
                return ResponseEntity.status(403).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{superReviewId}")
    public ResponseEntity<Void> deleteSuperReview(@PathVariable UUID superReviewId,
                                                  @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);
            addSuperReviewUseCase.deleteSuperReview(superReviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("You can only delete your own super reviews")) {
                return ResponseEntity.status(403).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/admin/{superReviewId}")
    public ResponseEntity<Void> adminDeleteSuperReview(@PathVariable UUID superReviewId,
                                                       @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID userId = sessionService.getUserId(sessionId);
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isAdmin()) {
                return ResponseEntity.status(403).build();
            }

            addSuperReviewUseCase.deleteSuperReview(superReviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}