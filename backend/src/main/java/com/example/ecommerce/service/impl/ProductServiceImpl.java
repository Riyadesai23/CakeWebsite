package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Inventory;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsPublic(String keyword, Long categoryId, String brand,
                                                      BigDecimal minPrice, BigDecimal maxPrice,
                                                      int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String trimmedBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;

        Page<Product> products = productRepository.searchProductsPublic(
                trimmedKeyword, categoryId, trimmedBrand, minPrice, maxPrice, pageable);

        List<ProductDto> content = products.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, products.getNumber(), products.getSize(),
                products.getTotalElements(), products.getTotalPages(), products.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsAdmin(String keyword, Long categoryId, Boolean active,
                                                     int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<Product> products = productRepository.searchProductsAdmin(
                trimmedKeyword, categoryId, active, pageable);

        List<ProductDto> content = products.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, products.getNumber(), products.getSize(),
                products.getTotalElements(), products.getTotalPages(), products.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return mapToDto(product);
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductRequest request, String createdBy) {
        if (productRepository.existsBySku(request.getSku().trim().toUpperCase())) {
            throw new BadRequestException("Product SKU already exists!", "SKU_EXISTS");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setSku(request.getSku().trim().toUpperCase());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());
        product.setUnit(request.getUnit() != null ? request.getUnit() : "Piece");
        product.setMinimumStockLevel(request.getMinimumStockLevel() != null ? request.getMinimumStockLevel() : 5);
        product.setActive(request.getActive() != null ? request.getActive() : true);
        product.setCreatedBy(createdBy != null ? createdBy : "admin");
        product.setUpdatedBy(createdBy != null ? createdBy : "admin");

        Product savedProduct = productRepository.save(product);

        // Initialize Inventory record
        int initialStock = request.getInitialQuantity() != null ? request.getInitialQuantity() : 0;
        Inventory inventory = new Inventory(savedProduct, initialStock, savedProduct.getMinimumStockLevel());
        inventoryRepository.save(inventory);

        return mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductRequest request, String updatedBy) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getSku().equalsIgnoreCase(request.getSku().trim()) &&
                productRepository.existsBySku(request.getSku().trim().toUpperCase())) {
            throw new BadRequestException("Product SKU already exists!", "SKU_EXISTS");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName().trim());
        product.setSku(request.getSku().trim().toUpperCase());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());
        product.setUnit(request.getUnit());
        if (request.getMinimumStockLevel() != null) {
            product.setMinimumStockLevel(request.getMinimumStockLevel());
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        product.setUpdatedBy(updatedBy != null ? updatedBy : "admin");

        Product updatedProduct = productRepository.save(product);

        // Synchronize Inventory minimum stock level if needed
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(id);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setMinimumStockLevel(updatedProduct.getMinimumStockLevel());
            inventoryRepository.save(inventory);
        }

        return mapToDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findAllActiveBrands();
    }

    public ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setFinalPrice(product.getFinalPrice());
        dto.setMinimumStockLevel(product.getMinimumStockLevel());
        dto.setImageUrl(product.getImageUrl());
        dto.setBrand(product.getBrand());
        dto.setUnit(product.getUnit());
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setUpdatedBy(product.getUpdatedBy());

        // Attach real-time stock
        Optional<Inventory> inv = inventoryRepository.findByProductId(product.getId());
        if (inv.isPresent()) {
            dto.setAvailableQuantity(inv.get().getAvailableQuantity());
            dto.setInStock(inv.get().getAvailableQuantity() > 0);
            dto.setLowStock(inv.get().isLowStock());
        } else {
            dto.setAvailableQuantity(0);
            dto.setInStock(false);
            dto.setLowStock(true);
        }

        return dto;
    }
}
