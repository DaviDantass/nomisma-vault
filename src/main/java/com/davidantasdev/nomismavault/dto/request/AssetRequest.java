package com.davidantasdev.nomismavault.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record AssetRequest(
    @NotBlank(message = "Ticker não pode ser vazio")
    String ticker,

    @NotBlank(message = "Nome do ativo não pode ser vazio")
    String name,

    @NotNull(message = "Categoria não pode ser nula")
    Long categoryId,

    @DecimalMin(value = "0", message = "Preço não pode ser negativo")
    BigDecimal currentPrice
) {
}
