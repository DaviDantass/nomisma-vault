package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.TransactionRequest;
import com.davidantasdev.nomismavault.dto.response.TransactionResponse;
import com.davidantasdev.nomismavault.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/portfolios/{portfolioId}/transactions")
@Tag(name = "Transactions", description = "Histórico de compras e vendas de ativos")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> findAllTransactionsByPortfolio(
            @PathVariable Long portfolioId,
            Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.findAllByPortfolio(portfolioId, pageable));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> findTransactionById(
            @PathVariable Long portfolioId,
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                transactionService.findById(portfolioId, transactionId));
    }

    @GetMapping("/period")
    public ResponseEntity<Page<TransactionResponse>> findTransactionsByPeriod(
            @PathVariable Long portfolioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByPeriod(portfolioId, start, end, pageable));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long portfolioId,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.create(portfolioId, request));
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @PathVariable Long portfolioId,
            @PathVariable Long transactionId) {

        transactionService.delete(portfolioId, transactionId);
    }
}
