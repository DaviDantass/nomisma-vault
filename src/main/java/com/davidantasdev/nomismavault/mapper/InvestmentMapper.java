package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.entity.Investment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {

    @Mapping(target = "portfolioId", source = "portfolio.id")
    @Mapping(target = "assetId", source = "asset.id")
    InvestmentResponse toResponse(Investment investment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Investment toEntity(InvestmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(InvestmentRequest request, @MappingTarget Investment investment);

    List<InvestmentResponse> toResponseList(List<Investment> investments);
}
