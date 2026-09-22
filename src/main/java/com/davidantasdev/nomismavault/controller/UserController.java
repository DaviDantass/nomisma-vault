package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestão de usuários")
public class UserController {

  private final UserService userService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findUserById(@PathVariable @NotNull Long id) {

    authenticatedUserProvider.validateOwnership(id);
    return ResponseEntity.ok(userService.findById(id));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponse> findUserByEmail(@PathVariable @NotNull @Email String email) {

    authenticatedUserProvider.validateEmailOwnership(email);
    return ResponseEntity.ok(userService.findById(authenticatedUserProvider.getCurrentUserId()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable @NotNull Long id, @Valid @RequestBody UserRequest userRequest) {

    authenticatedUserProvider.validateOwnership(id);
    return ResponseEntity.ok(userService.updateUser(id, userRequest));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable @NotNull Long id) {
    authenticatedUserProvider.validateOwnership(id);
    userService.delete(id);
  }
}
