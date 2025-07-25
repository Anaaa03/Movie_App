package com.example.movie.movie.api.model;

import lombok.Data;

@Data
public class AddMovieRequest {
    private final String title;
    private final Integer releaseYear;
    private final Integer durationMinutes;
    private final String description;
    private final String trailerUrl;
}
