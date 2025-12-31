package com.davidantasdev.NomismaVault.controller;

import com.davidantasdev.NomismaVault.dto.request.PortfolioRequest;
import com.davidantasdev.NomismaVault.dto.response.PortfolioResponse;
import com.davidantasdev.NomismaVault.service.PortfolioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@Validated
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> findAll() {
        return ResponseEntity.ok(portfolioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> findById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(portfolioService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioResponse>> findAllByUserId(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(portfolioService.findAllByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody PortfolioRequest portfolioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(portfolioRequest, userId));
    }

    @PutMapping("/user/{userId}/portfolio/{portfolioId}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long portfolioId,
            @Valid @RequestBody PortfolioRequest portfolioRequest) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(userId, portfolioId, portfolioRequest));
    }

    @DeleteMapping("/user/{userId}/portfolio/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long portfolioId) {
        portfolioService.delete(userId, portfolioId);
    }
}
