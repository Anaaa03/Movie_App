package com.example.movie.user.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Builder(setterPrefix = "with")
@Data
@RequiredArgsConstructor(access = PRIVATE)
public class User {
    private final UserId id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;


    public static User createNewUser(String username, String email, String passwordHash){
        UserId id = UserId.create();
        Instant createdAt = Instant.now();
        return User.builder()
                .withId(id)
                .withUsername(username)
                .withEmail(email)
                .withPasswordHash(passwordHash)
                .withCreatedAt(createdAt)
                .build();
    }

}
