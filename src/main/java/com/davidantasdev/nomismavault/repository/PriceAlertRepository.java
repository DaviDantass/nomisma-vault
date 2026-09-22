package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Asset;
import com.davidantasdev.nomismavault.entity.PriceAlert;
import com.davidantasdev.nomismavault.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

  long countByUserAndIsActive(User user, Boolean isActive);

  Page<PriceAlert> findAllByUser(User user, Pageable pageable);

  Page<PriceAlert> findAllByUserAndIsActive(User user, Boolean isActive, Pageable pageable);

  Optional<PriceAlert> findByIdAndUser(Long id, User user);

  List<PriceAlert> findByAssetAndIsActiveTrue(Asset asset);

  Optional<PriceAlert> findByIdAndUserId(Long id, Long userId);
  ;

  @Query("select distinct alert.asset from PriceAlert alert where alert.isActive = true")
  List<Asset> findDistinctAssetsWithActiveAlerts();
}
