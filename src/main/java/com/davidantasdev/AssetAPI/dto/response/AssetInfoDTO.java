package com.davidantasdev.AssetAPI.dto.response;

import java.math.BigDecimal;

public record AssetInfoDTO(
    String ticker,
    String name,
    BigDecimal price
) {
}
