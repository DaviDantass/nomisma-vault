package com.davidantasdev.AssetAPI.dto.response;

import java.time.LocalDateTime;

public record UserResponse (
    Long id,
    String name,
    String email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
