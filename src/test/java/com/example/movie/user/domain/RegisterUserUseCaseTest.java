package com.example.movie.user.domain;


import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.domain.model.IllegalRegisterUserRequestException;
import com.example.movie.user.domain.model.User;
import com.example.movie.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final RegisterUserUseCase tested = new RegisterUserUseCase(repository, passwordEncoder);

    @BeforeEach
    void setUp() {
        when(repository.save(any(User.class))).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRegisterUser() {
        // given
        String password = "password1";
        String hash = "hash";
        when(passwordEncoder.encode(password)).thenReturn(hash);

        RegisterUserRequest request = RegisterUserRequest.builder()
                .withUsername(USERNAME)
                .withPassword(password)
                .withEmail(EMAIL)
                .build();

        // when
        User actual = tested.register(request);

        // then
        User expected = User.builder()
                .withUsername(USERNAME)
                .withPasswordHash(hash)
                .withEmail(EMAIL)
                .withRole("USER")
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
    }


    @Test
    void shouldThrowWrongPasswordException_WhenPasswordIsTooShort() {
        // given
        String password = "pass";
        RegisterUserRequest request = RegisterUserRequest.builder()
                .withUsername(USERNAME)
                .withPassword(password)
                .withEmail(EMAIL)
                .build();

        // when
        Throwable caught = catchThrowable(() -> tested.register(request));

        // then
        assertThat(caught).isInstanceOf(IllegalRegisterUserRequestException.class)
                .hasMessage("Wrong password");
    }

    @Test
    void shouldThrowWrongEmailException_WhenEmailIsInvalid() {
        // given
        String invalidEmail = "invalidEmail";
        RegisterUserRequest request = RegisterUserRequest.builder()
                .withUsername(USERNAME)
                .withPassword("validPassword123")
                .withEmail(invalidEmail)
                .build();

        // when
        Throwable caught = catchThrowable(() -> tested.register(request));

        // then
        assertThat(caught).isInstanceOf(IllegalRegisterUserRequestException.class)
                .hasMessage("Wrong email");
    }

}