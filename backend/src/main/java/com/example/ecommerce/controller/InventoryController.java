package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.InventoryDto;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.StockUpdateRequest;
import com.example.ecommerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InventoryDto>>> searchInventory(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean lowStockOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<InventoryDto> response = inventoryService.searchInventory(keyword, lowStockOnly, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockProducts() {
        List<InventoryDto> list = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryByProductId(@PathVariable Long productId) {
        InventoryDto dto = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryDto>> updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {

        InventoryDto dto = inventoryService.updateStock(productId, request);
        return ResponseEntity.ok(ApiResponse.ok("Inventory updated successfully", dto));
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<ApiResponse<InventoryDto>> addStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {

        StockUpdateRequest request = new StockUpdateRequest(quantity, "ADD");
        InventoryDto dto = inventoryService.updateStock(productId, request);
        return ResponseEntity.ok(ApiResponse.ok("Stock added successfully", dto));
    }

    @PostMapping("/{productId}/remove")
    public ResponseEntity<ApiResponse<InventoryDto>> removeStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {

        StockUpdateRequest request = new StockUpdateRequest(quantity, "SUBTRACT");
        InventoryDto dto = inventoryService.updateStock(productId, request);
        return ResponseEntity.ok(ApiResponse.ok("Stock decreased successfully", dto));
    }
}
