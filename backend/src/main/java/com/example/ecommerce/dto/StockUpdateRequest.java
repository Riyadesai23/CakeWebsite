package com.example.ecommerce.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StockUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minimumStockLevel;

    /**
     * Operation type: "ADD" (increase), "SUBTRACT" (decrease), "SET" (set absolute value)
     */
    @NotBlank(message = "Operation type is required (ADD, SUBTRACT, SET)")
    private String operation = "ADD";

    private String reason;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(Integer quantity, String operation) {
        this.quantity = quantity;
        this.operation = operation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(Integer minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
