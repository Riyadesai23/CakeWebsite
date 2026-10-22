package com.example.ecommerce.dto;

import com.example.ecommerce.entity.OrderStatus;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class OrderStatusUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    private String comments;

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(OrderStatus status, String comments) {
        this.status = status;
        this.comments = comments;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
