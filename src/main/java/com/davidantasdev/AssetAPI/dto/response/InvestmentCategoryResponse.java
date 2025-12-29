package com.davidantasdev.AssetAPI.dto.response;

import com.davidantasdev.AssetAPI.entity.enums.RiskLevel;

public record InvestmentCategoryResponse(
    Long id,
    String name,
    String description,
    RiskLevel riskLevel
) {
}
