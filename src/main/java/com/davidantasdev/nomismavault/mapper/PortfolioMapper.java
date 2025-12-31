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
    Portfolio toEntity(PortfolioRequest portfolioRequest);

    List<PortfolioResponse> toResponseList(List<Portfolio> portfolioList);

    void updateEntityFromRequest(
            PortfolioRequest request,
            @MappingTarget Portfolio portfolio
    );
}


