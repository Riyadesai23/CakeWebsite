package com.example.ecommerce.service;

import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductRequest;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    PagedResponse<ProductDto> getProductsPublic(String keyword, Long categoryId, String brand,
                                               BigDecimal minPrice, BigDecimal maxPrice,
                                               int page, int size, String sortBy, String sortDir);

    PagedResponse<ProductDto> getProductsAdmin(String keyword, Long categoryId, Boolean active,
                                              int page, int size, String sortBy, String sortDir);

    ProductDto getProductById(Long id);

    ProductDto getProductBySku(String sku);

    ProductDto createProduct(ProductRequest request, String createdBy);

    ProductDto updateProduct(Long id, ProductRequest request, String updatedBy);

    void deleteProduct(Long id);

    List<String> getAllBrands();
}
