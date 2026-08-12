package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.entity.enums.AlertCondition;
import com.davidantasdev.nomismavault.mapper.PriceAlertMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceAlertServiceTest {

    @Mock PriceAlertRepository priceAlertRepository;
    @Mock UserRepository userRepository;
    @Mock AssetRepository assetRepository;
    @Mock PriceAlertMapper priceAlertMapper;
    @InjectMocks PriceAlertService priceAlertService;

    @Test
    void createAssociatesUserAndAssetAndActivatesAlert() {
        User user = new User();
        Asset asset = new Asset();
        PriceAlert alert = new PriceAlert();
        PriceAlertRequest request = new PriceAlertRequest(1L, 2L, new BigDecimal("30.00"), AlertCondition.ABOVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assetRepository.findById(2L)).thenReturn(Optional.of(asset));
        when(priceAlertMapper.toEntity(request)).thenReturn(alert);
        when(priceAlertRepository.save(alert)).thenReturn(alert);

        priceAlertService.create(1L, request);

        assertEquals(user, alert.getUser());
        assertEquals(asset, alert.getAsset());
        assertEquals(Boolean.TRUE, alert.getIsActive());
        verify(priceAlertRepository).save(alert);
    }

    @Test
    void deactivateMarksOwnedAlertAsInactive() {
        User user = new User();
        PriceAlert alert = new PriceAlert();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(priceAlertRepository.findByIdAndUser(3L, user)).thenReturn(Optional.of(alert));

        priceAlertService.deactivate(1L, 3L);

        assertFalse(alert.getIsActive());
    }

    @Test
    void updatePricePersistsPositiveValue() {
        PriceAlert alert = new PriceAlert();
        BigDecimal newPrice = new BigDecimal("42.50");
        when(priceAlertRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(alert));
        when(priceAlertRepository.save(alert)).thenReturn(alert);

        priceAlertService.updatePrice(1L, 3L, newPrice);

        assertEquals(newPrice, alert.getTargetPrice());
        verify(priceAlertRepository).save(alert);
    }

    @Test
    void updatePriceRejectsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> priceAlertService.updatePrice(1L, 3L, BigDecimal.ZERO));

        verify(priceAlertRepository, never()).save(any());
    }

    @Test
    void updatePriceRejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> priceAlertService.updatePrice(1L, 3L, null));

        verify(priceAlertRepository, never()).save(any());
    }
}
