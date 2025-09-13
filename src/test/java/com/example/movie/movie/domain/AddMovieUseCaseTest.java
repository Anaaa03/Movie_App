package com.example.movie.movie.domain;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.domain.model.InvalidMovieAddRequestException;
import com.example.movie.movie.domain.model.Movie;
import com.example.movie.movie.domain.model.MovieId;
import com.example.movie.movie.persistence.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddMovieUseCaseTest {
    private final MovieRepository repository = mock(MovieRepository.class);
    private final AddMovieUseCase tested = new AddMovieUseCase(repository);

    @BeforeEach
    void setUp() {
        when(repository.save(any(Movie.class))).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldAddMovieWithValidData() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Movie",
                2023,
                120,
                "A valid movie description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Movie actual = tested.addMovie(request);

        //then
        assertThat(actual.getTitle()).isEqualTo("Valid Movie");
        assertThat(actual.getReleaseYear()).isEqualTo(2023);
        assertThat(actual.getDurationMinutes()).isEqualTo(120);
        assertThat(actual.getDescription()).isEqualTo("A valid movie description");
        assertThat(actual.getTrailerUrl()).isEqualTo("https://youtube.com/watch?v=test");
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenRequestIsNull() {
        //given
        AddMovieRequest request = null;

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid request");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                null,
                2023,
                120,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid title");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "   ",
                2023,
                120,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid title");
    }

    @Test
    void shouldThrowExceptionWhenReleaseYearIsNull() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                null,
                120,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid year");
    }

    @Test
    void shouldThrowExceptionWhenReleaseYearIsInFuture() {
        //given
        int futureYear = 2030;
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                futureYear,
                120,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid year");
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNull() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                null,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid minutes duration");
    }

    @Test
    void shouldThrowExceptionWhenDurationIsTooShort() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                0,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid minutes duration");
    }

    @Test
    void shouldThrowExceptionWhenDurationIsTooLong() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                501,
                "Description",
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid minutes duration");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        //given
        String longDescription = "a".repeat(2001);
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                120,
                longDescription,
                "https://youtube.com/watch?v=test"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Description too long");
    }

    @Test
    void shouldThrowExceptionWhenTrailerUrlIsInvalid() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                120,
                "Description",
                "invalid-url"
        );

        //when
        Throwable caught = catchThrowable(() -> tested.addMovie(request));

        //then
        assertThat(caught).isInstanceOf(InvalidMovieAddRequestException.class)
                .hasMessage("Invalid trailer");
    }

    @Test
    void shouldAcceptValidTrailerUrl() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                120,
                "Description",
                "http://youtube.com/watch?v=test"
        );

        //when
        Movie actual = tested.addMovie(request);

        //then
        assertThat(actual.getTrailerUrl()).isEqualTo("http://youtube.com/watch?v=test");
    }

    @Test
    void shouldAcceptNullTrailerUrl() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                120,
                "Description",
                null
        );

        //when
        Movie actual = tested.addMovie(request);

        //then
        assertThat(actual.getTrailerUrl()).isNull();
    }

    @Test
    void shouldAcceptBlankTrailerUrl() {
        //given
        AddMovieRequest request = new AddMovieRequest(
                "Valid Title",
                2023,
                120,
                "Description",
                "   "
        );

        //when
        Movie actual = tested.addMovie(request);

        //then
        assertThat(actual.getTrailerUrl()).isEqualTo("   ");
    }


    @Test
    void shouldFindMovieById() {
        //given
        UUID movieUuid = UUID.randomUUID();
        MovieId movieId = new MovieId(movieUuid);
        Movie movie = Movie.builder()
                .withId(movieId)
                .withTitle("Test Movie")
                .withReleaseYear(2023)
                .withDurationMinutes(120)
                .withCreatedAt(Instant.now())
                .build();
        when(repository.findById(movieUuid)).thenReturn(Optional.of(movie));

        //when
        Optional<Movie> actual = tested.findById(movieUuid);

        //then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId().value()).isEqualTo(movieUuid);
        assertThat(actual.get().getTitle()).isEqualTo("Test Movie");
    }

    @Test
    void shouldReturnEmptyWhenMovieNotFound() {
        //given
        UUID movieId = UUID.randomUUID();
        when(repository.findById(movieId)).thenReturn(Optional.empty());

        //when
        Optional<Movie> actual = tested.findById(movieId);

        //then
        assertThat(actual).isEmpty();
    }
}