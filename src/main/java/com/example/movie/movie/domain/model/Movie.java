package com.example.movie.movie.domain.model;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.domain.model.UserId;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;
@Builder(toBuilder = true, setterPrefix = "with")
@Data
@RequiredArgsConstructor(access = PRIVATE)
public class Movie {
    private final MovieId id;
    private final String title;
    private final Integer releaseYear;
    private final Integer durationMinutes;
    private final String description;
    private final String trailerUrl;
    private final Poster poster;
    private final Instant createdAt;


    public static Movie addNewMovie(String title, Integer releaseYear, Integer durationMinutes, String description, String trailerUrl, Poster poster) {
        MovieId id = MovieId.create();
        Instant createdAt = Instant.now();
        return Movie.builder()
                .withId(id)
                .withTitle(title)
                .withReleaseYear(releaseYear)
                .withDurationMinutes(durationMinutes)
                .withDescription(description)
                .withTrailerUrl(trailerUrl)
                .withPoster(poster)
                .withCreatedAt(createdAt)
                .build();

    }

    public Movie addPoster(Poster newPoster) {
        return this.toBuilder()
                .withPoster(newPoster)
                .build();
    }
}
