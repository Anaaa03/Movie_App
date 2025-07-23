package com.example.movie.movie.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MovieEntity {
    @Id
    private UUID id;

    private String title;

    private Integer releaseYear;

    private Integer durationMinutes;

    private String description;

    private byte[] posterImage;

    private String trailerUrl;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

}
