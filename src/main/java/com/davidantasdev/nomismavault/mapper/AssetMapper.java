package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.AssetRequest;
import com.davidantasdev.nomismavault.dto.response.AssetResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "riskLevel", source = "category.riskLevel")
    AssetResponse toResponse(Asset asset);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "investments", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "priceHistories", ignore = true)
    @Mapping(target = "priceAlerts", ignore = true)
    Asset toEntity(AssetRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "investments", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "priceHistories", ignore = true)
    @Mapping(target = "priceAlerts", ignore = true)
    void updateEntityFromRequest(AssetRequest request, @MappingTarget Asset asset);

    List<AssetResponse> toResponseList(List<Asset> assets);
}
