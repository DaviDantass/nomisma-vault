package com.davidantasdev.nomismavault.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/alerts")
@RequiredArgsConstructor
@Tag(name = "Price Alerts", description = "Alertas de preço para ativos")
public class PriceAlertController {

  private final PriceAlertService priceAlertService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping
  public ResponseEntity<PageResponse<PriceAlertResponse>> findAllAlertsByUser(
      @PathVariable Long userId, Pageable pageable) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.ok(PageResponse.from(priceAlertService.findAllByUser(userId, pageable)));
  }

  @GetMapping("/active")
  public ResponseEntity<PageResponse<PriceAlertResponse>> findActiveAlertsByUser(
      @PathVariable Long userId, Pageable pageable) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.ok(
        PageResponse.from(priceAlertService.findActiveByUser(userId, pageable)));
  }

  @GetMapping("/{alertId}")
  public ResponseEntity<PriceAlertResponse> findAlertById(
      @PathVariable Long userId, @PathVariable Long alertId) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.ok(priceAlertService.findById(userId, alertId));
  }

  @PostMapping
  public ResponseEntity<PriceAlertResponse> createAlert(
      @PathVariable Long userId, @Valid @RequestBody PriceAlertRequest request) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(priceAlertService.create(userId, request));
  }

  @PatchMapping("/{alertId}/deactivate")
  public ResponseEntity<PriceAlertResponse> deactivateAlert(
      @PathVariable Long userId, @PathVariable Long alertId) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.ok(priceAlertService.deactivate(userId, alertId));
  }

  @PatchMapping("/{alertId}/price")
  public ResponseEntity<PriceAlertResponse> updateAlertPrice(
      @PathVariable Long userId, @PathVariable Long alertId, @RequestParam BigDecimal newPrice) {
    authenticatedUserProvider.validateOwnership(userId);
    return ResponseEntity.ok(priceAlertService.updatePrice(userId, alertId, newPrice));
  }

  @DeleteMapping("/{alertId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAlert(@PathVariable Long userId, @PathVariable Long alertId) {
    authenticatedUserProvider.validateOwnership(userId);
    priceAlertService.delete(userId, alertId);
  }
}
