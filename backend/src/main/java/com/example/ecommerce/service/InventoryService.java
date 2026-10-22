package com.example.ecommerce.service;

import com.example.ecommerce.dto.InventoryDto;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.StockUpdateRequest;

import java.util.List;

public interface InventoryService {
    InventoryDto getInventoryByProductId(Long productId);
    PagedResponse<InventoryDto> searchInventory(String keyword, boolean lowStockOnly, int page, int size);
    List<InventoryDto> getLowStockProducts();
    InventoryDto updateStock(Long productId, StockUpdateRequest request);
    void deductStock(Long productId, int quantity);
    void restoreStock(Long productId, int quantity);
    void checkStockAvailability(Long productId, int requestedQuantity);
}
