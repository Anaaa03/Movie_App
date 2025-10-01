package com.example.movie;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.review.api.model.AddReviewRequest;
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
class AuthenticationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp_Auth")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String sessionId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Test Complete Authentication Flow")
    void testCompleteAuthenticationFlow() {
        UserResponse userResponse = registerUser("authuser", "auth@test.com", "password123");
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getUsername()).isEqualTo("authuser");
        userId = userResponse.getId();

        LoginResponse loginResponse = loginUser("auth@test.com", "password123");
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getSessionId()).isNotNull();
        assertThat(loginResponse.getMessage()).isEqualTo("Login successful");
        sessionId = loginResponse.getSessionId();

        UserResponse retrievedUser = getUserById(userId, sessionId);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("authuser");

        logoutUser(sessionId);

        ResponseEntity<UserResponse> response = getUserByIdRaw(userId, sessionId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(2)
    @DisplayName("Test Authentication Failures")
    void testAuthenticationFailures() {
        LoginRequest invalidUserRequest = new LoginRequest("nonexistent@test.com", "password123");
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                invalidUserRequest,
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        registerUser("wrongpassuser", "wrongpass@test.com", "correctpassword");
        LoginRequest wrongPasswordRequest = new LoginRequest("wrongpass@test.com", "wrongpassword");
        response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                wrongPasswordRequest,
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        testProtectedEndpointsWithoutSession();
    }

    @Test
    @Order(3)
    @DisplayName("Test Invalid Session Handling")
    void testInvalidSessionHandling() {
        registerUser("sessionuser", "session@test.com", "password123");
        LoginResponse loginResponse = loginUser("session@test.com", "password123");

        ResponseEntity<UserResponse> response = getUserByIdRaw(UUID.randomUUID(), "invalid-session-id");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<UserResponse> response2 = getUserByIdRaw(UUID.randomUUID(), UUID.randomUUID().toString());
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", "");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponse> response3 = restTemplate.exchange(
                baseUrl + "/api/users/" + UUID.randomUUID(),
                HttpMethod.GET,
                entity,
                UserResponse.class
        );
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<UserResponse> response4 = restTemplate.getForEntity(
                baseUrl + "/api/users/" + UUID.randomUUID(),
                UserResponse.class
        );
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(4)
    @DisplayName("Test Session Persistence Across Requests")
    void testSessionPersistenceAcrossRequests() {
        UserResponse userResponse = registerUser("persistuser", "persist@test.com", "password123");
        LoginResponse loginResponse = loginUser("persist@test.com", "password123");
        String sessionId = loginResponse.getSessionId();

        MovieResponse movieResponse = addMovieWithSession("Test Movie", 2023, 120,
                "Test description", "http://test.com", sessionId);
        assertThat(movieResponse).isNotNull();
        UUID movieId = movieResponse.getId();

        AddReviewRequest reviewRequest = new AddReviewRequest(movieId, 8, "Great movie!");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddReviewRequest> entity = new HttpEntity<>(reviewRequest, headers);

        ResponseEntity<String> reviewResponse = restTemplate.exchange(
                baseUrl + "/api/reviews",
                HttpMethod.POST,
                entity,
                String.class
        );
        assertThat(reviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserResponse retrievedUser = getUserById(userResponse.getId(), sessionId);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("persistuser");
    }

    @Test
    @Order(5)
    @DisplayName("Test Multiple User Sessions")
    void testMultipleUserSessions() {
        UserResponse user1 = registerUser("user1", "user1@test.com", "password123");
        UserResponse user2 = registerUser("user2", "user2@test.com", "password123");

        LoginResponse login1 = loginUser("user1@test.com", "password123");
        LoginResponse login2 = loginUser("user2@test.com", "password123");

        String session1 = login1.getSessionId();
        String session2 = login2.getSessionId();

        assertThat(session1).isNotEqualTo(session2);

        UserResponse retrieved1 = getUserById(user1.getId(), session1);
        UserResponse retrieved2 = getUserById(user2.getId(), session2);

        assertThat(retrieved1.getUsername()).isEqualTo("user1");
        assertThat(retrieved2.getUsername()).isEqualTo("user2");

        MovieResponse movie1 = addMovieWithSession("User1 Movie", 2023, 120, "Description", "http://test.com", session1);
        MovieResponse movie2 = addMovieWithSession("User2 Movie", 2023, 130, "Description", "http://test.com", session2);

        assertThat(movie1.getTitle()).isEqualTo("User1 Movie");
        assertThat(movie2.getTitle()).isEqualTo("User2 Movie");
    }

    private void testProtectedEndpointsWithoutSession() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        ResponseEntity<String> movieResponse = restTemplate.exchange(
                baseUrl + "/api/movies",
                HttpMethod.POST,
                entity,
                String.class
        );
        assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> reviewResponse = restTemplate.exchange(
                baseUrl + "/api/reviews",
                HttpMethod.POST,
                entity,
                String.class
        );
        assertThat(reviewResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> userResponse = restTemplate.getForEntity(
                baseUrl + "/api/users/" + UUID.randomUUID(),
                String.class
        );
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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

    private UserResponse getUserById(UUID userId, String sessionId) {
        ResponseEntity<UserResponse> response = getUserByIdRaw(userId, sessionId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private ResponseEntity<UserResponse> getUserByIdRaw(UUID userId, String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Session-Id", sessionId);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                baseUrl + "/api/users/" + userId,
                HttpMethod.GET,
                entity,
                UserResponse.class
        );
    }

    private MovieResponse addMovieWithSession(String title, Integer releaseYear, Integer durationMinutes,
                                              String description, String trailerUrl, String sessionId) {
        AddMovieRequest movieRequest = new AddMovieRequest(title, releaseYear, durationMinutes, description, trailerUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
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

    private void logoutUser(String sessionId) {
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
