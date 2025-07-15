package com.example.movie.user.domain;

import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserQueryUseCase {
    private final UserRepository userRepository;

    public UserQueryUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
