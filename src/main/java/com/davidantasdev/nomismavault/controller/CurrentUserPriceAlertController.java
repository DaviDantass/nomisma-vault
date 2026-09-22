package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.CurrentUserPriceAlertRequest;
import com.davidantasdev.nomismavault.dto.request.PriceAlertRequest;
import com.davidantasdev.nomismavault.dto.response.PageResponse;
import com.davidantasdev.nomismavault.dto.response.PriceAlertResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.PriceAlertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Alertas de preço pertencentes ao usuário autenticado. */
@RestController
@RequestMapping("/me/alerts")
@RequiredArgsConstructor
@Tag(name = "Meus alertas", description = "Alertas de preço do usuário autenticado")
public class CurrentUserPriceAlertController {

  private final PriceAlertService priceAlertService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping
  public ResponseEntity<PageResponse<PriceAlertResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.from(priceAlertService.findAllByUser(currentUserId(), pageable)));
  }

  @GetMapping("/active")
  public ResponseEntity<PageResponse<PriceAlertResponse>> findActive(Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.from(priceAlertService.findActiveByUser(currentUserId(), pageable)));
  }

  @GetMapping("/{alertId}")
  public ResponseEntity<PriceAlertResponse> findById(@PathVariable Long alertId) {
    return ResponseEntity.ok(priceAlertService.findById(currentUserId(), alertId));
  }

  @PostMapping
  public ResponseEntity<PriceAlertResponse> create(
      @Valid @RequestBody CurrentUserPriceAlertRequest request) {
    Long userId = currentUserId();
    PriceAlertRequest serviceRequest =
        new PriceAlertRequest(
            userId, request.assetId(), request.targetPrice(), request.condition());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(priceAlertService.create(userId, serviceRequest));
  }

  @PatchMapping("/{alertId}/deactivate")
  public ResponseEntity<PriceAlertResponse> deactivate(@PathVariable Long alertId) {
    return ResponseEntity.ok(priceAlertService.deactivate(currentUserId(), alertId));
  }

  @PatchMapping("/{alertId}/price")
  public ResponseEntity<PriceAlertResponse> updatePrice(
      @PathVariable Long alertId, @RequestParam BigDecimal newPrice) {
    return ResponseEntity.ok(priceAlertService.updatePrice(currentUserId(), alertId, newPrice));
  }

  @DeleteMapping("/{alertId}")
  public ResponseEntity<Void> delete(@PathVariable Long alertId) {
    priceAlertService.delete(currentUserId(), alertId);
    return ResponseEntity.noContent().build();
  }

  private Long currentUserId() {
    return authenticatedUserProvider.getCurrentUserId();
  }
}
