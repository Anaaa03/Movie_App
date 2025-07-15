package com.example.movie.user.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(force = true)
public class RegisterUserRequest {
    private final String username;
    private final String email;
    @ToString.Exclude
    private final String password;
}
