package com.example.movie.user.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String username;

    private String email;

    private String passwordHash;

    private String role;

    private Instant createdAt;
}