package com.example.movie.user.domain;

import com.example.movie.user.domain.model.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
    }

    @Test
    void shouldCreateSessionForUser() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        String sessionId = sessionService.createSession(userId);

        // then
        assertThat(sessionId).isNotNull();
        assertThat(sessionId).isNotEmpty();

        UUID retrievedUserId = sessionService.getUserId(sessionId);
        assertThat(retrievedUserId).isEqualTo(userId);
    }

    @Test
    void shouldCreateUniqueSessionIds() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // when
        String sessionId1 = sessionService.createSession(userId1);
        String sessionId2 = sessionService.createSession(userId2);

        // then
        assertThat(sessionId1).isNotEqualTo(sessionId2);
    }

    @Test
    void shouldRemovePreviousSessionsWhenCreatingNewSession() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        String firstSessionId = sessionService.createSession(userId);
        String secondSessionId = sessionService.createSession(userId);

        // then
        assertThat(sessionService.getUserId(firstSessionId)).isNull();
        assertThat(sessionService.getUserId(secondSessionId)).isEqualTo(userId);
    }

    @Test
    void shouldReturnUserIdForValidSession() {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = sessionService.createSession(userId);

        // when
        UUID retrievedUserId = sessionService.getUserId(sessionId);

        // then
        assertThat(retrievedUserId).isEqualTo(userId);
    }

    @Test
    void shouldReturnNullForNonExistentSession() {
        // given
        String nonExistentSessionId = UUID.randomUUID().toString();

        // when
        UUID userId = sessionService.getUserId(nonExistentSessionId);

        // then
        assertThat(userId).isNull();
    }

    @Test
    void shouldReturnNullForEmptySessionId() {
        // when
        UUID userId = sessionService.getUserId("");

        // then
        assertThat(userId).isNull();
    }

    @Test
    void shouldRemoveExpiredSessionAndReturnNull() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = sessionService.createSession(userId);

        injectExpiredSession(sessionId, userId);

        // when
        UUID retrievedUserId = sessionService.getUserId(sessionId);

        // then
        assertThat(retrievedUserId).isNull();

        assertThat(sessionService.getUserId(sessionId)).isNull();
    }

    @Test
    void shouldRemoveSessionById() {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = sessionService.createSession(userId);

        // when
        sessionService.removeSession(sessionId);

        // then
        assertThat(sessionService.getUserId(sessionId)).isNull();
    }

    @Test
    void shouldHandleRemovalOfNonExistentSession() {
        // given
        String nonExistentSessionId = UUID.randomUUID().toString();

        // when & then
        sessionService.removeSession(nonExistentSessionId);
    }

    @Test
    void shouldHandleRemovalOfNonExistentUserSessions() {
        // given
        UUID nonExistentUserId = UUID.randomUUID();

        // when & then
        sessionService.removeUserSessions(nonExistentUserId);
    }

    @Test
    void shouldCreateSessionWith24HoursValidity() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when
        String sessionId = sessionService.createSession(userId);

        // then
        SessionData sessionData = getSessionDataFromStore(sessionId);
        assertThat(sessionData).isNotNull();
        assertThat(sessionData.getUserId()).isEqualTo(userId);

        Instant expectedExpiration = Instant.now().plusSeconds(24 * 3600);
        long timeDifference = Math.abs(sessionData.getExpiresAt().getEpochSecond() - expectedExpiration.getEpochSecond());
        assertThat(timeDifference).isLessThan(10);

    }

    @Test
    void shouldNotExpireValidSession() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = sessionService.createSession(userId);

        // when
        UUID retrievedUserId = sessionService.getUserId(sessionId);

        // then
        assertThat(retrievedUserId).isEqualTo(userId);

        assertThat(getSessionDataFromStore(sessionId)).isNotNull();
    }

    @Test
    void shouldHandleMultipleUsersWithSessions() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();

        // when
        String sessionId1 = sessionService.createSession(userId1);
        String sessionId2 = sessionService.createSession(userId2);
        String sessionId3 = sessionService.createSession(userId3);

        // then
        assertThat(sessionService.getUserId(sessionId1)).isEqualTo(userId1);
        assertThat(sessionService.getUserId(sessionId2)).isEqualTo(userId2);
        assertThat(sessionService.getUserId(sessionId3)).isEqualTo(userId3);
    }

    @Test
    void shouldRemoveOnlyTargetUserSessionsWhenMultipleUsersExist() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        String session1 = sessionService.createSession(userId1);
        String session2 = sessionService.createSession(userId2);

        // when
        sessionService.removeUserSessions(userId1);

        // then
        assertThat(sessionService.getUserId(session1)).isNull();
        assertThat(sessionService.getUserId(session2)).isEqualTo(userId2);
    }


    @SuppressWarnings("unchecked")
    private Map<String, SessionData> getSessionStore() throws Exception {
        Field sessionStoreField = SessionService.class.getDeclaredField("sessionStore");
        sessionStoreField.setAccessible(true);
        return (Map<String, SessionData>) sessionStoreField.get(sessionService);
    }

    private SessionData getSessionDataFromStore(String sessionId) throws Exception {
        return getSessionStore().get(sessionId);
    }

    private void injectExpiredSession(String sessionId, UUID userId) {
        try {
            SessionData expiredSession = new SessionData(userId, Instant.now().minusSeconds(3600));
            getSessionStore().put(sessionId, expiredSession);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
