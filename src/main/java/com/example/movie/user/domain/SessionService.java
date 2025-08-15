package com.example.movie.user.domain;

import com.example.movie.user.domain.model.SessionData;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, SessionData> sessionStore = new ConcurrentHashMap<>();
    private static final int SESSION_HOURS_VALID = 24;
    
    public String createSession(UUID userId) {
        removeUserSessions(userId);
        
        String sessionId = UUID.randomUUID().toString();
        SessionData sessionData = SessionData.create(userId, SESSION_HOURS_VALID);
        sessionStore.put(sessionId, sessionData);
        
        return sessionId;
    }
    
    public UUID getUserId(String sessionId) {
        SessionData sessionData = sessionStore.get(sessionId);
        
        if (sessionData == null) {
            return null;
        }
        
        if (sessionData.isExpired()) {
            sessionStore.remove(sessionId);
            return null;
        }
        
        return sessionData.getUserId();
    }
    
    public void removeSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
    
    public void removeUserSessions(UUID userId) {
        sessionStore.entrySet().removeIf(entry -> 
            entry.getValue().getUserId().equals(userId));
    }
}