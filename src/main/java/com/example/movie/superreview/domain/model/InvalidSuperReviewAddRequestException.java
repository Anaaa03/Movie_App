package com.example.movie.superreview.domain.model;

public class InvalidSuperReviewAddRequestException extends IllegalArgumentException {
    private InvalidSuperReviewAddRequestException(String message) {
        super(message);
    }

    public static InvalidSuperReviewAddRequestException invalidRequest() {
        return new InvalidSuperReviewAddRequestException("Invalid request");
    }

    public static InvalidSuperReviewAddRequestException invalidMovieId() {
        return new InvalidSuperReviewAddRequestException("Invalid movie ID");
    }

    public static InvalidSuperReviewAddRequestException invalidOverallRating() {
        return new InvalidSuperReviewAddRequestException("Overall rating must be between 1 and 10");
    }

    public static InvalidSuperReviewAddRequestException invalidScriptRating() {
        return new InvalidSuperReviewAddRequestException("Script rating must be between 1 and 10");
    }

    public static InvalidSuperReviewAddRequestException invalidActingRating() {
        return new InvalidSuperReviewAddRequestException("Acting rating must be between 1 and 10");
    }

    public static InvalidSuperReviewAddRequestException invalidEffectsRating() {
        return new InvalidSuperReviewAddRequestException("Effects rating must be between 1 and 10");
    }

    public static InvalidSuperReviewAddRequestException invalidMusicRating() {
        return new InvalidSuperReviewAddRequestException("Music rating must be between 1 and 10");
    }

    public static InvalidSuperReviewAddRequestException invalidTitle() {
        return new InvalidSuperReviewAddRequestException("Invalid title");
    }

    public static InvalidSuperReviewAddRequestException titleTooLong() {
        return new InvalidSuperReviewAddRequestException("Title too long");
    }

    public static InvalidSuperReviewAddRequestException detailedCommentTooLong() {
        return new InvalidSuperReviewAddRequestException("Detailed comment too long");
    }

    public static InvalidSuperReviewAddRequestException prosTooLong() {
        return new InvalidSuperReviewAddRequestException("Pros section too long");
    }

    public static InvalidSuperReviewAddRequestException consTooLong() {
        return new InvalidSuperReviewAddRequestException("Cons section too long");
    }
}
