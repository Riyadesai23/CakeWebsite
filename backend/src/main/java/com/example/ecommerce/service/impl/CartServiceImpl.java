package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.dto.UpdateCartItemRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Inventory;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.OutOfStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public CartDto getCartForUser(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDto addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("This product is no longer available for purchase.", "PRODUCT_INACTIVE");
        }

        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "id", product.getId()));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        int newTotalQuantity = request.getQuantity();
        CartItem cartItem;

        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            newTotalQuantity = cartItem.getQuantity() + request.getQuantity();
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
        }

        if (inventory.getAvailableQuantity() < newTotalQuantity) {
            throw new OutOfStockException(product.getId(), product.getName(), newTotalQuantity, inventory.getAvailableQuantity());
        }

        cartItem.setQuantity(newTotalQuantity);
        cartItem.setUnitPrice(product.getFinalPrice());
        cartItem.calculateTotalPrice();

        if (!existingItemOpt.isPresent()) {
            cart.addItem(cartItem);
        }

        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to user's cart", "INVALID_CART_ITEM");
        }

        if (request.getQuantity() <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
            return mapToDto(cart);
        }

        Product product = item.getProduct();
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "id", product.getId()));

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new OutOfStockException(product.getId(), product.getName(), request.getQuantity(), inventory.getAvailableQuantity());
        }

        item.setQuantity(request.getQuantity());
        item.setUnitPrice(product.getFinalPrice());
        item.calculateTotalPrice();
        cartItemRepository.save(item);

        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to user's cart", "INVALID_CART_ITEM");
        }

        cart.removeItem(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clearItems();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    private CartDto mapToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemDto itemDto = new CartItemDto();
                itemDto.setId(item.getId());
                Product product = item.getProduct();
                if (product != null) {
                    itemDto.setProductId(product.getId());
                    itemDto.setProductName(product.getName());
                    itemDto.setProductSku(product.getSku());
                    itemDto.setProductImage(product.getImageUrl());
                    itemDto.setProductBrand(product.getBrand());
                    itemDto.setUnit(product.getUnit());
                    itemDto.setUnitPrice(product.getFinalPrice());

                    Optional<Inventory> inv = inventoryRepository.findByProductId(product.getId());
                    if (inv.isPresent()) {
                        itemDto.setAvailableStock(inv.get().getAvailableQuantity());
                        itemDto.setInStock(inv.get().getAvailableQuantity() >= item.getQuantity());
                    } else {
                        itemDto.setAvailableStock(0);
                        itemDto.setInStock(false);
                    }
                }

                itemDto.setQuantity(item.getQuantity());
                BigDecimal lineTotal = itemDto.getUnitPrice() != null ?
                        itemDto.getUnitPrice().multiply(new BigDecimal(item.getQuantity())) : BigDecimal.ZERO;
                itemDto.setTotalPrice(lineTotal);

                itemDtos.add(itemDto);
                totalItems += item.getQuantity();

                // Compute prices
                if (product != null && product.getPrice() != null) {
                    subtotal = subtotal.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
                finalAmount = finalAmount.add(lineTotal);
            }
        }

        dto.setItems(itemDtos);
        dto.setTotalItems(totalItems);
        dto.setSubtotal(subtotal);
        dto.setFinalAmount(finalAmount);
        dto.setDiscountAmount(subtotal.subtract(finalAmount).max(BigDecimal.ZERO));

        return dto;
    }
}
