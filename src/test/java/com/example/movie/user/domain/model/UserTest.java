package com.example.movie.user.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldCreateNewUserWithDefaultRole() {
        // given
        String username = "testuser";
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";

        // when
        User user = User.createNewUser(username, email, passwordHash);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldCreateUserWithUniqueId() {
        // given
        String username = "testuser";
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";

        // when
        User user1 = User.createNewUser(username, email, passwordHash);
        User user2 = User.createNewUser(username, email, passwordHash);

        // then
        assertThat(user1.getId()).isNotEqualTo(user2.getId());
    }

    @Test
    void shouldHaveRoleReturnTrueForMatchingRole() {
        // given
        User user = createUserWithRole("ADMIN");

        // when & then
        assertThat(user.hasRole("ADMIN")).isTrue();
        assertThat(user.hasRole("USER")).isFalse();
        assertThat(user.hasRole("SUPER_REVIEWER")).isFalse();
    }

    @Test
    void shouldHaveRoleReturnFalseForNonMatchingRole() {
        // given
        User user = createUserWithRole("USER");

        // when & then
        assertThat(user.hasRole("ADMIN")).isFalse();
        assertThat(user.hasRole("SUPER_REVIEWER")).isFalse();
    }

    @Test
    void shouldReturnTrueForIsAdminWhenUserIsAdmin() {
        // given
        User adminUser = createUserWithRole("ADMIN");
        User regularUser = createUserWithRole("USER");
        User superReviewerUser = createUserWithRole("SUPER_REVIEWER");

        // when & then
        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(regularUser.isAdmin()).isFalse();
        assertThat(superReviewerUser.isAdmin()).isFalse();
    }

    @Test
    void shouldReturnTrueForIsSuperReviewerWhenUserIsSuperReviewer() {
        // given
        User adminUser = createUserWithRole("ADMIN");
        User regularUser = createUserWithRole("USER");
        User superReviewerUser = createUserWithRole("SUPER_REVIEWER");

        // when & then
        assertThat(superReviewerUser.isSuperReviewer()).isTrue();
        assertThat(adminUser.isSuperReviewer()).isFalse();
        assertThat(regularUser.isSuperReviewer()).isFalse();
    }

    @Test
    void shouldChangeRoleAndReturnNewUserInstance() {
        // given
        User originalUser = createUserWithRole("USER");
        String newRole = "ADMIN";

        // when
        User updatedUser = originalUser.changeRole(newRole);

        // then
        assertThat(updatedUser.getRole()).isEqualTo(newRole);
        assertThat(updatedUser.getId()).isEqualTo(originalUser.getId());
        assertThat(updatedUser.getUsername()).isEqualTo(originalUser.getUsername());
        assertThat(updatedUser.getEmail()).isEqualTo(originalUser.getEmail());
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalUser.getPasswordHash());
        assertThat(updatedUser.getCreatedAt()).isEqualTo(originalUser.getCreatedAt());
        assertThat(originalUser.getRole()).isEqualTo("USER");
        assertThat(updatedUser).isNotSameAs(originalUser);
    }

    @Test
    void shouldChangeRoleFromUserToAdmin() {
        // given
        User user = createUserWithRole("USER");

        // when
        User adminUser = user.changeRole("ADMIN");

        // then
        assertThat(adminUser.getRole()).isEqualTo("ADMIN");
        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(adminUser.isSuperReviewer()).isFalse();
    }

    @Test
    void shouldChangeRoleFromUserToSuperReviewer() {
        // given
        User user = createUserWithRole("USER");

        // when
        User superReviewerUser = user.changeRole("SUPER_REVIEWER");

        // then
        assertThat(superReviewerUser.getRole()).isEqualTo("SUPER_REVIEWER");
        assertThat(superReviewerUser.isSuperReviewer()).isTrue();
        assertThat(superReviewerUser.isAdmin()).isFalse();
    }

    @Test
    void shouldChangeRoleFromAdminToUser() {
        // given
        User adminUser = createUserWithRole("ADMIN");

        // when
        User regularUser = adminUser.changeRole("USER");

        // then
        assertThat(regularUser.getRole()).isEqualTo("USER");
        assertThat(regularUser.isAdmin()).isFalse();
        assertThat(regularUser.isSuperReviewer()).isFalse();
    }

    @Test
    void shouldPreserveAllFieldsExceptRoleWhenChangingRole() {
        // given
        UserId userId = new UserId(UUID.randomUUID());
        String username = "testuser";
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";
        Instant createdAt = Instant.now().minusSeconds(3600);

        User originalUser = User.builder()
                .withId(userId)
                .withUsername(username)
                .withEmail(email)
                .withPasswordHash(passwordHash)
                .withRole("USER")
                .withCreatedAt(createdAt)
                .build();

        // when
        User updatedUser = originalUser.changeRole("ADMIN");

        // then
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getUsername()).isEqualTo(username);
        assertThat(updatedUser.getEmail()).isEqualTo(email);
        assertThat(updatedUser.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(updatedUser.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedUser.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void shouldHandleNullRoleInHasRole() {
        // given
        User user = createUserWithRole("USER");

        // when & then
        assertThat(user.hasRole(null)).isFalse();
    }

    @Test
    void shouldHandleEmptyRoleInHasRole() {
        // given
        User user = createUserWithRole("USER");

        // when & then
        assertThat(user.hasRole("")).isFalse();
    }

    @Test
    void shouldBeCaseSensitiveForRoles() {
        // given
        User user = createUserWithRole("ADMIN");

        // when & then
        assertThat(user.hasRole("admin")).isFalse();
        assertThat(user.hasRole("Admin")).isFalse();
        assertThat(user.hasRole("ADMIN")).isTrue();
    }

    private User createUserWithRole(String role) {
        UserId userId = new UserId(UUID.randomUUID());
        return User.builder()
                .withId(userId)
                .withUsername("testuser")
                .withEmail("test@example.com")
                .withPasswordHash("hashedPassword123")
                .withRole(role)
                .withCreatedAt(Instant.now())
                .build();
    }
}
