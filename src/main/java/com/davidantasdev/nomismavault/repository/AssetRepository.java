package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

  Optional<Asset> findByTicker(String ticker);

  boolean existsByTicker(String ticker);

  List<Asset> findByLastUpdateAfter(LocalDateTime threshold);
}
