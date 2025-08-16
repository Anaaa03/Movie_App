package com.example.movie.user.api;

import com.example.movie.user.api.model.ChangeUserRoleRequest;
import com.example.movie.user.api.model.RegisterUserRequest;
import com.example.movie.user.api.model.UserResponse;
import com.example.movie.user.domain.AdminUserManagementUseCase;
import com.example.movie.user.domain.RegisterUserUseCase;
import com.example.movie.user.domain.SessionService;
import com.example.movie.user.domain.UserQueryUseCase;
import com.example.movie.user.domain.model.IllegalRegisterUserRequestException;
import com.example.movie.user.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import static com.example.movie.user.api.UserResponseMapper.mapToUserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final AdminUserManagementUseCase adminUserManagementUseCase;
    private final SessionService sessionService;

    public UserController(RegisterUserUseCase registerUserUseCase, 
                         UserQueryUseCase userQueryUseCase,
                         AdminUserManagementUseCase adminUserManagementUseCase,
                         SessionService sessionService) {
        this.registerUserUseCase = registerUserUseCase;
        this.userQueryUseCase = userQueryUseCase;
        this.adminUserManagementUseCase = adminUserManagementUseCase;
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        try {
            User registered = registerUserUseCase.register(request);
            UserResponse response = mapToUserResponse(registered);
            return ResponseEntity.ok(response);
        } catch (IllegalRegisterUserRequestException e) {
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

    @PostMapping("/admin/change-role")
    public ResponseEntity<UserResponse> changeUserRole(@RequestBody ChangeUserRoleRequest request,
                                                      @RequestHeader("Session-Id") String sessionId) {
        try {
            UUID adminUserId = sessionService.getUserId(sessionId);
            if (adminUserId == null) {
                return ResponseEntity.status(401).build();
            }
            
            User updatedUser = adminUserManagementUseCase.changeUserRole(
                adminUserId, 
                request.getUserId(), 
                request.getNewRole()
            );
            
            UserResponse response = mapToUserResponse(updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
