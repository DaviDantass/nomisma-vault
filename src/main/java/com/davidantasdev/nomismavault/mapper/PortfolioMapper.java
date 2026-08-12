package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.entity.Portfolio;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PortfolioMapper {

    @Mapping(target = "userId", source = "user.id")
    PortfolioResponse toResponse(Portfolio portfolio);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "investments", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "snapshots", ignore = true)
    Portfolio toEntity(PortfolioRequest portfolioRequest);

    List<PortfolioResponse> toResponseList(List<Portfolio> portfolioList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "investments", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "snapshots", ignore = true)
    void updateEntityFromRequest(
            PortfolioRequest request,
            @MappingTarget Portfolio portfolio
    );
}


