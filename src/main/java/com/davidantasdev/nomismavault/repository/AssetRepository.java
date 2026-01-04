package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    Optional<Asset> findByTicker(String ticker);
    
    boolean existsByTicker(String ticker);
}
