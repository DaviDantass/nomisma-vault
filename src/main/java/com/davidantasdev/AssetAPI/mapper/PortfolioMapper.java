package com.davidantasdev.AssetAPI.mapper;

import com.davidantasdev.AssetAPI.dto.request.PortfolioRequest;
import com.davidantasdev.AssetAPI.dto.response.PortfolioResponse;
import com.davidantasdev.AssetAPI.entity.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PortfolioMapper {

    PortfolioResponse toResponse(Portfolio portfolio);

    Portfolio toEntity(PortfolioRequest portfolioRequest);

    List<PortfolioResponse> toResponseList(List<Portfolio> portfolioList);

    void updateEntityFromRequest(PortfolioRequest request, @MappingTarget Portfolio portfolio);
}

