package com.davidantasdev.nomismavault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.davidantasdev.nomismavault.dto.request.AssetRequest;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.AssetMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.InvestmentCategoryRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

  @Mock AssetRepository assetRepository;
  @Mock InvestmentCategoryRepository categoryRepository;
  @Mock AssetMapper assetMapper;
  @InjectMocks AssetService assetService;

  @Test
  void createAssociatesCategoryAndSetsLastUpdate() {
    AssetRequest request = new AssetRequest("PETR4", "Petrobras", 1L, new BigDecimal("35.50"));
    Asset asset = new Asset();
    InvestmentCategory category = new InvestmentCategory();
    asset.setTicker("PETR4");

    when(assetMapper.toEntity(request)).thenReturn(asset);
    when(assetRepository.existsByTicker("PETR4")).thenReturn(false);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(assetRepository.save(asset)).thenReturn(asset);

    assetService.create(1L, request);

    assertEquals(category, asset.getCategory());
    org.junit.jupiter.api.Assertions.assertNotNull(asset.getLastUpdate());
    verify(assetRepository).save(asset);
  }

  @Test
  void createRejectsDuplicateTicker() {
    AssetRequest request = new AssetRequest("PETR4", "Petrobras", 1L, BigDecimal.TEN);
    Asset asset = new Asset();
    when(assetMapper.toEntity(request)).thenReturn(asset);
    asset.setTicker("PETR4");
    when(assetRepository.existsByTicker("PETR4")).thenReturn(true);

    assertThrows(BusinessException.class, () -> assetService.create(1L, request));
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createRejectsUnknownCategory() {
    AssetRequest request = new AssetRequest("PETR4", "Petrobras", 99L, BigDecimal.TEN);
    Asset asset = new Asset();
    when(assetMapper.toEntity(request)).thenReturn(asset);
    asset.setTicker("PETR4");
    when(assetRepository.existsByTicker("PETR4")).thenReturn(false);
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> assetService.create(99L, request));
    verify(assetRepository, never()).save(any());
  }

  @Test
  void deleteRejectsUnknownAsset() {
    when(assetRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> assetService.delete(99L));
    verify(assetRepository, never()).delete(any());
  }
}
