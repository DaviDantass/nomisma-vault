package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.InvestmentRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentResponse;
import com.davidantasdev.nomismavault.dto.response.InvestmentWithPnLResponse;
import com.davidantasdev.nomismavault.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/investments")
@Tag(name = "Investments", description = "Gestão de investimentos e cálculo de P&L em tempo real")
public class InvestmentController {

        private final InvestmentService investmentService;

        public InvestmentController(InvestmentService investmentService) {
                this.investmentService = investmentService;
        }

        @GetMapping
        public ResponseEntity<Page<InvestmentResponse>> findAllInvestmentsByPortfolio(
                        @PathVariable Long portfolioId,
                        Pageable pageable) {
                return ResponseEntity.ok(
                                investmentService.findAllByPortfolio(portfolioId, pageable));
        }

        @GetMapping("/{investmentId}/pnl")
        public ResponseEntity<InvestmentWithPnLResponse> getInvestmentPnL(
                        @PathVariable Long portfolioId,
                        @PathVariable Long investmentId) {
                return ResponseEntity.ok(
                                investmentService.getInvestmentWithPnL(portfolioId, investmentId));
        }

        @GetMapping("/{investmentId}")
        public ResponseEntity<InvestmentResponse> findInvestmentById(
                        @PathVariable Long portfolioId,
                        @PathVariable Long investmentId) {
                return ResponseEntity.ok(
                                investmentService.findById(portfolioId, investmentId));
        }

        @PostMapping
        public ResponseEntity<InvestmentResponse> createInvestment(
                        @PathVariable Long portfolioId,
                        @Valid @RequestBody InvestmentRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(investmentService.create(
                                                portfolioId,
                                                request.assetId(),
                                                request));
        }

        @PutMapping("/{investmentId}")
        public ResponseEntity<InvestmentResponse> updateInvestment(
                        @PathVariable Long portfolioId,
                        @PathVariable Long investmentId,
                        @Valid @RequestBody InvestmentRequest request) {
                return ResponseEntity.ok(
                                investmentService.update(
                                                portfolioId,
                                                investmentId,
                                                request.assetId(),
                                                request));
        }

        @DeleteMapping("/{investmentId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteInvestment(
                        @PathVariable Long portfolioId,
                        @PathVariable Long investmentId) {

                investmentService.delete(portfolioId, investmentId);
        }
}
