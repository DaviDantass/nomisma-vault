package com.davidantasdev.nomismavault.controller;

import com.davidantasdev.nomismavault.dto.request.InvestmentCategoryRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentCategoryResponse;
import com.davidantasdev.nomismavault.service.InvestmentCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class InvestmentCategoryController {

    private final InvestmentCategoryService categoryService;

    public InvestmentCategoryController(InvestmentCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<InvestmentCategoryResponse>> findAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentCategoryResponse> findCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<InvestmentCategoryResponse> findCategoryByName(
            @PathVariable String name) {

        return ResponseEntity.ok(categoryService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<InvestmentCategoryResponse> createCategory(
            @Valid @RequestBody InvestmentCategoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentCategoryRequest request) {

        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
