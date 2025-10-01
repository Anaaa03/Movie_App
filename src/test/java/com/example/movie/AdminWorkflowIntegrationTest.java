package com.example.movie;

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
class AdminWorkflowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.0")
            .withDatabaseName("db_MovieApp_Admin")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private String baseUrl;
    private String adminSessionId;
    private String userSessionId;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Test Admin Login and Basic Admin Operations")
    void testAdminLoginAndBasicOperations() {
        LoginResponse adminLogin = loginAsAdmin();
        assertThat(adminLogin).isNotNull();
        assertThat(adminLogin.getSessionId()).isNotNull();
        adminSessionId = adminLogin.getSessionId();

        UserResponse regularUser = registerUser("regularuser", "regular@test.com", "password123");
        assertThat(regularUser).isNotNull();
        regularUserId = regularUser.getId();

        LoginResponse userLogin = loginUser("regular@test.com", "password123");
        assertThat(userLogin).isNotNull();
        userSessionId = userLogin.getSessionId();

        testAdminEndpointAccess();
    }

    @Test
    @Order(2)
    @DisplayName("Test Role Change Functionality")
    void testRoleChangeFunctionality() {
        if (adminSessionId == null) {
            adminSessionId = loginAsAdmin().getSessionId();
        }

        if (regularUserId == null) {
            UserResponse user = registerUser("roleuser", "role@test.com", "password123");
            regularUserId = user.getId();
        }

        ChangeUserRoleRequest changeToSuperReviewer = new ChangeUserRoleRequest(regularUserId, "SUPER_REVIEWER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", adminSessionId);
        HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(changeToSuperReviewer, headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                entity,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse updatedUser = response.getBody();
        assertThat(updatedUser).isNotNull();

        ChangeUserRoleRequest changeToAdmin = new ChangeUserRoleRequest(regularUserId, "ADMIN");

        HttpEntity<ChangeUserRoleRequest> adminEntity = new HttpEntity<>(changeToAdmin, headers);

        ResponseEntity<UserResponse> adminResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                adminEntity,
                UserResponse.class
        );

        assertThat(adminResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ChangeUserRoleRequest changeToUser = new ChangeUserRoleRequest(regularUserId, "USER");

        HttpEntity<ChangeUserRoleRequest> userEntity = new HttpEntity<>(changeToUser, headers);

        ResponseEntity<UserResponse> userResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                userEntity,
                UserResponse.class
        );

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(3)
    @DisplayName("Test Invalid Role Change Scenarios")
    void testInvalidRoleChangeScenarios() {
        if (adminSessionId == null) {
            adminSessionId = loginAsAdmin().getSessionId();
        }
        if (regularUserId == null) {
            UserResponse user = registerUser("invalidroleuser", "invalidrole@test.com", "password123");
            regularUserId = user.getId();
            userSessionId = loginUser("invalidrole@test.com", "password123").getSessionId();
        }

        ChangeUserRoleRequest changeRequest = new ChangeUserRoleRequest(regularUserId, "ADMIN");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.set("Session-Id", userSessionId);
        HttpEntity<ChangeUserRoleRequest> userEntity = new HttpEntity<>(changeRequest, userHeaders);

        ResponseEntity<String> userResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                userEntity,
                String.class
        );

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ChangeUserRoleRequest invalidRoleRequest = new ChangeUserRoleRequest(regularUserId, "INVALID_ROLE");

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Session-Id", adminSessionId);
        HttpEntity<ChangeUserRoleRequest> adminEntity = new HttpEntity<>(invalidRoleRequest, adminHeaders);

        ResponseEntity<String> adminResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                adminEntity,
                String.class
        );

        assertThat(adminResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ChangeUserRoleRequest nonExistentUserRequest = new ChangeUserRoleRequest(UUID.randomUUID(), "USER");

        HttpEntity<ChangeUserRoleRequest> nonExistentEntity = new HttpEntity<>(nonExistentUserRequest, adminHeaders);

        ResponseEntity<String> nonExistentResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                nonExistentEntity,
                String.class
        );

        assertThat(nonExistentResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(4)
    @DisplayName("Test Admin Access Control")
    void testAdminAccessControl() {
        UserResponse regularUser = registerUser("accessuser", "access@test.com", "password123");
        String regularUserSession = loginUser("access@test.com", "password123").getSessionId();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.set("Session-Id", regularUserSession);

        ChangeUserRoleRequest changeRequest = new ChangeUserRoleRequest(regularUser.getId(), "ADMIN");

        HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(changeRequest, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        HttpHeaders noSessionHeaders = new HttpHeaders();
        noSessionHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChangeUserRoleRequest> noSessionEntity = new HttpEntity<>(changeRequest, noSessionHeaders);

        ResponseEntity<String> noSessionResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                noSessionEntity,
                String.class
        );

        assertThat(noSessionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        HttpHeaders invalidSessionHeaders = new HttpHeaders();
        invalidSessionHeaders.setContentType(MediaType.APPLICATION_JSON);
        invalidSessionHeaders.set("Session-Id", "invalid-session-id");
        HttpEntity<ChangeUserRoleRequest> invalidSessionEntity = new HttpEntity<>(changeRequest, invalidSessionHeaders);

        ResponseEntity<String> invalidSessionResponse = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                invalidSessionEntity,
                String.class
        );

        assertThat(invalidSessionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    private LoginResponse loginAsAdmin() {
        LoginRequest adminLoginRequest = new LoginRequest("admin@movieapp.com", "admin123");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                adminLoginRequest,
                LoginResponse.class
        );

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


    private void testAdminEndpointAccess() {
        UserResponse dummyUser = registerUser("dummyuser", "dummy@test.com", "password123");

        ChangeUserRoleRequest changeRequest = new ChangeUserRoleRequest(dummyUser.getId(), "SUPER_REVIEWER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Session-Id", adminSessionId);
        HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(changeRequest, headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/api/users/admin/change-role",
                HttpMethod.POST,
                entity,
                UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
