package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.PortfolioRequest;
import com.davidantasdev.nomismavault.dto.response.PageResponse;
import com.davidantasdev.nomismavault.dto.response.PortfolioResponse;
import com.davidantasdev.nomismavault.dto.response.PortfolioSummaryResponse;
import com.davidantasdev.nomismavault.security.AuthenticatedUserProvider;
import com.davidantasdev.nomismavault.service.PortfolioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de carteira do usuario autenticado.
 *
 * <p>Evita expor o identificador do usuario na URL para as telas comuns. As rotas {@code
 * /users/{userId}/portfolios} permanecem disponiveis para compatibilidade.
 */
@RestController
@RequestMapping("/me/portfolios")
@RequiredArgsConstructor
@Tag(name = "Minha carteira", description = "Carteiras do usuario autenticado")
public class CurrentUserPortfolioController {

  private final PortfolioService portfolioService;
  private final AuthenticatedUserProvider authenticatedUserProvider;

  @GetMapping
  public ResponseEntity<PageResponse<PortfolioResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(
        PageResponse.from(portfolioService.findAllByUser(currentUserId(), pageable)));
  }

  @GetMapping("/{portfolioId}")
  public ResponseEntity<PortfolioResponse> findById(@PathVariable Long portfolioId) {
    return ResponseEntity.ok(portfolioService.findById(currentUserId(), portfolioId));
  }

  @GetMapping("/{portfolioId}/summary")
  public ResponseEntity<PortfolioSummaryResponse> getSummary(@PathVariable Long portfolioId) {
    return ResponseEntity.ok(portfolioService.getSummary(currentUserId(), portfolioId));
  }

  @PostMapping
  public ResponseEntity<PortfolioResponse> create(@Valid @RequestBody PortfolioRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(portfolioService.createPortfolio(request, currentUserId()));
  }

  @PutMapping("/{portfolioId}")
  public ResponseEntity<PortfolioResponse> update(
      @PathVariable Long portfolioId, @Valid @RequestBody PortfolioRequest request) {
    return ResponseEntity.ok(
        portfolioService.updatePortfolio(currentUserId(), portfolioId, request));
  }

  @DeleteMapping("/{portfolioId}")
  public ResponseEntity<Void> delete(@PathVariable Long portfolioId) {
    portfolioService.delete(currentUserId(), portfolioId);
    return ResponseEntity.noContent().build();
  }

  private Long currentUserId() {
    return authenticatedUserProvider.getCurrentUserId();
  }
}
