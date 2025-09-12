package com.example.movie.user.domain;

import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.domain.model.IllegalRegisterUserRequestException;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final Clock clock;
//    private final UserIdGenerator generator;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterUserRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String passwordHash = encodePassword(request.getPassword());

        User user = User.createNewUser(username, email, passwordHash);

        // clock.instant()
        // generator.generate()
        return userRepository.save(user);
    }

    private String encodePassword(String password) {
        if (password.length() <= 8) {
            throw IllegalRegisterUserRequestException.wrongPassword();
        }
        return passwordEncoder.encode(password);
    }
}
