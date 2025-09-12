package com.example.movie.superreview.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "super_reviews")
@Data
@NoArgsConstructor
public class SuperReviewEntity {
    @Id
    private UUID id;

    private UUID movieId;

    private UUID userId;

    private Integer overallRating;

    private Integer scriptRating;

    private Integer actingRating;

    private Integer effectsRating;

    private Integer musicRating;

    private String title;

    private String detailedComment;

    private String pros;

    private String cons;

    private Boolean recommendation;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}