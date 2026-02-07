package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.InvestmentCategoryRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentCategoryResponse;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvestmentCategoryMapper {

    InvestmentCategoryResponse toResponse(InvestmentCategory category);

    @Mapping(target = "id", ignore = true)
    InvestmentCategory toEntity(InvestmentCategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(InvestmentCategoryRequest request, @MappingTarget InvestmentCategory category);

    List<InvestmentCategoryResponse> toResponseList(List<InvestmentCategory> categories);
}
