package com.example.movie.movie.domain.model;

public class InvalidMovieAddRequestException extends IllegalArgumentException {
    private InvalidMovieAddRequestException(String message) {
        super(message);
    }

    public static InvalidMovieAddRequestException invalidTitle() {
        return new InvalidMovieAddRequestException("Invalid title");
    }

    public static InvalidMovieAddRequestException invalidRequest() {
        return new InvalidMovieAddRequestException("Invalid request");
    }

    public static InvalidMovieAddRequestException invalidYear() {
        return new InvalidMovieAddRequestException("Invalid year");
    }

    public static InvalidMovieAddRequestException invalidDuration() {
        return new InvalidMovieAddRequestException("Invalid minutes duration");
    }

    public static InvalidMovieAddRequestException invalidDescription() {
        return new InvalidMovieAddRequestException("Description too long");
    }

    public static InvalidMovieAddRequestException invalidTrailerUrl() {
        return new InvalidMovieAddRequestException("Invalid trailer");
    }

}
