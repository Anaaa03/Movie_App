package com.example.movie.user.persistence;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.model.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    public User save(User user) {
        UserEntity saved = jpaUserRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(saved);
    }

    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(UserEntityMapper::toDomain);
    }

    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }

    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(UserEntityMapper::toDomain);
    }
}
