package com.example.movie.movie.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MovieTest {

    @Test
    void shouldCreateNewMovieWithAllFields() {
        // given
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;
        String description = "A great test movie";
        String trailerUrl = "https://youtube.com/watch?v=test";
        Poster poster = new Poster("poster data".getBytes());

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, poster);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movie.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movie.getDescription()).isEqualTo(description);
        assertThat(movie.getTrailerUrl()).isEqualTo(trailerUrl);
        assertThat(movie.getPoster()).isEqualTo(poster);
        assertThat(movie.getId()).isNotNull();
        assertThat(movie.getCreatedAt()).isNotNull();
        assertThat(movie.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldCreateNewMovieWithoutPoster() {
        // given
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;
        String description = "A great test movie";
        String trailerUrl = "https://youtube.com/watch?v=test";

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movie.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movie.getDescription()).isEqualTo(description);
        assertThat(movie.getTrailerUrl()).isEqualTo(trailerUrl);
        assertThat(movie.getPoster()).isNull();
        assertThat(movie.getId()).isNotNull();
        assertThat(movie.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateNewMovieWithNullOptionalFields() {
        // given
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movie.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movie.getDescription()).isNull();
        assertThat(movie.getTrailerUrl()).isNull();
        assertThat(movie.getPoster()).isNull();
        assertThat(movie.getId()).isNotNull();
        assertThat(movie.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateMovieWithUniqueId() {
        // given
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;

        // when
        Movie movie1 = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);
        Movie movie2 = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie1.getId()).isNotEqualTo(movie2.getId());
    }

    @Test
    void shouldCreateMovieWithDifferentCreatedAtTimes() {
        // given
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;

        // when
        Movie movie1 = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Movie movie2 = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie1.getCreatedAt()).isNotEqualTo(movie2.getCreatedAt());
        assertThat(movie1.getCreatedAt()).isBefore(movie2.getCreatedAt());
    }

    @Test
    void shouldAddPosterToExistingMovie() {
        // given
        Movie originalMovie = Movie.addNewMovie("Test Movie", 2023, 120, "Description", null, null);
        Poster newPoster = new Poster("new poster data".getBytes());

        // when
        Movie movieWithPoster = originalMovie.addPoster(newPoster);

        // then
        assertThat(movieWithPoster.getPoster()).isEqualTo(newPoster);
        assertThat(movieWithPoster.getTitle()).isEqualTo(originalMovie.getTitle());
        assertThat(movieWithPoster.getReleaseYear()).isEqualTo(originalMovie.getReleaseYear());
        assertThat(movieWithPoster.getDurationMinutes()).isEqualTo(originalMovie.getDurationMinutes());
        assertThat(movieWithPoster.getDescription()).isEqualTo(originalMovie.getDescription());
        assertThat(movieWithPoster.getTrailerUrl()).isEqualTo(originalMovie.getTrailerUrl());
        assertThat(movieWithPoster.getId()).isEqualTo(originalMovie.getId());
        assertThat(movieWithPoster.getCreatedAt()).isEqualTo(originalMovie.getCreatedAt());

        assertThat(originalMovie.getPoster()).isNull();

        assertThat(movieWithPoster).isNotSameAs(originalMovie);
    }

    @Test
    void shouldReplacePosterWhenAddingNewPoster() {
        // given
        Poster originalPoster = new Poster("original poster".getBytes());
        Movie movieWithPoster = Movie.addNewMovie("Test Movie", 2023, 120, "Description", null, originalPoster);

        Poster newPoster = new Poster("new poster data".getBytes());

        // when
        Movie movieWithNewPoster = movieWithPoster.addPoster(newPoster);

        // then
        assertThat(movieWithNewPoster.getPoster()).isEqualTo(newPoster);
        assertThat(movieWithNewPoster.getPoster()).isNotEqualTo(originalPoster);

        assertThat(movieWithPoster.getPoster()).isEqualTo(originalPoster);
    }

    @Test
    void shouldAddNullPosterToMovie() {
        // given
        Poster originalPoster = new Poster("original poster".getBytes());
        Movie movieWithPoster = Movie.addNewMovie("Test Movie", 2023, 120, "Description", null, originalPoster);

        // when
        Movie movieWithoutPoster = movieWithPoster.addPoster(null);

        // then
        assertThat(movieWithoutPoster.getPoster()).isNull();
        assertThat(movieWithoutPoster.getTitle()).isEqualTo(movieWithPoster.getTitle());
        assertThat(movieWithoutPoster.getId()).isEqualTo(movieWithPoster.getId());

        assertThat(movieWithPoster.getPoster()).isEqualTo(originalPoster);
    }

    @Test
    void shouldPreserveAllFieldsWhenAddingPoster() {
        // given
        MovieId movieId = new MovieId(UUID.randomUUID());
        String title = "Test Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;
        String description = "Test description";
        String trailerUrl = "https://test.com";
        Instant createdAt = Instant.now().minusSeconds(3600);

        Movie originalMovie = Movie.builder()
                .withId(movieId)
                .withTitle(title)
                .withReleaseYear(releaseYear)
                .withDurationMinutes(durationMinutes)
                .withDescription(description)
                .withTrailerUrl(trailerUrl)
                .withPoster(null)
                .withCreatedAt(createdAt)
                .build();

        Poster newPoster = new Poster("poster data".getBytes());

        // when
        Movie movieWithPoster = originalMovie.addPoster(newPoster);

        // then
        assertThat(movieWithPoster.getId()).isEqualTo(movieId);
        assertThat(movieWithPoster.getTitle()).isEqualTo(title);
        assertThat(movieWithPoster.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movieWithPoster.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movieWithPoster.getDescription()).isEqualTo(description);
        assertThat(movieWithPoster.getTrailerUrl()).isEqualTo(trailerUrl);
        assertThat(movieWithPoster.getCreatedAt()).isEqualTo(createdAt);
        assertThat(movieWithPoster.getPoster()).isEqualTo(newPoster);
    }

    @Test
    void shouldCreateMovieWithMinimumRequiredFields() {
        // given
        String title = "Minimum Movie";
        Integer releaseYear = 1900;
        Integer durationMinutes = 1;

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movie.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movie.getDescription()).isNull();
        assertThat(movie.getTrailerUrl()).isNull();
        assertThat(movie.getPoster()).isNull();
    }

    @Test
    void shouldCreateMovieWithMaximumValues() {
        // given
        String title = "Maximum Movie";
        Integer releaseYear = 2030;
        Integer durationMinutes = 500;
        String description = "A".repeat(2000); // Maximum description length
        String trailerUrl = "https://youtube.com/watch?v=maxtest";

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getReleaseYear()).isEqualTo(releaseYear);
        assertThat(movie.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(movie.getDescription()).isEqualTo(description);
        assertThat(movie.getTrailerUrl()).isEqualTo(trailerUrl);
    }

    @Test
    void shouldCreateMovieWithEmptyStringFields() {
        // given
        String title = "Empty Fields Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;
        String description = "";
        String trailerUrl = "";

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getDescription()).isEqualTo("");
        assertThat(movie.getTrailerUrl()).isEqualTo("");
    }

    @Test
    void shouldCreateMovieWithWhitespaceFields() {
        // given
        String title = "Whitespace Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;
        String description = "   ";
        String trailerUrl = "   ";

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getDescription()).isEqualTo("   ");
        assertThat(movie.getTrailerUrl()).isEqualTo("   ");
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {
        // given
        String title = "Special Movie: The Sequel! (2023) - Director's Cut";
        String description = "A movie with special chars: àáâãäåæçèéêë & symbols: @#$%^&*()";
        String trailerUrl = "https://youtube.com/watch?v=special_chars_123";

        // when
        Movie movie = Movie.addNewMovie(title, 2023, 120, description, trailerUrl, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(title);
        assertThat(movie.getDescription()).isEqualTo(description);
        assertThat(movie.getTrailerUrl()).isEqualTo(trailerUrl);
    }

    @Test
    void shouldHandleLongTitle() {
        // given
        String longTitle = "A".repeat(500);
        Integer releaseYear = 2023;
        Integer durationMinutes = 120;

        // when
        Movie movie = Movie.addNewMovie(longTitle, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie.getTitle()).isEqualTo(longTitle);
        assertThat(movie.getTitle()).hasSize(500);
    }

    @Test
    void shouldCreateMovieWithZeroDuration() {
        // given
        String title = "Zero Duration Movie";
        Integer releaseYear = 2023;
        Integer durationMinutes = 0;

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie.getDurationMinutes()).isEqualTo(0);
    }

    @Test
    void shouldCreateMovieWithNegativeYear() {
        // given
        String title = "Ancient Movie";
        Integer releaseYear = -100;
        Integer durationMinutes = 120;

        // when
        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, null, null, null);

        // then
        assertThat(movie.getReleaseYear()).isEqualTo(-100);
    }

    @Test
    void shouldCreateMovieWithLargeImagePoster() {
        // given
        String title = "Large Poster Movie";
        byte[] largeImageData = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeImageData.length; i++) {
            largeImageData[i] = (byte) (i % 256);
        }
        Poster largePoster = new Poster(largeImageData);

        // when
        Movie movie = Movie.addNewMovie(title, 2023, 120, null, null, largePoster);

        // then
        assertThat(movie.getPoster()).isEqualTo(largePoster);
        assertThat(movie.getPoster().imageData()).hasSize(1024 * 1024);
    }

    @Test
    void shouldCreateMovieWithEmptyPosterData() {
        // given
        String title = "Empty Poster Movie";
        Poster emptyPoster = new Poster(new byte[0]);

        // when
        Movie movie = Movie.addNewMovie(title, 2023, 120, null, null, emptyPoster);

        // then
        assertThat(movie.getPoster()).isEqualTo(emptyPoster);
        assertThat(movie.getPoster().imageData()).isEmpty();
    }

    @Test
    void shouldMultipleAddPosterOperations() {
        // given
        Movie originalMovie = Movie.addNewMovie("Test Movie", 2023, 120, null, null, null);

        Poster poster1 = new Poster("poster1".getBytes());
        Poster poster2 = new Poster("poster2".getBytes());
        Poster poster3 = new Poster("poster3".getBytes());

        // when
        Movie movie1 = originalMovie.addPoster(poster1);
        Movie movie2 = movie1.addPoster(poster2);
        Movie movie3 = movie2.addPoster(poster3);

        // then
        assertThat(originalMovie.getPoster()).isNull();
        assertThat(movie1.getPoster()).isEqualTo(poster1);
        assertThat(movie2.getPoster()).isEqualTo(poster2);
        assertThat(movie3.getPoster()).isEqualTo(poster3);

        assertThat(movie3.getId()).isEqualTo(originalMovie.getId());
        assertThat(movie3.getTitle()).isEqualTo(originalMovie.getTitle());
        assertThat(movie3.getCreatedAt()).isEqualTo(originalMovie.getCreatedAt());
    }
}
