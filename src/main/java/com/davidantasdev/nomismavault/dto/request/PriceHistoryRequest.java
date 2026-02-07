package com.davidantasdev.nomismavault.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceHistoryRequest(
        @NotNull(message = "ID do ativo é obrigatório") Long assetId,

        @NotNull(message = "Preço é obrigatório") @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0") BigDecimal price,

        @NotNull(message = "Data é obrigatória") LocalDate date) {
}
