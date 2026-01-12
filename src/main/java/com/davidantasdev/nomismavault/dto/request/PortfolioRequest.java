package com.davidantasdev.nomismavault.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PortfolioRequest(
    @NotBlank(message = "Nome do portfólio não pode ser vazio")
    String name,

    String description
) {
}
