package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "Users", description = "Endpoints para gestão de usuários")
public class UserController {

    private final UserService userService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public UserController(
            UserService userService,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.userService = userService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUserById(
            @PathVariable @NotNull Long id) {
        validateUserAccess(id);

        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UserRequest userRequest) {
        validateUserAccess(id);

        return ResponseEntity.ok(
                userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull Long id) {
        validateUserAccess(id);
        userService.delete(id);
    }

    private void validateUserAccess(Long userId) {
        Long currentUserId = authenticatedUserProvider.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            throw new AccessDeniedException("Access denied: user does not match authenticated user");
        }
    }
}
