package com.example.movie.user.persistence;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.domain.model.UserId;
import com.example.movie.user.persistence.model.UserEntity;

class UserEntityMapper {
    static User toDomain(UserEntity entity) {
        UserId id = new UserId(entity.getId());
        return User.builder()
                .withId(id)
                .withUsername(entity.getUsername())
                .withEmail(entity.getEmail())
                .withPasswordHash(entity.getPasswordHash())
                .withCreatedAt(entity.getCreatedAt())
                .build();
    }

    static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId() != null ? user.getId().value() : null);
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setCreatedAt(user.getCreatedAt());
        return entity;
    }

}
