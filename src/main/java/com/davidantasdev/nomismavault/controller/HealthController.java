package com.davidantasdev.nomismavault.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "Health", description = "Status da aplicação")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Verifica se a API está online")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "NomismaVault",
                "timestamp", LocalDateTime.now()));
    }
}
