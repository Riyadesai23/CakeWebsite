# NovaStore Database Documentation

This directory contains the complete Relational Database Definition (DDL) and Seed Data for the **NovaStore Multi-Purpose E-Commerce Platform**.

---

## 1. Database Files

- [`schema.sql`](file:///d:/Ecommerce_Project/database/schema.sql): Complete SQL DDL statements creating all 12 tables, indexes, constraints, and foreign key relationships.
- [`seed.sql`](file:///d:/Ecommerce_Project/database/seed.sql): Comprehensive test seed data with 2 roles, 4 user accounts, 6 product categories, 19 products, inventory stock levels, addresses, and sample orders.

---

## 2. Table Specifications & Relationships

```
 users (id, username, email, password, first_name, last_name, phone, active, created_at, updated_at)
   ├── user_roles (user_id, role_id) ──> roles (id, name)
   ├── addresses (id, user_id, recipient_name, phone, address_line1, city, state, postal_code, ...)
   ├── carts (id, user_id, created_at, updated_at)
   │     └── cart_items (id, cart_id, product_id, quantity, unit_price, total_price)
   └── orders (id, order_number, user_id, recipient_name, shipping_address, final_amount, order_status, ...)
         ├── order_items (id, order_id, product_id, product_name, product_sku, unit_price, quantity, total_price)
         └── order_status_history (id, order_id, status, comments, changed_by, changed_at)

 categories (id, name, code, description, image_url, active)
   └── products (id, name, sku, category_id, price, discount_percentage, final_price, minimum_stock_level, unit, brand, active)
         └── inventory (id, product_id, available_quantity, reserved_quantity, minimum_stock_level, last_restocked_at)
```

---

## 3. How to Setup with MySQL

1. Ensure MySQL 5.7+ or 8.0+ is running on `localhost:3306`.
2. Execute the schema and seed scripts using MySQL CLI or MySQL Workbench:

```bash
mysql -u root -p < d:/Ecommerce_Project/database/schema.sql
mysql -u root -p ecommerce_db < d:/Ecommerce_Project/database/seed.sql
```

3. To connect Spring Boot to MySQL, start the application with the `mysql` profile:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
