package com.davidantasdev.NomismaVault.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequest(
    @NotNull(message = "ID do portfólio não pode ser nulo")
    Long portfolioId,

    @NotNull(message = "ID do ativo não pode ser nulo")
    Long assetId,

    @NotNull(message = "Quantidade não pode ser nula")
    @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que 0")
    BigDecimal quantity,

    @NotNull(message = "Preço médio não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
    BigDecimal averagePrice,

    @NotNull(message = "Data de compra não pode ser nula")
    LocalDate purchaseDate,

    String notes
) {
}
