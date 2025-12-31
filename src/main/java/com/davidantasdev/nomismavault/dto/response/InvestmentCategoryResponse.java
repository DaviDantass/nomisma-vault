package com.davidantasdev.nomismavault.dto.response;

import com.davidantasdev.nomismavault.entity.enums.RiskLevel;

public record InvestmentCategoryResponse(
    Long id,
    String name,
    String description,
    RiskLevel riskLevel
) {
}
