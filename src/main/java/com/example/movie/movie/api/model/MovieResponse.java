package com.example.movie.movie.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class MovieResponse {
    private final UUID id;
    private final String title;
    private final Integer releaseYear;
    private final Integer durationMinutes;
    private final String description;
    private final String trailerUrl;
    private final String posterUrl;
}
