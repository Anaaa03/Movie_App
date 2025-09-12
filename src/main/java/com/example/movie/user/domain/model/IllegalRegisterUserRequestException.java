package com.example.movie.user.domain.model;

public class IllegalRegisterUserRequestException extends IllegalArgumentException {
    private IllegalRegisterUserRequestException(String message) {
        super(message);
    }

    public static IllegalRegisterUserRequestException wrongPassword() {
        return new IllegalRegisterUserRequestException("Wrong password");
    }
}
