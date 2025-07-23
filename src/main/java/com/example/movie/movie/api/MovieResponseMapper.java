package com.example.movie.movie.api;

import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.movie.domain.model.Movie;

class MovieResponseMapper {
    static MovieResponse mapToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId().value())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .durationMinutes(movie.getDurationMinutes())
                .description(movie.getDescription())
                .trailerUrl(movie.getTrailerUrl())
                .posterUrl(movie.getPoster() != null ? "/api/movies/" + movie.getId().value() + "/poster" : null)
                .build();
    }
}
