package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // GET
    // /api/portfolios/{portfolioId}/transactions?page=0&size=10&sort=transactionDate,desc
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> findAllTransactionsByPortfolio(
            @PathVariable Long portfolioId,
            Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.findAllByPortfolio(portfolioId, pageable));
    }

    // GET /api/portfolios/{portfolioId}/transactions/{transactionId}
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> findTransactionById(
            @PathVariable Long portfolioId,
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                transactionService.findById(portfolioId, transactionId));
    }

    // GET
    // /api/portfolios/{portfolioId}/transactions/period?start=2024-01-01&end=2024-12-31
    @GetMapping("/period")
    public ResponseEntity<Page<TransactionResponse>> findTransactionsByPeriod(
            @PathVariable Long portfolioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByPeriod(portfolioId, start, end, pageable));
    }

    // POST /api/portfolios/{portfolioId}/transactions
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long portfolioId,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.create(portfolioId, request));
    }

    // DELETE /api/portfolios/{portfolioId}/transactions/{transactionId}
    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @PathVariable Long portfolioId,
            @PathVariable Long transactionId) {

        transactionService.delete(portfolioId, transactionId);
    }
}
