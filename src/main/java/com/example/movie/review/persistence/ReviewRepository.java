package com.example.movie.review.persistence;

import com.example.movie.review.domain.model.Review;
import com.example.movie.review.persistence.model.ReviewEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository {
    private final JpaReviewRepository jpaReviewRepository;

    public ReviewRepository(JpaReviewRepository jpaReviewRepository) {
        this.jpaReviewRepository = jpaReviewRepository;
    }

    public Review save(Review review) {
        ReviewEntity saved = jpaReviewRepository.save(ReviewEntityMapper.toEntity(review));
        return ReviewEntityMapper.toDomain(saved);
    }

    public Optional<Review> findById(UUID id) {
        return jpaReviewRepository.findById(id).map(ReviewEntityMapper::toDomain);
    }

    public void deleteById(UUID id) {
        jpaReviewRepository.deleteById(id);
    }

    public List<Review> findByMovieId(UUID movieId) {
        return jpaReviewRepository.findByMovieId(movieId)
                .stream()
                .map(ReviewEntityMapper::toDomain)
                .toList();
    }
}
