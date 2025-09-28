package com.example.movie.review.domain.model;

public class InvalidReviewAddRequestException extends IllegalArgumentException {
    private InvalidReviewAddRequestException(String message) {
        super(message);
    }

    public static InvalidReviewAddRequestException invalidRequest() {
        return new InvalidReviewAddRequestException("Invalid request");
    }

    public static InvalidReviewAddRequestException invalidRating() {
        return new InvalidReviewAddRequestException("Rating must be between 1 and 10");
    }

    public static InvalidReviewAddRequestException commentTooLong() {
        return new InvalidReviewAddRequestException("Comment too long");
    }

    public static InvalidReviewAddRequestException invalidMovieId() {
        return new InvalidReviewAddRequestException("Invalid movie ID");
    }
}
