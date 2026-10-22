package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.DashboardStatsDto;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderStatusUpdateRequest;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.AdminService;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<OrderDto> orders = orderService.getAdminOrders(keyword, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderDetails(@PathVariable Long id) {
        OrderDto order = orderService.getAdminOrderDetails(id);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String updatedBy = currentUser != null ? currentUser.getUsername() : "admin";
        OrderDto order = orderService.updateOrderStatus(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", order));
    }
}
