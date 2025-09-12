package com.example.movie.superreview.persistence;

import com.example.movie.superreview.domain.model.SuperReview;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SuperReviewRepository {
    private final JpaSuperReviewRepository jpaSuperReviewRepository;

    public SuperReviewRepository(JpaSuperReviewRepository jpaSuperReviewRepository) {
        this.jpaSuperReviewRepository = jpaSuperReviewRepository;
    }

    public SuperReview save(SuperReview superReview) {
        var saved = jpaSuperReviewRepository.save(SuperReviewEntityMapper.toEntity(superReview));
        return SuperReviewEntityMapper.toDomain(saved);
    }

    public Optional<SuperReview> findById(UUID id) {
        return jpaSuperReviewRepository.findById(id)
                .map(SuperReviewEntityMapper::toDomain);
    }

    public void deleteById(UUID id) {
        jpaSuperReviewRepository.deleteById(id);
    }
}