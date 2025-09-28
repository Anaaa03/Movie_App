package com.example.movie.user.domain;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.domain.model.UserId;
import com.example.movie.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminUserManagementUseCaseTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AdminUserManagementUseCase tested = new AdminUserManagementUseCase(userRepository);

    private UUID adminUserId;
    private UUID targetUserId;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        adminUserId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        regularUserId = UUID.randomUUID();

        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldChangeUserRoleWhenAdminChangesRegularUserToSuperReviewer() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "SUPER_REVIEWER");

        // then
        assertThat(result.getRole()).isEqualTo("SUPER_REVIEWER");
        assertThat(result.getId()).isEqualTo(targetUser.getId());
        assertThat(result.getUsername()).isEqualTo(targetUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(targetUser.getEmail());
        assertThat(result.getPasswordHash()).isEqualTo(targetUser.getPasswordHash());
        assertThat(result.getCreatedAt()).isEqualTo(targetUser.getCreatedAt());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldChangeUserRoleWhenAdminChangesUserToAdmin() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "ADMIN");

        // then
        assertThat(result.getRole()).isEqualTo("ADMIN");
        assertThat(result.isAdmin()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldChangeUserRoleWhenAdminDemotesAdminToUser() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetAdmin = createUserWithRole("ADMIN", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetAdmin));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "USER");

        // then
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.isAdmin()).isFalse();
        assertThat(result.isSuperReviewer()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldChangeUserRoleWhenAdminDemotesSuperReviewerToUser() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User superReviewer = createUserWithRole("SUPER_REVIEWER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(superReviewer));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "USER");

        // then
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.isSuperReviewer()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegularUserTriesToChangeRole() {
        // given
        User regularUser = createUserWithRole("USER", regularUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(regularUserId)).thenReturn(Optional.of(regularUser));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(regularUserId, targetUserId, "ADMIN"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Only admins can change user roles");
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findById(targetUserId);
    }

    @Test
    void shouldThrowExceptionWhenSuperReviewerTriesToChangeRole() {
        // given
        User superReviewer = createUserWithRole("SUPER_REVIEWER", regularUserId);

        when(userRepository.findById(regularUserId)).thenReturn(Optional.of(superReviewer));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(regularUserId, targetUserId, "ADMIN"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Only admins can change user roles");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenAdminNotFound() {
        // given
        when(userRepository.findById(adminUserId)).thenReturn(Optional.empty());

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, "ADMIN"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Admin not found");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenTargetUserNotFound() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, "ADMIN"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Target user not found");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenInvalidRole() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, "INVALID_ROLE"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid role: INVALID_ROLE");
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findById(targetUserId);
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNull() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, null));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid role: null");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsEmpty() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, ""));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid role: ");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsCaseIncorrect() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));

        // when
        Throwable caught = catchThrowable(() -> tested.changeUserRole(adminUserId, targetUserId, "admin"));

        // then
        assertThat(caught).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid role: admin");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldAcceptAllValidRoles() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when & then
        tested.changeUserRole(adminUserId, targetUserId, "USER");
        tested.changeUserRole(adminUserId, targetUserId, "ADMIN");
        tested.changeUserRole(adminUserId, targetUserId, "SUPER_REVIEWER");

        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void shouldChangeRoleToSameRole() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "USER");

        // then
        assertThat(result.getRole()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldAllowAdminToChangeOwnRole() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));

        // when
        User result = tested.changeUserRole(adminUserId, adminUserId, "USER");

        // then
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.isAdmin()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldPreserveAllUserFieldsExceptRole() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);

        UserId targetId = new UserId(targetUserId);
        String username = "targetuser";
        String email = "target@example.com";
        String passwordHash = "targetHash";
        Instant createdAt = Instant.now().minusSeconds(86400);

        User targetUser = User.builder()
                .withId(targetId)
                .withUsername(username)
                .withEmail(email)
                .withPasswordHash(passwordHash)
                .withRole("USER")
                .withCreatedAt(createdAt)
                .build();

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when
        User result = tested.changeUserRole(adminUserId, targetUserId, "SUPER_REVIEWER");

        // then
        assertThat(result.getId()).isEqualTo(targetId);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getRole()).isEqualTo("SUPER_REVIEWER");
    }

    @Test
    void shouldHandleMultipleRoleChangesInSequence() {
        // given
        User admin = createUserWithRole("ADMIN", adminUserId);
        User targetUser = createUserWithRole("USER", targetUserId);

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        // when
        User result1 = tested.changeUserRole(adminUserId, targetUserId, "SUPER_REVIEWER");

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(result1));
        User result2 = tested.changeUserRole(adminUserId, targetUserId, "ADMIN");

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(result2));
        User result3 = tested.changeUserRole(adminUserId, targetUserId, "USER");

        // then
        assertThat(result1.getRole()).isEqualTo("SUPER_REVIEWER");
        assertThat(result2.getRole()).isEqualTo("ADMIN");
        assertThat(result3.getRole()).isEqualTo("USER");

        verify(userRepository, times(3)).save(any(User.class));
    }

    private User createUserWithRole(String role, UUID userId) {
        return User.builder()
                .withId(new UserId(userId))
                .withUsername("testuser")
                .withEmail("test@example.com")
                .withPasswordHash("hashedPassword")
                .withRole(role)
                .withCreatedAt(Instant.now())
                .build();
    }
}
