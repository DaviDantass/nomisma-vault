package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.response.DashboardResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me/dashboard")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping
  public ResponseEntity<DashboardResponse> get() {
    return ResponseEntity.ok(
        dashboardService.getForUser(authenticatedUserProvider.getCurrentUserId()));
  }
}
