package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto>>> getProductsPublic(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<ProductDto> products = productService.getProductsPublic(
                keyword, categoryId, brand, minPrice, maxPrice, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto>>> getProductsAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<ProductDto> products = productService.getProductsAdmin(
                keyword, categoryId, active, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySku(@PathVariable String sku) {
        ProductDto product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<String>>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.ok(brands));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String createdBy = currentUser != null ? currentUser.getUsername() : "admin";
        ProductDto product = productService.createProduct(request, createdBy);
        return new ResponseEntity<>(ApiResponse.ok("Product created successfully", product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String updatedBy = currentUser != null ? currentUser.getUsername() : "admin";
        ProductDto product = productService.updateProduct(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deactivated successfully"));
    }
}
