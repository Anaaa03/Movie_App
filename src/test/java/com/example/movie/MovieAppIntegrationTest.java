package com.example.movie;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.review.api.model.AddReviewRequest;
import com.example.movie.review.api.model.ReviewResponse;
import com.example.movie.user.api.model.LoginRequest;
import com.example.movie.user.api.model.LoginResponse;
import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.api.model.UserResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieAppIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private String baseUrl;
    private String sessionId;
    private UUID userId;
    private UUID movieId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("End-to-End Test: Complete Movie App Workflow")
    void testCompleteMovieAppWorkflow() {
        UserResponse userResponse = registerUser();
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getUsername()).isEqualTo("testuser");
        assertThat(userResponse.getEmail()).isEqualTo("test@example.com");
        userId = userResponse.getId();

        LoginResponse loginResponse = loginUser();
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getSessionId()).isNotNull();
        assertThat(loginResponse.getMessage()).isEqualTo("Login successful");
        sessionId = loginResponse.getSessionId();

        MovieResponse movieResponse = addMovie();
        assertThat(movieResponse).isNotNull();
        assertThat(movieResponse.getTitle()).isEqualTo("The Matrix");
        assertThat(movieResponse.getReleaseYear()).isEqualTo(1999);
        assertThat(movieResponse.getDurationMinutes()).isEqualTo(136);
        movieId = movieResponse.getId();

        MovieResponse retrievedMovie = getMovieById(movieId);
        assertThat(retrievedMovie).isNotNull();
        assertThat(retrievedMovie.getTitle()).isEqualTo("The Matrix");
        assertThat(retrievedMovie.getDescription()).isEqualTo("A computer hacker learns from mysterious rebels about the true nature of his reality.");

        ReviewResponse reviewResponse = addReview();
        assertThat(reviewResponse).isNotNull();
        assertThat(reviewResponse.getRating()).isEqualTo(5);
        assertThat(reviewResponse.getComment()).isEqualTo("Excellent movie! Mind-bending plot and great special effects.");
        assertThat(reviewResponse.getMovieId()).isEqualTo(movieId);

        UserResponse retrievedUser = getUserById(userId);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("testuser");
        assertThat(retrievedUser.getEmail()).isEqualTo("test@example.com");

        logoutUser();
    }


    @Test
    @Order(2)
    @DisplayName("Test Invalid Data Handling")
    void testInvalidDataHandling() {
        registerUser("testuser2", "test2@example.com", "password123");
        LoginResponse loginResponse = loginUser("test2@example.com", "password123");
        String testSessionId = loginResponse.getSessionId();

        AddMovieRequest invalidMovieRequest = new AddMovieRequest(
                null,
                2023,
                120,
                "Test description",
                "http://example.com/trailer"
        );

        HttpHeaders movieHeaders = new HttpHeaders();
        movieHeaders.setContentType(MediaType.APPLICATION_JSON);
        movieHeaders.set("Session-Id", testSessionId);
        HttpEntity<AddMovieRequest> movieEntity = new HttpEntity<>(invalidMovieRequest, movieHeaders);

        ResponseEntity<MovieResponse> movieResponse = restTemplate.exchange(
                baseUrl + "/api/movies",
                HttpMethod.POST,
                movieEntity,
                MovieResponse.class
        );

        assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        MovieResponse validMovie = addMovieWithSession("Test Movie", 2023, 120, "Test description", "http://example.com", testSessionId);

        AddReviewRequest invalidReviewRequest = new AddReviewRequest(
                validMovie.getId(),
                11,
                "Test comment"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", testSessionId);
        HttpEntity<AddReviewRequest> reviewEntity = new HttpEntity<>(invalidReviewRequest, headers);

        ResponseEntity<ReviewResponse> reviewResponse = restTemplate.exchange(
                baseUrl + "/api/reviews",
                HttpMethod.POST,
                reviewEntity,
                ReviewResponse.class
        );

        assertThat(reviewResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AddReviewRequest zeroRatingRequest = new AddReviewRequest(
                validMovie.getId(),
                0,
                "Test comment"
        );

        HttpEntity<AddReviewRequest> zeroRatingEntity = new HttpEntity<>(zeroRatingRequest, headers);

        ResponseEntity<ReviewResponse> zeroRatingResponse = restTemplate.exchange(
                baseUrl + "/api/reviews",
                HttpMethod.POST,
                zeroRatingEntity,
                ReviewResponse.class
        );

        assertThat(zeroRatingResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    private UserResponse registerUser() {
        return registerUser("testuser", "test@example.com", "password123");
    }

    private UserResponse registerUser(String username, String email, String password) {
        RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .withUsername(username)
                .withEmail(email)
                .withPassword(password)
                .build();

        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/users",
                registerRequest,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private LoginResponse loginUser() {
        return loginUser("test@example.com", "password123");
    }

    private LoginResponse loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private MovieResponse addMovie() {
        return addMovie("The Matrix", 1999, 136,
                "A computer hacker learns from mysterious rebels about the true nature of his reality.",
                "https://www.youtube.com/watch?v=vKQi3bBA1y8");
    }

    private MovieResponse addMovie(String title, Integer releaseYear, Integer durationMinutes,
                                   String description, String trailerUrl) {
        return addMovieWithSession(title, releaseYear, durationMinutes, description, trailerUrl, sessionId);
    }

    private MovieResponse addMovieWithSession(String title, Integer releaseYear, Integer durationMinutes,
                                              String description, String trailerUrl, String sessionIdToUse) {
        AddMovieRequest movieRequest = new AddMovieRequest(
                title,
                releaseYear,
                durationMinutes,
                description,
                trailerUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionIdToUse);
        HttpEntity<AddMovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies",
                HttpMethod.POST,
                entity,
                MovieResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private MovieResponse getMovieById(UUID movieId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", sessionId);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies/" + movieId,
                HttpMethod.GET,
                entity,
                MovieResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private ReviewResponse addReview() {
        AddReviewRequest reviewRequest = new AddReviewRequest(
                movieId,
                5,
                "Excellent movie! Mind-bending plot and great special effects."
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddReviewRequest> entity = new HttpEntity<>(reviewRequest, headers);

        ResponseEntity<ReviewResponse> response = restTemplate.exchange(
                baseUrl + "/api/reviews",
                HttpMethod.POST,
                entity,
                ReviewResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private UserResponse getUserById(UUID userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", sessionId);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/api/users/" + userId,
                HttpMethod.GET,
                entity,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private void logoutUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", sessionId);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/api/auth/logout",
                HttpMethod.POST,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
