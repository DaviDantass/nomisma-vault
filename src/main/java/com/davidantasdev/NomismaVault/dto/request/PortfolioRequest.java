package com.davidantasdev.NomismaVault.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PortfolioRequest(
    @NotBlank(message = "Nome do portfólio não pode ser vazio")
    String name,

    String description,

    @NotNull(message = "ID do usuário não pode ser nulo")
    Long userId
) {
}
