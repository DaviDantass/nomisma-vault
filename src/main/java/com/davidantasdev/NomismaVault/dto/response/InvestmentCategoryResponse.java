package com.davidantasdev.NomismaVault.dto.response;

import com.davidantasdev.NomismaVault.entity.enums.RiskLevel;

public record InvestmentCategoryResponse(
    Long id,
    String name,
    String description,
    RiskLevel riskLevel
) {
}
