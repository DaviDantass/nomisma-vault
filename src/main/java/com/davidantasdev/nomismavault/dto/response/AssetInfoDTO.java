package com.davidantasdev.nomismavault.dto.response;

import java.math.BigDecimal;

public record AssetInfoDTO(
    String ticker,
    String name,
    BigDecimal price
) {
}
