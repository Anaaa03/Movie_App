package com.example.movie.movie.domain.model;

import com.example.movie.user.domain.model.UserId;
import lombok.NonNull;

import java.util.UUID;

public record MovieId(@NonNull UUID value) {
    public static MovieId create() {
        return new MovieId(UUID.randomUUID());
    }
}
