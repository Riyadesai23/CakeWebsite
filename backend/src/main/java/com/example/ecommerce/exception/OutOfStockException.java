package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutOfStockException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private Integer requestedQuantity;
    private Integer availableQuantity;

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(Long productId, String productName, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Product '%s' (ID: %d) has only %d item(s) in stock, but %d requested",
                productName, productId, availableQuantity, requestedQuantity));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}
