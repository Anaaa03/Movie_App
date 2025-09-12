package com.example.movie.user.api;

import com.example.movie.user.api.model.LoginRequest;
import com.example.movie.user.api.model.LoginResponse;
import com.example.movie.user.domain.SessionService;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(SessionService sessionService, 
                         UserRepository userRepository, 
                         PasswordEncoder passwordEncoder) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(401).build();
            }


            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

            if (!passwordMatches) {
                return ResponseEntity.status(401).build();
            }

            String sessionId = sessionService.createSession(user.getId().value());

            Instant expiresAt = Instant.now().plusSeconds(24 * 3600L);

            LoginResponse response = LoginResponse.builder()
                    .sessionId(sessionId)
                    .expiresAt(expiresAt)
                    .message("Login successful")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Session-Id") String sessionId) {
        sessionService.removeSession(sessionId);
        return ResponseEntity.ok().build();
    }
} 