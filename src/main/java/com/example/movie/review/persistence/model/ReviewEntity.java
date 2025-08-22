package com.example.movie.review.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"movie_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {
    @Id
    private UUID id;

    private UUID movieId;

    private UUID userId;

    private Integer rating;

    private String comment;

    private Instant createdAt;

    private Instant updatedAt;
} 