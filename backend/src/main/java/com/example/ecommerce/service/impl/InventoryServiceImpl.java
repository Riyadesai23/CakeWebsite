package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.InventoryDto;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.StockUpdateRequest;
import com.example.ecommerce.entity.Inventory;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.OutOfStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryDto getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product id", "productId", productId));
        return mapToDto(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InventoryDto> searchInventory(String keyword, boolean lowStockOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<Inventory> inventoryPage = inventoryRepository.searchInventory(trimmedKeyword, lowStockOnly, pageable);

        List<InventoryDto> content = inventoryPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, inventoryPage.getNumber(), inventoryPage.getSize(),
                inventoryPage.getTotalElements(), inventoryPage.getTotalPages(), inventoryPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockProducts() {
        return inventoryRepository.findLowStockInventory().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryDto updateStock(Long productId, StockUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseGet(() -> new Inventory(product, 0, product.getMinimumStockLevel()));

        int currentQty = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0;
        String op = request.getOperation() != null ? request.getOperation().toUpperCase() : "ADD";

        if ("ADD".equals(op)) {
            inventory.setAvailableQuantity(currentQty + request.getQuantity());
            inventory.setLastRestockedAt(new Date());
        } else if ("SUBTRACT".equals(op)) {
            if (currentQty < request.getQuantity()) {
                throw new BadRequestException(
                        String.format("Cannot subtract %d units. Available stock is only %d units.",
                                request.getQuantity(), currentQty), "INSUFFICIENT_STOCK");
            }
            inventory.setAvailableQuantity(currentQty - request.getQuantity());
        } else if ("SET".equals(op)) {
            if (request.getQuantity() < 0) {
                throw new BadRequestException("Stock quantity cannot be negative.", "INVALID_QUANTITY");
            }
            inventory.setAvailableQuantity(request.getQuantity());
            inventory.setLastRestockedAt(new Date());
        } else {
            throw new BadRequestException("Invalid operation. Must be ADD, SUBTRACT, or SET.", "INVALID_OPERATION");
        }

        if (request.getMinimumStockLevel() != null) {
            inventory.setMinimumStockLevel(request.getMinimumStockLevel());
            product.setMinimumStockLevel(request.getMinimumStockLevel());
            productRepository.save(product);
        }

        inventory.setUpdatedAt(new Date());
        Inventory saved = inventoryRepository.save(inventory);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deductStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product id", "productId", productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new OutOfStockException(productId, inventory.getProduct().getName(), quantity, inventory.getAvailableQuantity());
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setUpdatedAt(new Date());
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product id", "productId", productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setUpdatedAt(new Date());
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkStockAvailability(Long productId, int requestedQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product id", "productId", productId));

        if (inventory.getAvailableQuantity() < requestedQuantity) {
            throw new OutOfStockException(productId, inventory.getProduct().getName(), requestedQuantity, inventory.getAvailableQuantity());
        }
    }

    private InventoryDto mapToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        if (inventory.getProduct() != null) {
            dto.setProductId(inventory.getProduct().getId());
            dto.setProductName(inventory.getProduct().getName());
            dto.setProductSku(inventory.getProduct().getSku());
            dto.setProductBrand(inventory.getProduct().getBrand());
            if (inventory.getProduct().getCategory() != null) {
                dto.setCategoryName(inventory.getProduct().getCategory().getName());
            }
        }
        dto.setAvailableQuantity(inventory.getAvailableQuantity());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setMinimumStockLevel(inventory.getMinimumStockLevel());
        dto.setLowStock(inventory.isLowStock());
        dto.setOutOfStock(inventory.isOutOfStock());
        dto.setLastRestockedAt(inventory.getLastRestockedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        return dto;
    }
}
