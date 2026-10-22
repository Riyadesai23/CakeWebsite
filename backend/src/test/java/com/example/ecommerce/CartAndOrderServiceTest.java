package com.example.ecommerce;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.exception.OutOfStockException;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.InventoryService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CartAndOrderServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Test
    @Transactional
    public void testAddToCartAndOrderPlacementWithInventoryDeduction() {
        // 1. Create a test user
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("shopper1");
        reg.setEmail("shopper1@example.com");
        reg.setPassword("pass12345");
        reg.setFirstName("Shopper");
        UserDto user = authService.register(reg);

        // 2. Fetch a product with stock
        PagedResponse<ProductDto> products = productService.getProductsPublic(null, null, null, null, null, 0, 1, "id", "asc");
        assertFalse(products.getContent().isEmpty());
        ProductDto product = products.getContent().get(0);
        int initialStock = product.getAvailableQuantity();
        assertTrue(initialStock >= 2, "Need at least 2 units of stock for testing");

        // 3. Add 2 units to cart
        AddToCartRequest addReq = new AddToCartRequest(product.getId(), 2);
        CartDto cart = cartService.addToCart(user.getId(), addReq);
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());

        // 4. Place order
        CheckoutRequest checkoutReq = new CheckoutRequest();
        checkoutReq.setRecipientName("Shopper One");
        checkoutReq.setContactPhone("9876543210");
        checkoutReq.setShippingAddress("123 Market Street, City");
        checkoutReq.setPaymentMethod(PaymentMethod.COD);

        OrderDto order = orderService.placeOrder(user.getId(), checkoutReq);

        assertNotNull(order);
        assertNotNull(order.getOrderNumber());
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());
        assertEquals(1, order.getItems().size());

        // 5. Verify inventory decreased by 2
        InventoryDto updatedInventory = inventoryService.getInventoryByProductId(product.getId());
        assertEquals(initialStock - 2, updatedInventory.getAvailableQuantity());

        // 6. Verify user's cart is now empty
        CartDto emptyCart = cartService.getCartForUser(user.getId());
        assertTrue(emptyCart.getItems().isEmpty());
    }

    @Test
    @Transactional
    public void testOrderingMoreThanAvailableStockThrowsException() {
        // 1. Create a test user
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("stock_tester");
        reg.setEmail("stock_tester@example.com");
        reg.setPassword("pass12345");
        reg.setFirstName("Stock");
        UserDto user = authService.register(reg);

        // 2. Get a product
        PagedResponse<ProductDto> products = productService.getProductsPublic(null, null, null, null, null, 0, 1, "id", "asc");
        ProductDto product = products.getContent().get(0);
        int availableStock = product.getAvailableQuantity();

        // 3. Attempt to add more than available stock
        AddToCartRequest addReq = new AddToCartRequest(product.getId(), availableStock + 100);

        assertThrows(OutOfStockException.class, () -> {
            cartService.addToCart(user.getId(), addReq);
        });
    }

    @Test
    @Transactional
    public void testOrderCancellationRestoresInventory() {
        // 1. Create a test user
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("cancel_tester");
        reg.setEmail("cancel_tester@example.com");
        reg.setPassword("pass12345");
        reg.setFirstName("Cancel");
        UserDto user = authService.register(reg);

        // 2. Get product and current stock
        PagedResponse<ProductDto> products = productService.getProductsPublic(null, null, null, null, null, 0, 1, "id", "asc");
        ProductDto product = products.getContent().get(0);
        int originalStock = product.getAvailableQuantity();

        // 3. Add 1 item and place order
        cartService.addToCart(user.getId(), new AddToCartRequest(product.getId(), 1));

        CheckoutRequest checkoutReq = new CheckoutRequest();
        checkoutReq.setRecipientName("Cancel Tester");
        checkoutReq.setContactPhone("9876543210");
        checkoutReq.setShippingAddress("456 Cancel Ave");
        checkoutReq.setPaymentMethod(PaymentMethod.COD);

        OrderDto order = orderService.placeOrder(user.getId(), checkoutReq);

        // Inventory should be decremented
        InventoryDto invAfterOrder = inventoryService.getInventoryByProductId(product.getId());
        assertEquals(originalStock - 1, invAfterOrder.getAvailableQuantity());

        // 4. Cancel the order
        OrderStatusUpdateRequest cancelReq = new OrderStatusUpdateRequest(OrderStatus.CANCELLED, "Customer requested cancellation");
        orderService.updateOrderStatus(order.getId(), cancelReq, "admin");

        // 5. Inventory should be restored to original stock
        InventoryDto invAfterCancel = inventoryService.getInventoryByProductId(product.getId());
        assertEquals(originalStock, invAfterCancel.getAvailableQuantity());
    }
}
