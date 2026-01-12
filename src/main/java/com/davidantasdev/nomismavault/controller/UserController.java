package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUserById(
            @PathVariable @NotNull Long id) {

        return ResponseEntity.ok(userService.findById(id));
    }

    // GET /api/users/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findUserByEmail(
            @PathVariable @NotNull @Email String email) {

        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRequest));
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok(
                userService.updateUser(id, userRequest)
        );
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull Long id) {
        userService.delete(id);
    }
}
