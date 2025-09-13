package com.example.movie.movie.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
public class AddMovieRequest {
    private final String title;
    private final Integer releaseYear;
    private final Integer durationMinutes;
    private final String description;
    private final String trailerUrl;

    public AddMovieRequest(String title, Integer releaseYear, Integer durationMinutes, String description, String trailerUrl) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.trailerUrl = trailerUrl;
    }
}
