package com.example.movie.movie.persistence;

import com.example.movie.movie.domain.model.Movie;
import com.example.movie.movie.domain.model.MovieId;
import com.example.movie.movie.domain.model.Poster;
import com.example.movie.movie.persistence.model.MovieEntity;

class MovieEntityMapper {
    static Movie toDomain(MovieEntity entity){
        MovieId id = new MovieId(entity.getId());
        Poster poster = entity.getPosterImage() != null
                ? new Poster(entity.getPosterImage())
                : null;
        return Movie.builder()
                .withId(id)
                .withTitle(entity.getTitle())
                .withReleaseYear(entity.getReleaseYear())
                .withDurationMinutes(entity.getDurationMinutes())
                .withDescription(entity.getDescription())
                .withPoster(poster)
                .withTrailerUrl(entity.getTrailerUrl())
                .withCreatedAt(entity.getCreatedAt())
                .build();
    }

    static MovieEntity toEntity(Movie movie) {
        MovieEntity entity = new MovieEntity();
        entity.setId(movie.getId() != null ? movie.getId().value() : null);
        entity.setTitle(movie.getTitle());
        entity.setReleaseYear(movie.getReleaseYear());
        entity.setDurationMinutes(movie.getDurationMinutes());
        entity.setDescription(movie.getDescription());
        entity.setPosterImage(movie.getPoster() != null ? movie.getPoster().imageData() : null);
        entity.setTrailerUrl(movie.getTrailerUrl());
        entity.setCreatedAt(movie.getCreatedAt());
        return entity;
    }
}
