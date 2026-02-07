package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceAlertMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "assetId", source = "asset.id")
    PriceAlertResponse toResponse(PriceAlert priceAlert);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "triggeredAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PriceAlert toEntity(PriceAlertRequest request);

    List<PriceAlertResponse> toResponseList(List<PriceAlert> priceAlerts);
}
