package com.example.movie;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.review.api.model.AddReviewRequest;
import com.example.movie.review.api.model.ReviewResponse;
import com.example.movie.user.api.model.LoginRequest;
import com.example.movie.user.api.model.LoginResponse;
import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.api.model.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp_Review")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;
    private String adminSessionId;
    private String user1SessionId;
    private UUID user1Id;
    private UUID movieId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Test Basic Review Creation and Retrieval")
    void testBasicReviewCreationAndRetrieval() {
        setupUsersAndMovie();

        ReviewResponse review = createReview(movieId, user1SessionId, 8, "Excellent movie with great storytelling!");

        assertThat(review).isNotNull();
        assertThat(review.getMovieId()).isEqualTo(movieId);
        assertThat(review.getRating()).isEqualTo(8);
        assertThat(review.getComment()).isEqualTo("Excellent movie with great storytelling!");
        assertThat(review.getCreatedAt()).isNotNull();

        verifyReviewInDatabase(review.getId(), movieId, user1Id, 8);
    }

    private void setupUsersAndMovie() {
        adminSessionId = loginAsAdmin().getSessionId();

        UserResponse user1 = registerUser("reviewuser1", "review1@test.com", "password123");
        user1Id = user1.getId();
        user1SessionId = loginUser("review1@test.com", "password123").getSessionId();

        MovieResponse movie = createMovie("Review Test Movie", 2023, 120,
                "A movie created specifically for testing review functionality",
                "http://test-trailer.com", adminSessionId);
        movieId = movie.getId();
    }

    private LoginResponse loginAsAdmin() {
        LoginRequest request = new LoginRequest("admin@movieapp.com", "admin123");
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", request, LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private UserResponse registerUser(String username, String email, String password) {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .withUsername(username)
                .withEmail(email)
                .withPassword(password)
                .build();
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/users", request, UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private LoginResponse loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", request, LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private MovieResponse createMovie(String title, Integer year, Integer duration, String description, String trailerUrl, String sessionId) {
        AddMovieRequest request = new AddMovieRequest(title, year, duration, description, trailerUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddMovieRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies", HttpMethod.POST, entity, MovieResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private ReviewResponse createReview(UUID movieId, String sessionId, Integer rating, String comment) {
        AddReviewRequest request = new AddReviewRequest(movieId, rating, comment);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddReviewRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ReviewResponse> response = restTemplate.exchange(
                baseUrl + "/api/reviews", HttpMethod.POST, entity, ReviewResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private void verifyReviewInDatabase(UUID reviewId, UUID movieId, UUID userId, Integer rating) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE id = ? AND movie_id = ? AND user_id = ? AND rating = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, movieId, userId, rating);
        assertThat(count).isEqualTo(1);
    }
}
