package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import com.davidantasdev.nomismavault.entity.User;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.PriceAlertMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceAlertRepository;
import com.davidantasdev.nomismavault.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PriceAlertService {
    private final PriceAlertRepository priceAlertRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PriceAlertMapper priceAlertMapper;

    public PriceAlertService(
            PriceAlertRepository priceAlertRepository,
            UserRepository userRepository,
            AssetRepository assetRepository,
            PriceAlertMapper priceAlertMapper) {
        this.priceAlertRepository = priceAlertRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.priceAlertMapper = priceAlertMapper;
    }

    public Page<PriceAlertResponse> findAllByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return priceAlertRepository.findAllByUser(user, pageable)
                .map(priceAlertMapper::toResponse);
    }

    public Page<PriceAlertResponse> findActiveByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return priceAlertRepository.findAllByUserAndIsActive(user, true, pageable)
                .map(priceAlertMapper::toResponse);
    }
    public PriceAlertResponse findById(Long userId, Long alertId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PriceAlert alert = priceAlertRepository.findByIdAndUser(alertId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        return priceAlertMapper.toResponse(alert);
    }

    @Transactional
    public PriceAlertResponse create(Long userId, PriceAlertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        PriceAlert alert = priceAlertMapper.toEntity(request);
        alert.setUser(user);
        alert.setAsset(asset);
        alert.setIsActive(true);
        PriceAlert saved = priceAlertRepository.save(alert);
        return priceAlertMapper.toResponse(saved);
    }
    @Transactional
    public PriceAlertResponse deactivate(Long userId, Long alertId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        PriceAlert alert = priceAlertRepository.findByIdAndUser(alertId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        alert.setIsActive(false);
        PriceAlert saved = priceAlertRepository.save(alert);
        return priceAlertMapper.toResponse(saved);
    }
    @Transactional
    public void delete(Long userId, Long alertId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PriceAlert alert = priceAlertRepository.findByIdAndUser(alertId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        priceAlertRepository.delete(alert);
    }
    public PriceAlertResponse updatePrice(Long userId, Long alertId, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("invalid price value");
        }
        PriceAlert alert = priceAlertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found for this user"));

        alert.setTargetPrice(newPrice);
        return priceAlertMapper.toResponse(priceAlertRepository.save(alert));
    }
}
