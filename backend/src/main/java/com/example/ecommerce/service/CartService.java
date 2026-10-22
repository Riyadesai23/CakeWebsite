package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.UpdateCartItemRequest;

public interface CartService {
    CartDto getCartForUser(Long userId);
    CartDto addToCart(Long userId, AddToCartRequest request);
    CartDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartDto removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
