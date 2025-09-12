package com.example.movie.user.api.model;

import lombok.*;

@Data
@Builder(setterPrefix = "with")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RegisterUserRequest {
    private final String username;
    private final String email;
    @ToString.Exclude
    private final String password;
}
