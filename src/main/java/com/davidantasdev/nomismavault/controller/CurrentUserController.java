package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Perfil do usuário autenticado, sem ids expostos na URL. */
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Tag(name = "Meu perfil", description = "Dados do usuário autenticado")
public class CurrentUserController {

  private final UserService userService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping
  public ResponseEntity<UserResponse> getProfile() {
    return ResponseEntity.ok(userService.findById(currentUserId()));
  }

  @PutMapping
  public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.updateUser(currentUserId(), request));
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteProfile() {
    userService.delete(currentUserId());
    return ResponseEntity.noContent().build();
  }

  private Long currentUserId() {
    return authenticatedUserProvider.getCurrentUserId();
  }
}
