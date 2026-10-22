package com.example.ecommerce.config;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.util.OrderNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            logger.info("Initializing roles...");
            initRoles();
        }

        if (userRepository.count() == 0) {
            logger.info("Initializing default users...");
            initUsers();
        }

        // Detect if old non-bakery categories exist (e.g. Electronics, Grocery, Clothing, Books)
        // or if new bakery categories (Cakes, Pastries) are not present
        boolean hasOldCategories = categoryRepository.findByCode("CAT-ELEC").isPresent()
                || categoryRepository.findByCode("CAT-GROC").isPresent()
                || categoryRepository.findByCode("CAT-CLOTH").isPresent()
                || categoryRepository.findByCode("CAT-BOOK").isPresent()
                || categoryRepository.findByCode("CAT-HOME").isPresent()
                || categoryRepository.findByCode("CAT-ACC").isPresent();
        
        boolean hasBakeryCategories = categoryRepository.findByCode("CAT-CAKE").isPresent()
                && categoryRepository.findByCode("CAT-PAST").isPresent()
                && categoryRepository.findByCode("CAT-CUST").isPresent()
                && categoryRepository.findByCode("CAT-CAND").isPresent();

        if (hasOldCategories || !hasBakeryCategories || categoryRepository.count() != 4 || productRepository.count() == 0) {
            logger.info("Purging old catalog and initializing Bake My Day bakery catalog (Cakes, Pastries, Custom Cakes, Candles)...");

            // Clean up existing orders, cart items, inventory and products to ensure clean slate
            statusHistoryRepository.deleteAll();
            orderRepository.deleteAll();

            for (Cart cart : cartRepository.findAll()) {
                cart.getItems().clear();
                cartRepository.save(cart);
            }

            inventoryRepository.deleteAll();
            productRepository.deleteAll();
            categoryRepository.deleteAll();

            initCategoriesAndProducts();
            initSampleOrders();
            logger.info("Bake My Day bakery catalog (Cakes, Pastries, Custom Cakes, Candles) initialized successfully!");
        }
    }

    private void initRoles() {
        roleRepository.save(new Role(RoleName.ROLE_USER));
        roleRepository.save(new Role(RoleName.ROLE_ADMIN));
    }

    private void initUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(RuntimeException::new);
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(RuntimeException::new);

        // 1. Admin User
        User admin = new User("admin", "admin@ecommerce.com", passwordEncoder.encode("admin123"), "System", "Administrator", "+91 9876543210");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        admin.setRoles(adminRoles);
        userRepository.save(admin);
        cartRepository.save(new Cart(admin));

        // 2. User 1: John Doe
        User john = new User("john_doe", "john@example.com", passwordEncoder.encode("password123"), "John", "Doe", "+91 9876543211");
        john.setRoles(Collections.singleton(userRole));
        userRepository.save(john);
        cartRepository.save(new Cart(john));

        Address johnAddress = new Address();
        johnAddress.setUser(john);
        johnAddress.setRecipientName("John Doe");
        johnAddress.setPhone("+91 9876543211");
        johnAddress.setAddressLine1("42 Tech Park Avenue, Flat 302");
        johnAddress.setAddressLine2("Koramangala 5th Block");
        johnAddress.setCity("Bengaluru");
        johnAddress.setState("Karnataka");
        johnAddress.setPostalCode("560034");
        johnAddress.setCountry("India");
        johnAddress.setDefault(true);
        addressRepository.save(johnAddress);

        // 3. User 2: Sarah Smith
        User sarah = new User("sarah_smith", "sarah@example.com", passwordEncoder.encode("password123"), "Sarah", "Smith", "+91 9876543212");
        sarah.setRoles(Collections.singleton(userRole));
        userRepository.save(sarah);
        cartRepository.save(new Cart(sarah));

        // 4. User 3: Mike Ross
        User mike = new User("mike_ross", "mike@example.com", passwordEncoder.encode("password123"), "Mike", "Ross", "+91 9876543213");
        mike.setRoles(Collections.singleton(userRole));
        userRepository.save(mike);
        cartRepository.save(new Cart(mike));
    }

    private void initCategoriesAndProducts() {
        // 1. Cakes
        Category cakes = categoryRepository.save(new Category(
                "Cakes", "CAT-CAKE", "Handcrafted celebration cakes, velvety cheesecakes, truffles & multi-layer specialty cakes",
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&auto=format&fit=crop&q=60"
        ));

        // 2. Pastries
        Category pastries = categoryRepository.save(new Category(
                "Pastries", "CAT-PAST", "Artisanal single-serve French pastries, fruit tarts, eclairs & velvety mousse slices",
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60"
        ));

        // 3. Custom Cakes
        Category customCakes = categoryRepository.save(new Category(
                "Custom Cakes", "CAT-CUST", "Bespoke designer cakes, multi-tier wedding centerpieces, fondant art & photo cakes",
                "https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=500&auto=format&fit=crop&q=60"
        ));

        // 4. Candles & Decor
        Category candles = categoryRepository.save(new Category(
                "Candles & Decor", "CAT-CAND", "Metallic sparklers, numeral gold candles, musical rotating flower candles & festive cake toppers",
                "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=500&auto=format&fit=crop&q=60"
        ));

        // Products: Cakes
        createProductWithStock("Belgian Dark Chocolate Truffle Cake (1kg)", "SKU-CAKE-001",
                "Decadent Dutch cocoa sponge layered with 70% pure Belgian dark chocolate ganache and hand-rolled truffles. 100% Eggless.",
                cakes, new BigDecimal("1199.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Signature", "Cake", 5, 25);

        createProductWithStock("Royal Red Velvet Cream Cheese Cake (1kg)", "SKU-CAKE-002",
                "Classic crimson velvet crumb sponge filled and frosted with silky Madagascar vanilla cream cheese frosting.",
                cakes, new BigDecimal("1299.00"), new BigDecimal("15.00"),
                "https://images.unsplash.com/photo-1586985289688-ca3cf47d3e6e?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Signature", "Cake", 5, 18);

        createProductWithStock("New York Baked Blueberry Cheesecake (1kg)", "SKU-CAKE-003",
                "Creamy dense slow-baked Philadelphia style cream cheese on buttery Graham cracker crust topped with wild blueberry compote.",
                cakes, new BigDecimal("1499.00"), new BigDecimal("5.00"),
                "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=500&auto=format&fit=crop&q=60",
                "Gourmet Patisserie", "Cake", 4, 12);

        createProductWithStock("Classic German Black Forest Cake (1kg)", "SKU-CAKE-004",
                "Layers of moist chocolate sponge soaked with cherry syrup, whipped dairy cream, dark chocolate shavings and sour cherries.",
                cakes, new BigDecimal("999.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=500&auto=format&fit=crop&q=60",
                "Heritage Bakery", "Cake", 8, 30);

        createProductWithStock("Fresh Alphonso Mango Mousse Cake (1kg)", "SKU-CAKE-005",
                "Seasonal fresh Ratnagiri Alphonso mango puree whipped into light cloud mousse with vanilla sponge layers.",
                cakes, new BigDecimal("1399.00"), new BigDecimal("0.00"),
                "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=500&auto=format&fit=crop&q=60",
                "Seasonal Delights", "Cake", 3, 2); // Low stock

        createProductWithStock("Classic Italian Tiramisu Cake (1kg)", "SKU-CAKE-006",
                "Espresso-soaked savoiardi ladyfingers layered with whipped mascarpone zabaione and dusted with Valrhona cocoa.",
                cakes, new BigDecimal("1350.00"), new BigDecimal("12.00"),
                "https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=500&auto=format&fit=crop&q=60",
                "Gourmet Patisserie", "Cake", 5, 15);

        // Products: Pastries
        createProductWithStock("French Chocolate Glaze Eclair (Box of 4)", "SKU-PAST-001",
                "Crisp choux pastry shells filled with vanilla bean diplomat cream and glazed with shiny dark chocolate fondant.",
                pastries, new BigDecimal("449.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1612203985729-70726954388c?w=500&auto=format&fit=crop&q=60",
                "French Patisserie", "Box", 10, 40);

        createProductWithStock("Wild Berry Fruit Tart Slices (Pack of 2)", "SKU-PAST-002",
                "All-butter shortcrust pastry shells filled with silky almond frangipane cream and topped with glazed fresh berries.",
                pastries, new BigDecimal("379.00"), new BigDecimal("0.00"),
                "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=500&auto=format&fit=crop&q=60",
                "Artisan Pastry", "Pack", 5, 22);

        createProductWithStock("Raspberry Chocolate Opera Slice", "SKU-PAST-003",
                "Traditional French Opera cake slice featuring almond Joconde sponge, coffee buttercream, and rich dark ganache.",
                pastries, new BigDecimal("229.00"), new BigDecimal("5.00"),
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60",
                "French Patisserie", "Piece", 8, 35);

        createProductWithStock("Hazelnut Praline Cruffin (Pack of 2)", "SKU-PAST-004",
                "Flaky croissant pastry baked in muffin mould, filled with Piedmont hazelnut praline and dusted with cinnamon sugar.",
                pastries, new BigDecimal("329.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500&auto=format&fit=crop&q=60",
                "Artisan Pastry", "Pack", 5, 3); // Low stock

        createProductWithStock("Salted Caramel Brownie Slice", "SKU-PAST-005",
                "Fudgy triple chocolate brownie topped with gooey fleur de sel caramel and roasted walnut chunks.",
                pastries, new BigDecimal("189.00"), new BigDecimal("15.00"),
                "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Signature", "Piece", 10, 50);

        // Products: Custom Cakes
        createProductWithStock("Grand Botanical 3-Tier Wedding Cake", "SKU-CUST-001",
                "Three-tier bespoke celebration cake decorated with edible sugar lace, handcrafted wafer paper peonies, and 24K gold foil.",
                customCakes, new BigDecimal("6499.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Couture", "Cake", 2, 6);

        createProductWithStock("Personalised Edible Photo Cake (1.5kg)", "SKU-CUST-002",
                "Customizable high-resolution edible sugar sheet print cake with your chosen photo and custom birthday greeting. Choice of vanilla/chocolate base.",
                customCakes, new BigDecimal("1799.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Couture", "Cake", 4, 14);

        createProductWithStock("Enchanted Forest Theme Designer Cake (2kg)", "SKU-CUST-003",
                "Artistic fondant sculpted fantasy forest theme cake with edible sugar mushrooms, golden butterflies, and chocolate bark.",
                customCakes, new BigDecimal("2899.00"), new BigDecimal("8.00"),
                "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Couture", "Cake", 2, 5);

        createProductWithStock("Pastel Drip & Macaron Celebration Cake (1.5kg)", "SKU-CUST-004",
                "Luscious white chocolate watercolor drip cake crowned with French almond macarons, meringues, and fresh flowers.",
                customCakes, new BigDecimal("2199.00"), new BigDecimal("12.00"),
                "https://images.unsplash.com/photo-1562440499-64c9a111f713?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Couture", "Cake", 3, 2); // Low stock

        createProductWithStock("Number / Monogram Floral Cake (1kg)", "SKU-CUST-005",
                "Custom biscuit number or alphabet cake filled with cream cheese diplomat, fresh strawberries, chocolate bonbons and macarons.",
                customCakes, new BigDecimal("1999.00"), new BigDecimal("5.00"),
                "https://images.unsplash.com/photo-1535254973040-607b474cb50d?w=500&auto=format&fit=crop&q=60",
                "Bake My Day Couture", "Cake", 3, 9);

        // Products: Candles & Decor
        createProductWithStock("Champagne Gold Metallic Number Candle", "SKU-CAND-001",
                "Premium smokeless dripless metallic gold numbered birthday candle with sturdy stick (0 to 9 available).",
                candles, new BigDecimal("149.00"), new BigDecimal("0.00"),
                "https://images.unsplash.com/photo-1514517521153-1be72277b32f?w=500&auto=format&fit=crop&q=60",
                "PartyGlow", "Piece", 20, 100);

        createProductWithStock("Musical Blooming Lotus Flower Candle", "SKU-CAND-002",
                "Magical rotating birthday candle that blossoms petals, plays the Happy Birthday melody, and sparkles when lit.",
                candles, new BigDecimal("299.00"), new BigDecimal("20.00"),
                "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=500&auto=format&fit=crop&q=60",
                "PartyGlow", "Piece", 10, 45);

        createProductWithStock("Rose Gold Acrylic 'Happy Birthday' Topper", "SKU-CAND-003",
                "Mirror finish reusable laser-cut acrylic cake topper that adds elegance to any birthday cake centerpiece.",
                candles, new BigDecimal("199.00"), new BigDecimal("10.00"),
                "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&auto=format&fit=crop&q=60",
                "CelebrationSpark", "Piece", 15, 60);

        createProductWithStock("Smokeless Magic Sparkler Cake Candles (Pack of 6)", "SKU-CAND-004",
                "Food-safe indoor celebratory dessert sparklers producing golden sparkling fireworks for 45 seconds.",
                candles, new BigDecimal("249.00"), new BigDecimal("15.00"),
                "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=500&auto=format&fit=crop&q=60",
                "CelebrationSpark", "Pack", 5, 1); // Low stock
    }

    private void createProductWithStock(String name, String sku, String description, Category category,
                                       BigDecimal price, BigDecimal discount, String image,
                                       String brand, String unit, int minStock, int initialStock) {
        Product p = new Product(name, sku, description, category, price, discount, image, brand, unit, minStock);
        p.setCreatedBy("system");
        p.setUpdatedBy("system");
        Product saved = productRepository.save(p);

        Inventory inv = new Inventory(saved, initialStock, minStock);
        inventoryRepository.save(inv);
    }

    private void initSampleOrders() {
        User john = userRepository.findByUsername("john_doe").orElse(null);
        if (john == null) return;

        Product truffleCake = productRepository.findBySku("SKU-CAKE-001").orElse(null);
        Product eclairs = productRepository.findBySku("SKU-PAST-001").orElse(null);
        Product candles = productRepository.findBySku("SKU-CAND-001").orElse(null);

        if (truffleCake == null || eclairs == null || candles == null) return;

        // Order 1: Delivered
        Order order1 = new Order();
        order1.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order1.setUser(john);
        order1.setRecipientName("John Doe");
        order1.setContactPhone("+91 9876543211");
        order1.setShippingAddress("42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034");
        order1.setBillingAddress("42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034");
        order1.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        order1.setPaymentStatus(PaymentStatus.PAID);
        order1.setOrderStatus(OrderStatus.DELIVERED);
        order1.setNotes("Please write 'Happy 30th Birthday John!' on the cake box.");

        OrderItem item1 = new OrderItem(order1, truffleCake, truffleCake.getName(), truffleCake.getSku(), truffleCake.getImageUrl(), truffleCake.getFinalPrice(), 1);
        OrderItem item2 = new OrderItem(order1, candles, candles.getName(), candles.getSku(), candles.getImageUrl(), candles.getFinalPrice(), 2);

        BigDecimal total1 = truffleCake.getPrice().multiply(new BigDecimal(1)).add(candles.getPrice().multiply(new BigDecimal(2)));
        BigDecimal final1 = item1.getTotalPrice().add(item2.getTotalPrice());

        order1.setTotalAmount(total1);
        order1.setFinalAmount(final1);
        order1.setDiscountAmount(total1.subtract(final1));
        order1.setItems(Arrays.asList(item1, item2));

        Order saved1 = orderRepository.save(order1);
        statusHistoryRepository.save(new OrderStatusHistory(saved1, OrderStatus.CREATED, "Order received", "john_doe"));
        statusHistoryRepository.save(new OrderStatusHistory(saved1, OrderStatus.CONFIRMED, "Payment verified", "system"));
        statusHistoryRepository.save(new OrderStatusHistory(saved1, OrderStatus.SHIPPED, "Dispatched in cold storage box", "admin"));
        statusHistoryRepository.save(new OrderStatusHistory(saved1, OrderStatus.DELIVERED, "Freshly delivered to customer", "admin"));

        // Order 2: Processing
        Order order2 = new Order();
        order2.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order2.setUser(john);
        order2.setRecipientName("John Doe");
        order2.setContactPhone("+91 9876543211");
        order2.setShippingAddress("42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034");
        order2.setBillingAddress("42 Tech Park Avenue, Koramangala, Bengaluru, Karnataka - 560034");
        order2.setPaymentMethod(PaymentMethod.COD);
        order2.setPaymentStatus(PaymentStatus.PENDING);
        order2.setOrderStatus(OrderStatus.PROCESSING);
        order2.setNotes("Cake delivery needed by 6 PM sharp for celebration.");

        OrderItem item3 = new OrderItem(order2, eclairs, eclairs.getName(), eclairs.getSku(), eclairs.getImageUrl(), eclairs.getFinalPrice(), 2);
        BigDecimal total2 = eclairs.getPrice().multiply(new BigDecimal(2));
        BigDecimal final2 = item3.getTotalPrice();

        order2.setTotalAmount(total2);
        order2.setFinalAmount(final2);
        order2.setDiscountAmount(total2.subtract(final2));
        order2.setItems(Collections.singletonList(item3));

        Order saved2 = orderRepository.save(order2);
        statusHistoryRepository.save(new OrderStatusHistory(saved2, OrderStatus.CREATED, "Order received", "john_doe"));
        statusHistoryRepository.save(new OrderStatusHistory(saved2, OrderStatus.CONFIRMED, "Bakery confirmed baking slot", "admin"));
        statusHistoryRepository.save(new OrderStatusHistory(saved2, OrderStatus.PROCESSING, "Fresh pastries being packed", "admin"));
    }
}
