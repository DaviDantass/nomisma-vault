package com.davidantasdev.nomismavault.service;

import com.davidantasdev.nomismavault.dto.request.InvestmentCategoryRequest;
import com.davidantasdev.nomismavault.dto.response.InvestmentCategoryResponse;
import com.davidantasdev.nomismavault.entity.InvestmentCategory;
import com.davidantasdev.nomismavault.exception.BusinessException;
import com.davidantasdev.nomismavault.exception.ResourceNotFoundException;
import com.davidantasdev.nomismavault.mapper.InvestmentCategoryMapper;
import com.davidantasdev.nomismavault.repository.InvestmentCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvestmentCategoryService {
    private final InvestmentCategoryRepository categoryRepository;
    private final InvestmentCategoryMapper categoryMapper;

    public InvestmentCategoryService(
            InvestmentCategoryRepository categoryRepository,
            InvestmentCategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<InvestmentCategoryResponse> findAll() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    public InvestmentCategoryResponse findById(Long id) {
        InvestmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        return categoryMapper.toResponse(category);
    }

    public InvestmentCategoryResponse findByName(String name) {
        InvestmentCategory category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found " + name));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public InvestmentCategoryResponse create(InvestmentCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException("Category already exists with name " + request.name());
        }

        InvestmentCategory category = categoryMapper.toEntity(request);
        InvestmentCategory saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Transactional
    public InvestmentCategoryResponse update(Long id, InvestmentCategoryRequest request) {
        InvestmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category not found "));

        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new BusinessException("Category already exists with name " + request.name());
        }

        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        InvestmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category not found"));

        categoryRepository.delete(category);
    }

}
