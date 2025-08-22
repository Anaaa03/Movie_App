package com.example.movie.review.persistence;

import com.example.movie.review.persistence.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    List<ReviewEntity> findByMovieId(UUID movieId);
}
