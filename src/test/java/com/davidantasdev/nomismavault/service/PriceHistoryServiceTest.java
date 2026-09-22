package com.davidantasdev.nomismavault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.davidantasdev.nomismavault.dto.request.PriceHistoryRequest;
import com.davidantasdev.nomismavault.dto.response.PriceHistoryResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceHistory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.mapper.PriceHistoryMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceHistoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceHistoryServiceTest {

  @Mock PriceHistoryRepository priceHistoryRepository;
  @Mock AssetRepository assetRepository;
  @Mock PriceHistoryMapper priceHistoryMapper;
  @InjectMocks PriceHistoryService priceHistoryService;

  @Test
  void createRejectsASecondSnapshotForTheSameAssetAndDate() {
    PriceHistoryRequest request = request();
    Asset asset = new Asset();
    when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
    when(priceHistoryRepository.existsByAssetIdAndDate(1L, request.date())).thenReturn(true);

    assertThrows(BusinessException.class, () -> priceHistoryService.create(request));

    verify(priceHistoryMapper, never()).toEntity(any());
    verify(priceHistoryRepository, never()).save(any());
  }

  @Test
  void createStoresOneObservedDailySnapshot() {
    PriceHistoryRequest request = request();
    Asset asset = new Asset();
    PriceHistory snapshot = new PriceHistory();
    PriceHistoryResponse response =
        new PriceHistoryResponse(1L, 1L, "PETR4", "Petrobras", request.price(), request.date());
    when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
    when(priceHistoryRepository.existsByAssetIdAndDate(1L, request.date())).thenReturn(false);
    when(priceHistoryMapper.toEntity(request)).thenReturn(snapshot);
    when(priceHistoryRepository.save(snapshot)).thenReturn(snapshot);
    when(priceHistoryMapper.toResponse(snapshot)).thenReturn(response);

    PriceHistoryResponse created = priceHistoryService.create(request);

    assertEquals(response, created);
    assertEquals(asset, snapshot.getAsset());
    assertEquals(request.date(), created.date());
    verify(priceHistoryRepository).save(snapshot);
  }

  private PriceHistoryRequest request() {
    return new PriceHistoryRequest(1L, new BigDecimal("38.50"), LocalDate.of(2026, 9, 21));
  }
}
