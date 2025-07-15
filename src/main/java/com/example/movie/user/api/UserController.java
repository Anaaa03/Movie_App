package com.example.movie.user.api;

import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.api.model.UserResponse;
import com.example.movie.user.domain.RegisterUserUseCase;
import com.example.movie.user.domain.UserQueryUseCase;
import com.example.movie.user.domain.model.IllegalRegisterUserRequestException;
import com.example.movie.user.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.movie.user.api.UserResponseMapper.mapToUserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final UserQueryUseCase userQueryUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, UserQueryUseCase userQueryUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.userQueryUseCase = userQueryUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        try {
            User registered = registerUserUseCase.register(request);
            UserResponse response = mapToUserResponse(registered);
            return ResponseEntity.ok(response);
        } catch (IllegalRegisterUserRequestException e) {
//            todo how to make response json AND?? text. guess it supposed to be only json not <?>
            System.out.println("u see me??" + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return userQueryUseCase.findById(id)
                .map(UserResponseMapper::mapToUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
