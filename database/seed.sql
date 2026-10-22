-- ===================================================================
-- Bake My Day Seed & Sample Data SQL Script
-- ===================================================================

USE `ecommerce_db`;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `order_status_history`;
TRUNCATE TABLE `order_items`;
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `cart_items`;
TRUNCATE TABLE `carts`;
TRUNCATE TABLE `inventory`;
TRUNCATE TABLE `products`;
TRUNCATE TABLE `categories`;
TRUNCATE TABLE `addresses`;
TRUNCATE TABLE `user_roles`;
TRUNCATE TABLE `roles`;
TRUNCATE TABLE `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insert Roles
INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

-- 2. Insert Users (BCrypt Encrypted Passwords)
-- 'admin123'    -> $2a$10$d8oZqW1Wk7n7LgTfIqKze.u.W6eU8gP8n9I9gR7s4z8y1t6u5v4w2 (example BCrypt hash)
-- 'password123' -> $2a$10$wN9iL6b6D2t0L9P7Q5Z3EuB1H7K8M9O0P1Q2R3S4T5U6V7W8X9Y0Z (example BCrypt hash)
-- Note: Spring Boot DataInitializer also initializes fresh BCrypt hashes on startup automatically.

INSERT INTO `users` (`id`, `username`, `email`, `password`, `first_name`, `last_name`, `phone`, `active`, `created_at`) VALUES
(1, 'admin', 'admin@ecommerce.com', '$2a$10$rDkPvvafV8H6xc51hZ.v2u0cO9g1z2b3c4d5e6f7g8h9i0j1k2l3m', 'System', 'Administrator', '+91 9876543210', 1, NOW()),
(2, 'john_doe', 'john@example.com', '$2a$10$rDkPvvafV8H6xc51hZ.v2u0cO9g1z2b3c4d5e6f7g8h9i0j1k2l3m', 'John', 'Doe', '+91 9876543211', 1, NOW()),
(3, 'sarah_smith', 'sarah@example.com', '$2a$10$rDkPvvafV8H6xc51hZ.v2u0cO9g1z2b3c4d5e6f7g8h9i0j1k2l3m', 'Sarah', 'Smith', '+91 9876543212', 1, NOW()),
(4, 'mike_ross', 'mike@example.com', '$2a$10$rDkPvvafV8H6xc51hZ.v2u0cO9g1z2b3c4d5e6f7g8h9i0j1k2l3m', 'Mike', 'Ross', '+91 9876543213', 1, NOW());

-- 3. Assign User Roles
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 2), -- Admin has both ROLE_ADMIN and ROLE_USER
(2, 1),
(3, 1),
(4, 1);

-- 4. Insert Default Addresses
INSERT INTO `addresses` (`id`, `user_id`, `recipient_name`, `phone`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `is_default`) VALUES
(1, 2, 'John Doe', '+91 9876543211', '42 Tech Park Avenue, Flat 302', 'Koramangala 5th Block', 'Bengaluru', 'Karnataka', '560034', 'India', 1),
(2, 3, 'Sarah Smith', '+91 9876543212', '15 Green Ridge Enclave', 'Bandra West', 'Mumbai', 'Maharashtra', '400050', 'India', 1);

-- 5. Insert Categories
INSERT INTO `categories` (`id`, `name`, `code`, `description`, `image_url`, `active`, `created_at`) VALUES
(1, 'Cakes', 'CAT-CAKE', 'Handcrafted celebration cakes, velvety cheesecakes, truffles & multi-layer specialty cakes', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&auto=format&fit=crop&q=60', 1, NOW()),
(2, 'Pastries', 'CAT-PAST', 'Artisanal single-serve French pastries, fruit tarts, eclairs & velvety mousse slices', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60', 1, NOW()),
(3, 'Custom Cakes', 'CAT-CUST', 'Bespoke designer cakes, multi-tier wedding centerpieces, fondant art & photo cakes', 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=500&auto=format&fit=crop&q=60', 1, NOW()),
(4, 'Candles & Decor', 'CAT-CAND', 'Metallic sparklers, numeral gold candles, musical rotating flower candles & festive cake toppers', 'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=500&auto=format&fit=crop&q=60', 1, NOW());

-- 6. Insert Products
INSERT INTO `products` (`id`, `name`, `sku`, `description`, `category_id`, `price`, `discount_percentage`, `final_price`, `minimum_stock_level`, `image_url`, `brand`, `unit`, `active`, `created_at`, `created_by`, `updated_by`) VALUES
-- Cakes
(1, 'Belgian Dark Chocolate Truffle Cake (1kg)', 'SKU-CAKE-001', 'Decadent Dutch cocoa sponge layered with 70% pure Belgian dark chocolate ganache and hand-rolled truffles. 100% Eggless.', 1, 1199.00, 10.00, 1079.10, 5, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&auto=format&fit=crop&q=60', 'Bake My Day Signature', 'Cake', 1, NOW(), 'admin', 'admin'),
(2, 'Royal Red Velvet Cream Cheese Cake (1kg)', 'SKU-CAKE-002', 'Classic crimson velvet crumb sponge filled and frosted with silky Madagascar vanilla cream cheese frosting.', 1, 1299.00, 15.00, 1104.15, 5, 'https://images.unsplash.com/photo-1586985289688-ca3cf47d3e6e?w=500&auto=format&fit=crop&q=60', 'Bake My Day Signature', 'Cake', 1, NOW(), 'admin', 'admin'),
(3, 'New York Baked Blueberry Cheesecake (1kg)', 'SKU-CAKE-003', 'Creamy dense slow-baked Philadelphia style cream cheese on buttery Graham cracker crust topped with wild blueberry compote.', 1, 1499.00, 5.00, 1424.05, 4, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=500&auto=format&fit=crop&q=60', 'Gourmet Patisserie', 'Cake', 1, NOW(), 'admin', 'admin'),
(4, 'Classic German Black Forest Cake (1kg)', 'SKU-CAKE-004', 'Layers of moist chocolate sponge soaked with cherry syrup, whipped dairy cream, dark chocolate shavings and sour cherries.', 1, 999.00, 10.00, 899.10, 8, 'https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=500&auto=format&fit=crop&q=60', 'Heritage Bakery', 'Cake', 1, NOW(), 'admin', 'admin'),
(5, 'Fresh Alphonso Mango Mousse Cake (1kg)', 'SKU-CAKE-005', 'Seasonal fresh Ratnagiri Alphonso mango puree whipped into light cloud mousse with vanilla sponge layers.', 1, 1399.00, 0.00, 1399.00, 3, 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=500&auto=format&fit=crop&q=60', 'Seasonal Delights', 'Cake', 1, NOW(), 'admin', 'admin'),
(6, 'Classic Italian Tiramisu Cake (1kg)', 'SKU-CAKE-006', 'Espresso-soaked savoiardi ladyfingers layered with whipped mascarpone zabaione and dusted with Valrhona cocoa.', 1, 1350.00, 12.00, 1188.00, 5, 'https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=500&auto=format&fit=crop&q=60', 'Gourmet Patisserie', 'Cake', 1, NOW(), 'admin', 'admin'),

-- Pastries
(7, 'French Chocolate Glaze Eclair (Box of 4)', 'SKU-PAST-001', 'Crisp choux pastry shells filled with vanilla bean diplomat cream and glazed with shiny dark chocolate fondant.', 2, 449.00, 10.00, 404.10, 10, 'https://images.unsplash.com/photo-1612203985729-70726954388c?w=500&auto=format&fit=crop&q=60', 'French Patisserie', 'Box', 1, NOW(), 'admin', 'admin'),
(8, 'Wild Berry Fruit Tart Slices (Pack of 2)', 'SKU-PAST-002', 'All-butter shortcrust pastry shells filled with silky almond frangipane cream and topped with glazed fresh berries.', 2, 379.00, 0.00, 379.00, 5, 'https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=500&auto=format&fit=crop&q=60', 'Artisan Pastry', 'Pack', 1, NOW(), 'admin', 'admin'),
(9, 'Raspberry Chocolate Opera Slice', 'SKU-PAST-003', 'Traditional French Opera cake slice featuring almond Joconde sponge, coffee buttercream, and rich dark ganache.', 2, 229.00, 5.00, 217.55, 8, 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60', 'French Patisserie', 'Piece', 1, NOW(), 'admin', 'admin'),
(10, 'Hazelnut Praline Cruffin (Pack of 2)', 'SKU-PAST-004', 'Flaky croissant pastry baked in muffin mould, filled with Piedmont hazelnut praline and dusted with cinnamon sugar.', 2, 329.00, 10.00, 296.10, 5, 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500&auto=format&fit=crop&q=60', 'Artisan Pastry', 'Pack', 1, NOW(), 'admin', 'admin'),
(11, 'Salted Caramel Brownie Slice', 'SKU-PAST-005', 'Fudgy triple chocolate brownie topped with gooey fleur de sel caramel and roasted walnut chunks.', 2, 189.00, 15.00, 160.65, 10, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop&q=60', 'Bake My Day Signature', 'Piece', 1, NOW(), 'admin', 'admin'),

-- Custom Cakes
(12, 'Grand Botanical 3-Tier Wedding Cake', 'SKU-CUST-001', 'Three-tier bespoke celebration cake decorated with edible sugar lace, handcrafted wafer paper peonies, and 24K gold foil.', 3, 6499.00, 10.00, 5849.10, 2, 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=500&auto=format&fit=crop&q=60', 'Bake My Day Couture', 'Cake', 1, NOW(), 'admin', 'admin'),
(13, 'Personalised Edible Photo Cake (1.5kg)', 'SKU-CUST-002', 'Customizable high-resolution edible sugar sheet print cake with your chosen photo and custom birthday greeting. Choice of vanilla/chocolate base.', 3, 1799.00, 10.00, 1619.10, 4, 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500&auto=format&fit=crop&q=60', 'Bake My Day Couture', 'Cake', 1, NOW(), 'admin', 'admin'),
(14, 'Enchanted Forest Theme Designer Cake (2kg)', 'SKU-CUST-003', 'Artistic fondant sculpted fantasy forest theme cake with edible sugar mushrooms, golden butterflies, and chocolate bark.', 3, 2899.00, 8.00, 2667.08, 2, 'https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=500&auto=format&fit=crop&q=60', 'Bake My Day Couture', 'Cake', 1, NOW(), 'admin', 'admin'),
(15, 'Pastel Drip & Macaron Celebration Cake (1.5kg)', 'SKU-CUST-004', 'Luscious white chocolate watercolor drip cake crowned with French almond macarons, meringues, and fresh flowers.', 3, 2199.00, 12.00, 1935.12, 3, 'https://images.unsplash.com/photo-1562440499-64c9a111f713?w=500&auto=format&fit=crop&q=60', 'Bake My Day Couture', 'Cake', 1, NOW(), 'admin', 'admin'),
(16, 'Number / Monogram Floral Cake (1kg)', 'SKU-CUST-005', 'Custom biscuit number or alphabet cake filled with cream cheese diplomat, fresh strawberries, chocolate bonbons and macarons.', 3, 1999.00, 5.00, 1899.05, 3, 'https://images.unsplash.com/photo-1535254973040-607b474cb50d?w=500&auto=format&fit=crop&q=60', 'Bake My Day Couture', 'Cake', 1, NOW(), 'admin', 'admin'),

-- Candles & Decor
(17, 'Champagne Gold Metallic Number Candle', 'SKU-CAND-001', 'Premium smokeless dripless metallic gold numbered birthday candle with sturdy stick (0 to 9 available).', 4, 149.00, 0.00, 149.00, 20, 'https://images.unsplash.com/photo-1514517521153-1be72277b32f?w=500&auto=format&fit=crop&q=60', 'PartyGlow', 'Piece', 1, NOW(), 'admin', 'admin'),
(18, 'Musical Blooming Lotus Flower Candle', 'SKU-CAND-002', 'Magical rotating birthday candle that blossoms petals, plays the Happy Birthday melody, and sparkles when lit.', 4, 299.00, 20.00, 239.20, 10, 'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=500&auto=format&fit=crop&q=60', 'PartyGlow', 'Piece', 1, NOW(), 'admin', 'admin'),
(19, 'Rose Gold Acrylic \'Happy Birthday\' Topper', 'SKU-CAND-003', 'Mirror finish reusable laser-cut acrylic cake topper that adds elegance to any birthday cake centerpiece.', 4, 199.00, 10.00, 179.10, 15, 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&auto=format&fit=crop&q=60', 'CelebrationSpark', 'Piece', 1, NOW(), 'admin', 'admin'),
(20, 'Smokeless Magic Sparkler Cake Candles (Pack of 6)', 'SKU-CAND-004', 'Food-safe indoor celebratory dessert sparklers producing golden sparkling fireworks for 45 seconds.', 4, 249.00, 15.00, 211.65, 5, 'https://images.unsplash.com/photo-1513151233558-d860c5398176?w=500&auto=format&fit=crop&q=60', 'CelebrationSpark', 'Pack', 1, NOW(), 'admin', 'admin');

-- 7. Insert Inventory Records
INSERT INTO `inventory` (`id`, `product_id`, `available_quantity`, `reserved_quantity`, `minimum_stock_level`, `last_restocked_at`) VALUES
(1, 1, 25, 0, 5, NOW()),
(2, 2, 18, 0, 5, NOW()),
(3, 3, 12, 0, 4, NOW()),
(4, 4, 30, 0, 8, NOW()),
(5, 5, 2, 0, 3, NOW()), -- Low stock
(6, 6, 15, 0, 5, NOW()),
(7, 7, 40, 0, 10, NOW()),
(8, 8, 22, 0, 5, NOW()),
(9, 9, 35, 0, 8, NOW()),
(10, 10, 3, 0, 5, NOW()), -- Low stock
(11, 11, 50, 0, 10, NOW()),
(12, 12, 6, 0, 2, NOW()),
(13, 13, 14, 0, 4, NOW()),
(14, 14, 5, 0, 2, NOW()),
(15, 15, 2, 0, 3, NOW()), -- Low stock
(16, 16, 9, 0, 3, NOW()),
(17, 17, 100, 0, 20, NOW()),
(18, 18, 45, 0, 10, NOW()),
(19, 19, 60, 0, 15, NOW()),
(20, 20, 1, 0, 5, NOW()); -- Low stock

-- 8. Insert User Carts
INSERT INTO `carts` (`id`, `user_id`, `created_at`) VALUES
(1, 1, NOW()),
(2, 2, NOW()),
(3, 3, NOW()),
(4, 4, NOW());

-- 9. Insert Sample Orders
INSERT INTO `orders` (`id`, `order_number`, `user_id`, `recipient_name`, `contact_phone`, `shipping_address`, `billing_address`, `total_amount`, `discount_amount`, `final_amount`, `payment_method`, `payment_status`, `order_status`, `notes`, `created_at`) VALUES
(1, 'ORD-20260826-1001', 2, 'John Doe', '+91 9876543211', '42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034', '42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034', 1497.00, 119.90, 1377.10, 'CREDIT_CARD', 'PAID', 'DELIVERED', 'Please write Happy 30th Birthday John! on the cake box.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'ORD-20260826-1002', 2, 'John Doe', '+91 9876543211', '42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034', '42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034', 898.00, 89.80, 808.20, 'COD', 'PENDING', 'PROCESSING', 'Cake delivery needed by 6 PM sharp for celebration.', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 10. Insert Order Line Items
INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `product_name`, `product_sku`, `product_image`, `unit_price`, `quantity`, `total_price`) VALUES
(1, 1, 1, 'Belgian Dark Chocolate Truffle Cake (1kg)', 'SKU-CAKE-001', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500', 1079.10, 1, 1079.10),
(2, 1, 17, 'Champagne Gold Metallic Number Candle', 'SKU-CAND-001', 'https://images.unsplash.com/photo-1514517521153-1be72277b32f?w=500', 149.00, 2, 298.00),
(3, 2, 7, 'French Chocolate Glaze Eclair (Box of 4)', 'SKU-PAST-001', 'https://images.unsplash.com/photo-1612203985729-70726954388c?w=500', 404.10, 2, 808.20);

-- 11. Insert Order Status History
INSERT INTO `order_status_history` (`id`, `order_id`, `status`, `comments`, `changed_by`, `changed_at`) VALUES
(1, 1, 'CREATED', 'Order received', 'john_doe', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 1, 'CONFIRMED', 'Payment verified', 'system', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 1, 'SHIPPED', 'Dispatched in cold storage box', 'admin', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 1, 'DELIVERED', 'Freshly delivered to customer', 'admin', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 2, 'CREATED', 'Order received', 'john_doe', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 2, 'CONFIRMED', 'Bakery confirmed baking slot', 'admin', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 2, 'PROCESSING', 'Fresh pastries being packed', 'admin', NOW());
