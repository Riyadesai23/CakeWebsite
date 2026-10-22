package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.DashboardStatsDto;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderItemDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByActive(true));

        stats.setTotalProducts(productRepository.count());
        stats.setActiveProducts(productRepository.countByActive(true));

        stats.setLowStockProducts(inventoryRepository.countLowStockInventory());

        stats.setTotalOrders(orderRepository.count());

        long pending = orderRepository.countByOrderStatus(OrderStatus.CREATED) +
                       orderRepository.countByOrderStatus(OrderStatus.CONFIRMED) +
                       orderRepository.countByOrderStatus(OrderStatus.PROCESSING) +
                       orderRepository.countByOrderStatus(OrderStatus.SHIPPED);
        stats.setPendingOrders(pending);

        stats.setCompletedOrders(orderRepository.countByOrderStatus(OrderStatus.DELIVERED));

        BigDecimal sales = orderRepository.calculateTotalSales();
        stats.setTotalSales(sales != null ? sales : BigDecimal.ZERO);

        List<Order> recent = orderRepository.findTop5ByOrderByCreatedAtDesc();
        List<OrderDto> recentDtos = recent.stream().map(this::mapToOrderDto).collect(Collectors.toList());
        stats.setRecentOrders(recentDtos);

        return stats;
    }

    private OrderDto mapToOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
            dto.setUserEmail(order.getUser().getEmail());
        }
        dto.setRecipientName(order.getRecipientName());
        dto.setContactPhone(order.getContactPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductName(item.getProductName());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setTotalPrice(item.getTotalPrice());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }
}
