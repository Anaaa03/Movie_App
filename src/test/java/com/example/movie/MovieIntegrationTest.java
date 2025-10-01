package com.example.movie;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp_Movie")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;
    private String userSessionId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Test Basic Movie Creation and Retrieval")
    void testBasicMovieCreationAndRetrieval() {
        setupUserSession();

        MovieResponse movie = createMovie("The Matrix", 1999, 136,
                "A computer hacker learns from mysterious rebels about the true nature of his reality.",
                "https://www.youtube.com/watch?v=vKQi3bBA1y8");

        assertThat(movie).isNotNull();
        assertThat(movie.getTitle()).isEqualTo("The Matrix");
        assertThat(movie.getReleaseYear()).isEqualTo(1999);
        assertThat(movie.getDurationMinutes()).isEqualTo(136);
        assertThat(movie.getDescription()).isEqualTo("A computer hacker learns from mysterious rebels about the true nature of his reality.");
        assertThat(movie.getTrailerUrl()).isEqualTo("https://www.youtube.com/watch?v=vKQi3bBA1y8");
        assertThat(movie.getId()).isNotNull();

        MovieResponse retrievedMovie = getMovieById(movie.getId());
        assertThat(retrievedMovie).isNotNull();
        assertThat(retrievedMovie.getId()).isEqualTo(movie.getId());
        assertThat(retrievedMovie.getTitle()).isEqualTo("The Matrix");
        assertThat(retrievedMovie.getReleaseYear()).isEqualTo(1999);

        verifyMovieInDatabase(movie.getId(), "The Matrix", 1999, 136);
    }

    private void setupUserSession() {
        UserResponse user = registerUser("movieuser", "movie@test.com", "password123");
        userId = user.getId();
        userSessionId = loginUser("movie@test.com", "password123").getSessionId();
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

    private MovieResponse createMovie(String title, Integer year, Integer duration, String description, String trailerUrl) {
        AddMovieRequest request = new AddMovieRequest(title, year, duration, description, trailerUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", userSessionId);
        HttpEntity<AddMovieRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies", HttpMethod.POST, entity, MovieResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private MovieResponse getMovieById(UUID movieId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", userSessionId);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies/" + movieId, HttpMethod.GET, entity, MovieResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private void verifyMovieInDatabase(UUID movieId, String title, Integer year, Integer duration) {
        String sql = "SELECT COUNT(*) FROM movies WHERE id = ? AND title = ? AND release_year = ? AND duration_minutes = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, movieId, title, year, duration);
        assertThat(count).isEqualTo(1);
    }
}
