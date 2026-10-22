package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesAdmin() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName().trim())) {
            throw new BadRequestException("Category name already exists!", "CATEGORY_NAME_EXISTS");
        }
        if (categoryRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new BadRequestException("Category code already exists!", "CATEGORY_CODE_EXISTS");
        }

        Category category = new Category(
                request.getName().trim(),
                request.getCode().trim().toUpperCase(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getImageUrl() != null ? request.getImageUrl().trim() : null
        );

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getName().equalsIgnoreCase(request.getName().trim()) &&
                categoryRepository.existsByName(request.getName().trim())) {
            throw new BadRequestException("Category with this name already exists!", "CATEGORY_NAME_EXISTS");
        }

        if (!category.getCode().equalsIgnoreCase(request.getCode().trim()) &&
                categoryRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new BadRequestException("Category with this code already exists!", "CATEGORY_CODE_EXISTS");
        }

        category.setName(request.getName().trim());
        category.setCode(request.getCode().trim().toUpperCase());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCode(category.getCode());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setActive(category.isActive());
        return dto;
    }
}
