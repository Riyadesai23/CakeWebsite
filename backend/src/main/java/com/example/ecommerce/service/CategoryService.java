package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllActiveCategories();
    List<CategoryDto> getAllCategoriesAdmin();
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CategoryRequest request);
    CategoryDto updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
