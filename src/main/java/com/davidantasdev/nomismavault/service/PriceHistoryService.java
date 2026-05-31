package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.PriceHistoryRequest;
import com.davidantasdev.nomismavault.dto.response.PriceHistoryResponse;
import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceHistory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.PriceHistoryMapper;
import com.davidantasdev.nomismavault.repository.AssetRepository;
import com.davidantasdev.nomismavault.repository.PriceHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final AssetRepository assetRepository;
    private final PriceHistoryMapper priceHistoryMapper;

    public PriceHistoryService(
            PriceHistoryRepository priceHistoryRepository,
            AssetRepository assetRepository,
            PriceHistoryMapper priceHistoryMapper) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.assetRepository = assetRepository;
        this.priceHistoryMapper = priceHistoryMapper;
    }

    public List<PriceHistoryResponse> findAll() {
        return priceHistoryMapper.toResponseList(priceHistoryRepository.findAll());
    }

    public PriceHistoryResponse findById(Long id) {
        PriceHistory priceHistory = priceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de preço não encontrado"));
        return priceHistoryMapper.toResponse(priceHistory);
    }

    public List<PriceHistoryResponse> findByAssetId(Long assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Ativo não encontrado");
        }
        return priceHistoryMapper.toResponseList(
                priceHistoryRepository.findByAssetIdOrderByDateDesc(assetId));
    }

    public List<PriceHistoryResponse> findByAssetIdAndDateRange(Long assetId, LocalDate startDate, LocalDate endDate) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Ativo não encontrado");
        }
        return priceHistoryMapper.toResponseList(
                priceHistoryRepository.findByAssetIdAndDateBetweenOrderByDateAsc(assetId, startDate, endDate));
    }

    public PriceHistoryResponse findLatestByAssetId(Long assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Ativo não encontrado");
        }
        PriceHistory priceHistory = priceHistoryRepository.findTopByAssetIdOrderByDateDesc(assetId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Nenhum histórico de preço encontrado para este ativo"));
        return priceHistoryMapper.toResponse(priceHistory);
    }

    @Transactional
    public PriceHistoryResponse create(PriceHistoryRequest request) {
        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));

        if (priceHistoryRepository.existsByAssetIdAndDate(request.assetId(), request.date())) {
            throw new BusinessException("Já existe um registro de preço para este ativo nesta data");
        }

        PriceHistory priceHistory = priceHistoryMapper.toEntity(request);
        priceHistory.setAsset(asset);

        PriceHistory saved = priceHistoryRepository.save(priceHistory);
        return priceHistoryMapper.toResponse(saved);
    }

    @Transactional
    public PriceHistoryResponse update(Long id, PriceHistoryRequest request) {
        PriceHistory priceHistory = priceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de preço não encontrado"));

        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));

        priceHistoryRepository.findByAssetIdAndDate(request.assetId(), request.date())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Já existe um registro de preço para este ativo nesta data");
                    }
                });

        priceHistory.setAsset(asset);
        priceHistory.setPrice(request.price());
        priceHistory.setDate(request.date());

        PriceHistory saved = priceHistoryRepository.save(priceHistory);
        return priceHistoryMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!priceHistoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Histórico de preço não encontrado");
        }
        priceHistoryRepository.deleteById(id);
    }
}
