package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.OutOfStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.util.OrderNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDto placeOrder(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("No shopping cart found for user", "CART_NOT_FOUND"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your shopping cart is empty!", "EMPTY_CART");
        }

        Order order = new Order();
        order.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order.setUser(user);
        order.setRecipientName(request.getRecipientName().trim());
        order.setContactPhone(request.getContactPhone().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setBillingAddress(request.getBillingAddress() != null ? request.getBillingAddress().trim() : request.getShippingAddress().trim());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.COD);
        order.setPaymentStatus(request.getPaymentMethod() == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive()) {
                throw new BadRequestException("Product '" + product.getName() + "' is no longer active", "PRODUCT_INACTIVE");
            }

            // Lock inventory for atomic deduction
            Inventory inventory = inventoryRepository.findByProductIdWithLock(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "id", product.getId()));

            if (inventory.getAvailableQuantity() < cartItem.getQuantity()) {
                throw new OutOfStockException(product.getId(), product.getName(), cartItem.getQuantity(), inventory.getAvailableQuantity());
            }

            // Deduct stock safely
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - cartItem.getQuantity());
            inventory.setUpdatedAt(new Date());
            inventoryRepository.save(inventory);

            // Create Order Item snapshot with current live product price
            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    product.getName(),
                    product.getSku(),
                    product.getImageUrl(),
                    product.getFinalPrice(),
                    cartItem.getQuantity()
            );
            orderItems.add(orderItem);

            BigDecimal originalItemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            BigDecimal finalItemTotal = product.getFinalPrice().multiply(new BigDecimal(cartItem.getQuantity()));

            totalAmount = totalAmount.add(originalItemTotal);
            finalAmount = finalAmount.add(finalItemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(finalAmount);
        order.setDiscountAmount(totalAmount.subtract(finalAmount).max(BigDecimal.ZERO));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Record Initial Status History
        OrderStatusHistory history = new OrderStatusHistory(
                savedOrder, OrderStatus.CREATED, "Order created successfully", user.getUsername());
        statusHistoryRepository.save(history);

        // Clear user's cart
        cart.clearItems();
        cartRepository.save(cart);

        return mapToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<OrderDto> content = orders.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, orders.getNumber(), orders.getSize(),
                orders.getTotalElements(), orders.getTotalPages(), orders.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getUserOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getAdminOrders(String keyword, OrderStatus status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<Order> orders = orderRepository.searchOrdersAdmin(trimmedKeyword, status, pageable);

        List<OrderDto> content = orders.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, orders.getNumber(), orders.getSize(),
                orders.getTotalElements(), orders.getTotalPages(), orders.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getAdminOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToDto(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateRequest request, String updatedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getStatus();

        // If order is being cancelled, restore the inventory stock
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED && oldStatus != OrderStatus.DELIVERED) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    Inventory inv = inventoryRepository.findByProductIdWithLock(item.getProduct().getId())
                            .orElse(null);
                    if (inv != null) {
                        inv.setAvailableQuantity(inv.getAvailableQuantity() + item.getQuantity());
                        inv.setUpdatedAt(new Date());
                        inventoryRepository.save(inv);
                    }
                }
            }
        }

        // If marked as DELIVERED, automatically mark payment as PAID
        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Record history log
        String comments = request.getComments() != null ? request.getComments() : "Status changed from " + oldStatus + " to " + newStatus;
        OrderStatusHistory history = new OrderStatusHistory(updatedOrder, newStatus, comments, updatedBy);
        statusHistoryRepository.save(history);

        return mapToDto(updatedOrder);
    }

    private OrderDto mapToDto(Order order) {
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
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getItems() != null) {
            List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                if (item.getProduct() != null) {
                    itemDto.setProductId(item.getProduct().getId());
                }
                itemDto.setProductName(item.getProductName());
                itemDto.setProductSku(item.getProductSku());
                itemDto.setProductImage(item.getProductImage());
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
