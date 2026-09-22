package com.davidantasdev.nomismavault.repository;

import com.davidantasdev.nomismavault.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Page<User> findAll(Pageable pageable);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
