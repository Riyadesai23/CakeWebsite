package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderStatusUpdateRequest;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.entity.OrderStatus;

public interface OrderService {
    OrderDto placeOrder(Long userId, CheckoutRequest request);
    PagedResponse<OrderDto> getUserOrders(Long userId, int page, int size);
    OrderDto getUserOrderDetails(Long userId, Long orderId);
    PagedResponse<OrderDto> getAdminOrders(String keyword, OrderStatus status, int page, int size, String sortBy, String sortDir);
    OrderDto getAdminOrderDetails(Long orderId);
    OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateRequest request, String updatedBy);
}
