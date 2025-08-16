package com.example.movie.user.api;

import com.example.movie.user.api.model.UserResponse;
import com.example.movie.user.domain.model.User;

class UserResponseMapper {
    static UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId().value(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
