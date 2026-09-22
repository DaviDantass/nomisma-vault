package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.Portfolio;
import com.davidantasdev.nomismavault.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

  Page<Portfolio> findAllByUser(User user, Pageable pageable);

  List<Portfolio> findAllByUser(User user);

  Optional<Portfolio> findByIdAndUser(Long id, User user);
}
