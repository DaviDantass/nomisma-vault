package com.davidantasdev.AssetAPI.dto.integration;

import java.util.List;

public record BrapiResponse(
    List<BrapiQuote> results
) {
}
