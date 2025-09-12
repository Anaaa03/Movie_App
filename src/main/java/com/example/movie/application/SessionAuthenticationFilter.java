package com.example.movie.application;

import com.example.movie.user.domain.SessionService;
import com.example.movie.user.domain.UserQueryUseCase;
import com.example.movie.user.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final UserQueryUseCase userQueryUseCase;

    public SessionAuthenticationFilter(SessionService sessionService, UserQueryUseCase userQueryUseCase) {
        this.sessionService = sessionService;
        this.userQueryUseCase = userQueryUseCase;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String sessionId = request.getHeader("Session-Id");
        
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                UUID userId = sessionService.getUserId(sessionId);
                if (userId != null) {
                    User user = userQueryUseCase.findById(userId).orElse(null);
                    String role = user != null ? user.getRole() : "USER";
                    String springRole = "ROLE_" + role;
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userId.toString(),
                            null,
                                Collections.singletonList(new SimpleGrantedAuthority(springRole))
                        );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.debug("Invalid session: " + sessionId);
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 