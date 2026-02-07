package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.PriceHistoryRequest;
import com.davidantasdev.nomismavault.dto.response.PriceHistoryResponse;
import com.davidantasdev.nomismavault.entity.PriceHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceHistoryMapper {

    @Mapping(target = "assetId", source = "asset.id")
    @Mapping(target = "assetTicker", source = "asset.ticker")
    @Mapping(target = "assetName", source = "asset.name")
    PriceHistoryResponse toResponse(PriceHistory priceHistory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asset", ignore = true)
    PriceHistory toEntity(PriceHistoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asset", ignore = true)
    void updateEntityFromRequest(PriceHistoryRequest request, @MappingTarget PriceHistory priceHistory);

    List<PriceHistoryResponse> toResponseList(List<PriceHistory> priceHistories);
}
