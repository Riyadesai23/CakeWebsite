package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CheckoutRequest request) {

        OrderDto order = orderService.placeOrder(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.ok("Order placed successfully!", order), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<OrderDto> orders = orderService.getUserOrders(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderDetails(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {

        OrderDto order = orderService.getUserOrderDetails(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }
}
