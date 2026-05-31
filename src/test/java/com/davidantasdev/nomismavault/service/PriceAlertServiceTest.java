package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.entity.enums.AlertCondition;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.PriceAlertMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceAlertServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private PriceAlertMapper priceAlertMapper;

    @InjectMocks
    private PriceAlertService priceAlertService;

    @Test
    void create_shouldSetIsActiveTrue() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Asset asset = new Asset();
        asset.setId(2L);
        PriceAlertRequest request = new PriceAlertRequest(userId, 2L, new BigDecimal("100"), AlertCondition.ABOVE);
        PriceAlert entity = new PriceAlert();
        PriceAlert saved = new PriceAlert();
        saved.setIsActive(true);
        PriceAlertResponse response = new PriceAlertResponse(1L, userId, 2L, request.targetPrice(), request.condition(), true, null, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assetRepository.findById(2L)).thenReturn(Optional.of(asset));
        when(priceAlertMapper.toEntity(request)).thenReturn(entity);
        when(priceAlertRepository.save(entity)).thenReturn(saved);
        when(priceAlertMapper.toResponse(saved)).thenReturn(response);

        priceAlertService.create(userId, request);

        ArgumentCaptor<PriceAlert> captor = ArgumentCaptor.forClass(PriceAlert.class);
        verify(priceAlertRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsActive());
    }

    @Test
    void deactivate_shouldSaveAlertWithIsActiveFalse() {
        Long userId = 1L;
        Long alertId = 2L;
        User user = new User();
        user.setId(userId);
        PriceAlert alert = new PriceAlert();
        alert.setId(alertId);
        alert.setIsActive(true);
        PriceAlertResponse response = new PriceAlertResponse(alertId, userId, 3L, new BigDecimal("100"), AlertCondition.BELOW, false, null, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(priceAlertRepository.findByIdAndUser(alertId, user)).thenReturn(Optional.of(alert));
        when(priceAlertRepository.save(alert)).thenAnswer(invocation -> invocation.getArgument(0));
        when(priceAlertMapper.toResponse(alert)).thenReturn(response);

        priceAlertService.deactivate(userId, alertId);

        assertFalse(alert.getIsActive());
        verify(priceAlertRepository).save(alert);
    }

    @Test
    void findById_whenAlertNotFound_shouldThrowResourceNotFoundException() {
        Long userId = 1L;
        Long alertId = 2L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(priceAlertRepository.findByIdAndUser(alertId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> priceAlertService.findById(userId, alertId));
    }
}
