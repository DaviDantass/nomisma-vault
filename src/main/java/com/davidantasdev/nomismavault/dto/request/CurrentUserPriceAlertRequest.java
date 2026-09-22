package com.davidantasdev.nomismavault.dto.request;

import com.davidantasdev.nomismavault.entity.enums.AlertCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Criação de alerta pelo usuário autenticado, sem permitir informar outro usuário. */
public record CurrentUserPriceAlertRequest(
    @NotNull Long assetId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal targetPrice,
    @NotNull AlertCondition condition) {}
