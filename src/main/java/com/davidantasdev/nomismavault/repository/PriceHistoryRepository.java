package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByAssetIdOrderByDateDesc(Long assetId);

    List<PriceHistory> findByAssetIdAndDateBetweenOrderByDateAsc(Long assetId, LocalDate startDate, LocalDate endDate);

    Optional<PriceHistory> findByAssetIdAndDate(Long assetId, LocalDate date);

    boolean existsByAssetIdAndDate(Long assetId, LocalDate date);

    Optional<PriceHistory> findTopByAssetIdOrderByDateDesc(Long assetId);
}
