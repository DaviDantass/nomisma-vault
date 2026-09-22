package com.davidantasdev.nomismavault.dto.request;

import com.davidantasdev.nomismavault.entity.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvestmentCategoryRequest(
    @NotBlank(message = "Nome da categoria não pode ser vazio") String name,
    String description,
    @NotNull(message = "Nível de risco não pode ser nulo") RiskLevel riskLevel) {}
