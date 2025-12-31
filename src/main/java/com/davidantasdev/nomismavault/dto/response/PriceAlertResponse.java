package com.davidantasdev.nomismavault.dto.response;

import com.davidantasdev.nomismavault.entity.enums.AlertCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceAlertResponse(
    Long id,
    Long userId,
    Long assetId,
    BigDecimal targetPrice,
    AlertCondition condition,
    Boolean isActive,
    LocalDateTime triggeredAt,
    LocalDateTime createdAt
) {
}
