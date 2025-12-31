package com.davidantasdev.NomismaVault.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.davidantasdev.NomismaVault.entity.enums.AlertCondition;

import java.math.BigDecimal;

public record PriceAlertRequest(
    @NotNull(message = "ID do usuário não pode ser nulo")
    Long userId,

    @NotNull(message = "ID do ativo não pode ser nulo")
    Long assetId,

    @NotNull(message = "Preço alvo não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço alvo deve ser maior que 0")
    BigDecimal targetPrice,

    @NotNull(message = "Condição não pode ser nula")
    AlertCondition condition
) {
}
