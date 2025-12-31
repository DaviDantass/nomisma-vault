package com.davidantasdev.nomismavault.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import com.davidantasdev.nomismavault.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
    @NotNull(message = "ID do portfólio não pode ser nulo")
    Long portfolioId,

    @NotNull(message = "ID do ativo não pode ser nulo")
    Long assetId,

    @NotNull(message = "Tipo de transação não pode ser nulo")
    TransactionType type,

    @NotNull(message = "Quantidade não pode ser nula")
    @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que 0")
    BigDecimal quantity,

    @NotNull(message = "Preço não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
    BigDecimal price,

    @NotNull(message = "Data da transação não pode ser nula")
    LocalDate transactionDate,

    @DecimalMin(value = "0", message = "Taxa não pode ser negativa")
    BigDecimal fees,

    String notes
) {
}
