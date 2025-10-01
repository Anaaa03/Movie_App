package com.example.movie;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.superreview.api.model.AddSuperReviewRequest;
import com.example.movie.superreview.api.model.SuperReviewResponse;
import com.example.movie.user.api.model.*;
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
class SuperReviewIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp_SuperReview")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String adminSessionId;
    private String superReviewerSessionId;
    private String regularUserSessionId;
    private UUID movieId;
    private UUID superReviewerId;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Test Super Review Creation by Super Reviewer")
    void testSuperReviewCreationBySuperReviewer() {
        adminSessionId = loginAsAdmin().getSessionId();

        UserResponse user = registerUser("superreviewer", "superreviewer@test.com", "password123");
        superReviewerId = user.getId();
        promoteUserToSuperReviewer(superReviewerId, adminSessionId);
        superReviewerSessionId = loginUser("superreviewer@test.com", "password123").getSessionId();

        MovieResponse movie = createMovie("The Matrix", 1999, 136,
                "A computer hacker learns from mysterious rebels about the true nature of his reality.",
                "https://www.youtube.com/watch?v=vKQi3bBA1y8", adminSessionId);
        movieId = movie.getId();

        SuperReviewResponse superReview = createSuperReview(movieId, superReviewerSessionId);

        assertThat(superReview).isNotNull();
        assertThat(superReview.getMovieId()).isEqualTo(movieId);
        assertThat(superReview.getOverallRating()).isEqualTo(5);
        assertThat(superReview.getScriptRating()).isEqualTo(5);
        assertThat(superReview.getActingRating()).isEqualTo(4);
        assertThat(superReview.getEffectsRating()).isEqualTo(5);
        assertThat(superReview.getMusicRating()).isEqualTo(4);
        assertThat(superReview.getTitle()).isEqualTo("Groundbreaking Sci-Fi Masterpiece");
        assertThat(superReview.getDetailedComment()).contains("revolutionary");
        assertThat(superReview.getPros()).containsIgnoringCase("innovative");
        assertThat(superReview.getCons()).contains("pacing");
        assertThat(superReview.getRecommendation()).isTrue();
        assertThat(superReview.getCreatedAt()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Test Super Review Creation by Admin")
    void testSuperReviewCreationByAdmin() {

        if (adminSessionId == null) {
            adminSessionId = loginAsAdmin().getSessionId();
        }
        if (movieId == null) {
            MovieResponse movie = createMovie("Inception", 2010, 148, "Dream within a dream", "http://test.com", adminSessionId);
            movieId = movie.getId();
        }

        SuperReviewResponse superReview = createSuperReviewByAdmin(movieId, adminSessionId);

        assertThat(superReview).isNotNull();
        assertThat(superReview.getMovieId()).isEqualTo(movieId);
        assertThat(superReview.getOverallRating()).isEqualTo(4);
        assertThat(superReview.getTitle()).isEqualTo("Admin's Professional Review");
    }

    @Test
    @Order(3)
    @DisplayName("Test Super Review Permission Restrictions")
    void testSuperReviewPermissionRestrictions() {

        UserResponse regularUser = registerUser("regularuser", "regular@test.com", "password123");
        regularUserId = regularUser.getId();
        regularUserSessionId = loginUser("regular@test.com", "password123").getSessionId();

        if (movieId == null) {
            if (adminSessionId == null) {
                adminSessionId = loginAsAdmin().getSessionId();
            }
            MovieResponse movie = createMovie("Test Movie", 2023, 120, "Test description", "http://test.com", adminSessionId);
            movieId = movie.getId();
        }

        AddSuperReviewRequest superReviewRequest = new AddSuperReviewRequest(
                movieId, 4, 3, 4, 5, 3,
                "Regular User Attempt", "This should fail", "Good effects", "Bad story", true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", regularUserSessionId);
        HttpEntity<AddSuperReviewRequest> entity = new HttpEntity<>(superReviewRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/super-reviews",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        HttpHeaders noSessionHeaders = new HttpHeaders();
        noSessionHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddSuperReviewRequest> noSessionEntity = new HttpEntity<>(superReviewRequest, noSessionHeaders);

        ResponseEntity<String> noSessionResponse = restTemplate.exchange(
                baseUrl + "/api/super-reviews",
                HttpMethod.POST,
                noSessionEntity,
                String.class
        );

        assertThat(noSessionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        HttpHeaders invalidSessionHeaders = new HttpHeaders();
        invalidSessionHeaders.setContentType(MediaType.APPLICATION_JSON);
        invalidSessionHeaders.set("Session-Id", "invalid-session");
        HttpEntity<AddSuperReviewRequest> invalidSessionEntity = new HttpEntity<>(superReviewRequest, invalidSessionHeaders);

        ResponseEntity<String> invalidSessionResponse = restTemplate.exchange(
                baseUrl + "/api/super-reviews",
                HttpMethod.POST,
                invalidSessionEntity,
                String.class
        );

        assertThat(invalidSessionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(4)
    @DisplayName("Test Super Review Validation Rules")
    void testSuperReviewValidationRules() {

        if (superReviewerSessionId == null) {
            if (adminSessionId == null) {
                adminSessionId = loginAsAdmin().getSessionId();
            }
            UserResponse user = registerUser("validator", "validator@test.com", "password123");
            promoteUserToSuperReviewer(user.getId(), adminSessionId);
            superReviewerSessionId = loginUser("validator@test.com", "password123").getSessionId();
        }
        if (movieId == null) {
            MovieResponse movie = createMovie("Validation Movie", 2023, 120, "Test", "http://test.com", adminSessionId);
            movieId = movie.getId();
        }

        AddSuperReviewRequest invalidOverallRating = new AddSuperReviewRequest(
                movieId, 11, 3, 4, 5, 3, // overallRating = 11 (invalid)
                "Invalid Overall Rating", "Test", "Pros", "Cons", true
        );

        ResponseEntity<String> response1 = postSuperReview(invalidOverallRating, superReviewerSessionId);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AddSuperReviewRequest invalidScriptRating = new AddSuperReviewRequest(
                movieId, 4, 0, 4, 5, 3, // scriptRating = 0 (invalid)
                "Invalid Script Rating", "Test", "Pros", "Cons", true
        );

        ResponseEntity<String> response2 = postSuperReview(invalidScriptRating, superReviewerSessionId);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AddSuperReviewRequest invalidActingRating = new AddSuperReviewRequest(
                movieId, 4, 3, -1, 5, 3, // actingRating = -1 (invalid)
                "Invalid Acting Rating", "Test", "Pros", "Cons", true
        );

        ResponseEntity<String> response3 = postSuperReview(invalidActingRating, superReviewerSessionId);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AddSuperReviewRequest nonExistentMovie = new AddSuperReviewRequest(
                UUID.randomUUID(), 4, 3, 4, 5, 3,
                "Non-existent Movie", "Test", "Pros", "Cons", true
        );

        ResponseEntity<String> response4 = postSuperReview(nonExistentMovie, superReviewerSessionId);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(6)
    @DisplayName("Test Super Review Unique Constraint")
    void testSuperReviewUniqueConstraint() {
        // Setup
        if (adminSessionId == null) {
            adminSessionId = loginAsAdmin().getSessionId();
        }

        UserResponse user = registerUser("uniqueuser", "unique@test.com", "password123");
        promoteUserToSuperReviewer(user.getId(), adminSessionId);
        String userSession = loginUser("unique@test.com", "password123").getSessionId();

        MovieResponse movie = createMovie("Unique Movie", 2023, 120, "Test", "http://test.com", adminSessionId);
        UUID testMovieId = movie.getId();

        SuperReviewResponse firstReview = createSuperReview(testMovieId, userSession);
        assertThat(firstReview).isNotNull();

        AddSuperReviewRequest duplicateRequest = new AddSuperReviewRequest(
                testMovieId, 3, 2, 3, 4, 2,
                "Duplicate Review", "This should fail", "Some pros", "Some cons", false
        );

        ResponseEntity<String> duplicateResponse = postSuperReview(duplicateRequest, userSession);
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(7)
    @DisplayName("Test Role Transition Impact on Super Reviews")
    void testRoleTransitionImpactOnSuperReviews() {
        if (adminSessionId == null) {
            adminSessionId = loginAsAdmin().getSessionId();
        }

        UserResponse user = registerUser("transitionuser", "transition@test.com", "password123");
        UUID userId = user.getId();

        String userSession = loginUser("transition@test.com", "password123").getSessionId();

        MovieResponse movie = createMovie("Transition Movie", 2023, 120, "Test", "http://test.com", adminSessionId);
        UUID testMovieId = movie.getId();

        AddSuperReviewRequest request = new AddSuperReviewRequest(
                testMovieId, 4, 3, 4, 5, 3,
                "Before Promotion", "Should fail", "Pros", "Cons", true
        );

        ResponseEntity<String> beforePromotion = postSuperReview(request, userSession);
        assertThat(beforePromotion.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        promoteUserToSuperReviewer(userId, adminSessionId);

        SuperReviewResponse afterPromotion = createSuperReview(testMovieId, userSession);
        assertThat(afterPromotion).isNotNull();

        demoteUserToRegular(userId, adminSessionId);

        MovieResponse anotherMovie = createMovie("Another Movie", 2023, 120, "Test", "http://test.com", adminSessionId);

        AddSuperReviewRequest afterDemotion = new AddSuperReviewRequest(
                anotherMovie.getId(), 4, 3, 4, 5, 3,
                "After Demotion", "Should fail again", "Pros", "Cons", true
        );

        ResponseEntity<String> afterDemotionResponse = postSuperReview(afterDemotion, userSession);
        assertThat(afterDemotionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private LoginResponse loginAsAdmin() {
        LoginRequest adminLoginRequest = new LoginRequest("admin@movieapp.com", "admin123");
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", adminLoginRequest, LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private UserResponse registerUser(String username, String email, String password) {
        RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .withUsername(username)
                .withEmail(email)
                .withPassword(password)
                .build();

        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/users", registerRequest, UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private LoginResponse loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login", loginRequest, LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private void promoteUserToSuperReviewer(UUID userId, String adminSessionId) {
        changeUserRole(userId, "SUPER_REVIEWER", adminSessionId);
    }

    private void demoteUserToRegular(UUID userId, String adminSessionId) {
        changeUserRole(userId, "USER", adminSessionId);
    }

    private void changeUserRole(UUID userId, String newRole, String adminSessionId) {
        ChangeUserRoleRequest changeRequest = new ChangeUserRoleRequest(userId, newRole);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", adminSessionId);
        HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(changeRequest, headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role", HttpMethod.POST, entity, UserResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private MovieResponse createMovie(String title, Integer year, Integer duration, String description, String trailerUrl, String sessionId) {
        AddMovieRequest movieRequest = new AddMovieRequest(title, year, duration, description, trailerUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddMovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<MovieResponse> response = restTemplate.exchange(
                baseUrl + "/api/movies", HttpMethod.POST, entity, MovieResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private SuperReviewResponse createSuperReview(UUID movieId, String sessionId) {
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 5, 5, 4, 5, 4,
                "Groundbreaking Sci-Fi Masterpiece",
                "This movie is revolutionary in its approach to storytelling and visual effects.",
                "Innovative concepts, excellent special effects, thought-provoking themes",
                "Some pacing issues in the middle act",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddSuperReviewRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<SuperReviewResponse> response = restTemplate.exchange(
                baseUrl + "/api/super-reviews", HttpMethod.POST, entity, SuperReviewResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private SuperReviewResponse createSuperReviewByAdmin(UUID movieId, String adminSessionId) {
        AddSuperReviewRequest request = new AddSuperReviewRequest(
                movieId, 4, 4, 3, 5, 4,
                "Admin's Professional Review",
                "Professional analysis from administrative perspective.",
                "Well-crafted narrative, strong technical execution",
                "Could benefit from tighter editing",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", adminSessionId);
        HttpEntity<AddSuperReviewRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<SuperReviewResponse> response = restTemplate.exchange(
                baseUrl + "/api/super-reviews", HttpMethod.POST, entity, SuperReviewResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private ResponseEntity<String> postSuperReview(AddSuperReviewRequest request, String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", sessionId);
        HttpEntity<AddSuperReviewRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(
                baseUrl + "/api/super-reviews", HttpMethod.POST, entity, String.class);
    }
}
