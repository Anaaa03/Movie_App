package com.example.movie.superreview.persistence;

import com.example.movie.superreview.persistence.model.SuperReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface JpaSuperReviewRepository extends JpaRepository<SuperReviewEntity, UUID> {
}