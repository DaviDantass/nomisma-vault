package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.entity.Investment;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {

  @Mapping(target = "portfolioId", source = "portfolio.id")
  @Mapping(target = "assetId", source = "asset.id")
  InvestmentResponse toResponse(Investment investment);

  List<InvestmentResponse> toResponseList(List<Investment> investments);
}
