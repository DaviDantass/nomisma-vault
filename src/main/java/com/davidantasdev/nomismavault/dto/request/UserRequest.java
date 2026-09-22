package com.davidantasdev.nomismavault.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = "Nome não pode ser vazio") String name,
    @NotBlank(message = "Email não pode ser vazio") @Email(message = "Email inválido") String email,
    @NotBlank(message = "Senha não pode ser vazia") String password) {}
