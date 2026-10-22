# Bake My Day - Artisanal Cake & Pastry Bakery Web Application

A full-stack, enterprise-grade, luxury artisanal bakery and cake ordering platform named **"Bake My Day"**, built with **Java 1.8**, **Spring Boot 2.7.x**, **Spring MVC REST APIs**, **Spring Data JPA / Hibernate**, **AngularJS 1.8.x**, **Bootstrap 5**, and **Relational SQL Database (H2 / MySQL)**.

Featuring dedicated bakery departments for **Cakes**, **Pastries**, **Custom Cakes**, and **Candles & Party Decor**, with real-time atomic inventory deduction with pessimistic locking, role-based authorization, custom cake greeting notes, eggless dietary filters, and ready for deployment in **Spring Tool Suite 4 (STS4)** or standalone/external **Apache Tomcat** as a WAR archive.

---

## 1. Technology Stack

### Backend
- **Language**: Java 1.8 (strictly Java 8 compliant, no post-Java 8 features)
- **Framework**: Spring Boot 2.7.18 (Spring MVC, Spring Data JPA, Spring Security, Validation)
- **ORM / Persistence**: Hibernate 5.6.x with standard JPA annotations
- **Security**: Spring Security with Stateless JWT (JSON Web Tokens) + BCrypt password hashing
- **Build Tool**: Apache Maven 3.x (`pom.xml` configured for WAR packaging)
- **Database Support**: Embedded H2 (instant zero-config local run) + MySQL 5.7/8.0 (production ready)
- **Servlet Container**: Apache Tomcat (Embedded 9.x for development, external Tomcat 8.5/9.x WAR compatible)
- **IDE Compatibility**: 100% compatible with Spring Tool Suite 4 (STS4), Eclipse, IntelliJ IDEA, VS Code

### Frontend
- **Framework**: AngularJS 1.8.x (`angular.js`, `angular-route.js`)
- **UI / Styling**: Vanilla CSS3 Custom Design Tokens + Bootstrap 5.3 + FontAwesome 6
- **Typography**: Google Fonts ('Outfit' for headings, 'Inter' for body)
- **Architecture**: Single Page Application (SPA) with modular controllers, services, directives, route guards, and JWT HTTP interceptors.

---

## 2. Project Directory Structure

```
d:/Ecommerce_Project/
├── backend/
│   ├── pom.xml                               # Backend Maven POM (Java 1.8, WAR packaging)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/ecommerce/
│   │   │   │   ├── EcommerceApplication.java # Spring Boot main + SpringBootServletInitializer
│   │   │   │   ├── config/                   # SecurityConfig, WebConfig, DataInitializer
│   │   │   │   ├── controller/               # REST Controllers (Auth, Product, Category, Cart, Order, Admin, User, Inventory)
│   │   │   │   ├── dto/                      # Data Transfer Objects & ApiResponse envelope
│   │   │   │   ├── entity/                   # JPA Entities (User, Role, Address, Category, Product, Inventory, Cart, CartItem, Order, OrderItem, OrderStatusHistory)
│   │   │   │   ├── exception/                # GlobalExceptionHandler & Custom Exceptions
│   │   │   │   ├── repository/               # Spring Data JPA Repositories
│   │   │   │   ├── security/                 # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService, UserPrincipal
│   │   │   │   ├── service/                  # Service interfaces
│   │   │   │   │   └── impl/                 # Service implementations with @Transactional logic
│   │   │   │   └── util/                     # OrderNumberGenerator
│   │   │   └── resources/
│   │   │       ├── application.properties    # Default H2 database & server configuration
│   │   │       ├── application-mysql.properties # MySQL database profile configuration
│   │   │       └── static/                   # Bundled AngularJS Single Page App for single-server execution
│   │   └── test/
│   │       └── java/com/example/ecommerce/   # Unit & Integration Tests (Auth, Cart, Orders, Stock)
├── frontend/                                 # Standalone distribution of AngularJS SPA
│   ├── index.html                            # Master HTML5 SPA file
│   ├── css/
│   │   └── style.css                         # Custom styling design system
│   └── app/
│       ├── app.js                            # AngularJS module & JWT interceptor
│       ├── routes.js                         # Routing & Route Guards
│       ├── controllers/                      # 16 AngularJS Controllers
│       ├── services/                         # 8 AngularJS REST Services
│       └── views/                            # 16 Responsive HTML Templates
├── database/
│   ├── schema.sql                            # Full DDL for MySQL / Relational DBs
│   ├── seed.sql                              # Seed data with Admin, Users, Categories, Products, Inventory
│   └── README.md                             # Database setup documentation
├── pom.xml                                   # Root Maven parent POM
└── README.md                                 # Master Documentation
```

---

## 3. Default Credentials

| User Role | Username | Email | Default Password | Description |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin@ecommerce.com` | `admin123` | Full access to Dashboard, Category, Product, Inventory, User, and Order management |
| **USER** | `john_doe` | `john@example.com` | `password123` | Standard shopper account with addresses and order history |
| **USER** | `sarah_smith` | `sarah@example.com` | `password123` | Standard shopper account |
| **USER** | `mike_ross` | `mike@example.com` | `password123` | Standard shopper account |

> [!NOTE]
> All passwords are automatically hashed using **BCrypt** (`PasswordEncoder`). You can also register new accounts directly through the UI.

---

## 4. How to Import and Run in Spring Tool Suite 4 (STS4)

### Method A: Import as Maven Project (Recommended)
1. In **Spring Tool Suite 4**, go to the top menu: **File -> Import...**
2. Expand **Maven** and choose **Existing Maven Projects**, then click **Next**.
3. In **Root Directory**, browse to:
   ```
   c:\Final_Year_Project\Ecommerce_Project-main
   ```
   (or directly to `c:\Final_Year_Project\Ecommerce_Project-main\backend`).
4. Ensure `pom.xml` (or both `pom.xml` and `backend/pom.xml`) is checked.
5. Click **Finish**. STS will resolve all Maven dependencies.

### Method B: Import as Existing Project
1. In **Spring Tool Suite 4**, go to: **File -> Import...**
2. Expand **General** and select **Existing Projects into Workspace**, then click **Next**.
3. Select **Root Directory** as:
   ```
   c:\Final_Year_Project\Ecommerce_Project-main\backend
   ```
4. Click **Finish**. (Pre-configured `.project`, `.classpath`, and launch settings are already provided).

### How to Run the App in STS4:
- In the **Boot Dashboard** or **Project Explorer**, right-click **ecommerce-backend** -> **Run As** -> **Spring Boot App** (or run `EcommerceApplication.java`).
- Alternatively, select **Run -> Run Configurations...** and select **EcommerceApplication** (pre-configured launch profile).
- Once started, open your browser and navigate to:
  ```
  http://localhost:8080/
  ```

---

## 5. How to Run with Maven via Command Line

### Step 1: Run with Default Embedded H2 Database (Instant Zero-Config Run)
```bash
cd c:\Final_Year_Project\Ecommerce_Project-main\backend
mvn spring-boot:run
```
- The application will start on port `8080`.
- The database is initialized and seeded automatically by `DataInitializer.java`.
- Open `http://localhost:8080/` in your browser.
- H2 Web Console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:ecommercedb`, User: `sa`, Password: `[blank]`).

### Step 2: Run with MySQL Database
1. Create and populate the MySQL database:
   ```bash
   mysql -u root -p < c:\Final_Year_Project\Ecommerce_Project-main\database\schema.sql
   mysql -u root -p ecommerce_db < c:\Final_Year_Project\Ecommerce_Project-main\database\seed.sql
   ```
2. Update connection credentials in `backend/src/main/resources/application-mysql.properties` if needed.
3. Start the application with the `mysql` profile:
   ```bash
   cd c:\Final_Year_Project\Ecommerce_Project-main\backend
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 6. How to Deploy the WAR file to Apache Tomcat

1. Build the production WAR file:
   ```bash
   cd c:\Final_Year_Project\Ecommerce_Project-main\backend
   mvn clean package
   ```
2. The packaged WAR file will be generated at:
   ```
   c:\Final_Year_Project\Ecommerce_Project-main\backend\target\ecommerce-backend.war
   ```
3. Copy `ecommerce-backend.war` to your Apache Tomcat installation directory's `webapps/` folder:
   ```bash
   # Example:
   copy "c:\Final_Year_Project\Ecommerce_Project-main\backend\target\ecommerce-backend.war" "C:\apache-tomcat-9.0.x\webapps\"
   ```
4. Start Apache Tomcat (`bin/startup.bat` or `bin/catalina.sh run`).
5. Access the application at:
   ```
   http://localhost:8080/ecommerce-backend/
   ```

---

## 7. Complete REST API Specifications

All endpoints return a standardized JSON response:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "errorCode": null,
  "timestamp": "2026-08-26T11:45:00.000+00:00"
}
```

### Authentication APIs (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticate user/admin, returns JWT token & user info |
| `POST` | `/api/auth/register` | Public | Register new customer account & initialize cart |
| `GET` | `/api/auth/me` | Authenticated | Get current authenticated user details |

### Products APIs (`/api/products`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Public | Paginated product search with category, brand, and price filters |
| `GET` | `/api/products/{id}` | Public | Get single product details by ID |
| `GET` | `/api/products/sku/{sku}` | Public | Get product by SKU code |
| `GET` | `/api/products/brands` | Public | List distinct active brands for filtering |
| `GET` | `/api/products/admin` | Admin | Search all products including inactive items |
| `POST` | `/api/products` | Admin | Create product and initialize inventory stock |
| `PUT` | `/api/products/{id}` | Admin | Update product details |
| `DELETE` | `/api/products/{id}` | Admin | Soft-deactivate product |

### Categories APIs (`/api/categories`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Public | List all active categories |
| `GET` | `/api/categories/all` | Admin | List all categories including inactive |
| `GET` | `/api/categories/{id}` | Public | Get category by ID |
| `POST` | `/api/categories` | Admin | Create new category |
| `PUT` | `/api/categories/{id}` | Admin | Update existing category |
| `DELETE` | `/api/categories/{id}` | Admin | Soft-deactivate category |

### Inventory APIs (`/api/inventory`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/inventory` | Admin | Paginated inventory list with low-stock filter |
| `GET` | `/api/inventory/low-stock` | Admin | List all products at or below minimum stock level |
| `GET` | `/api/inventory/{productId}` | Admin | Get inventory stock details for product |
| `PUT` | `/api/inventory/{productId}` | Admin | Adjust stock (ADD / SUBTRACT / SET) & update threshold |
| `POST` | `/api/inventory/{productId}/add` | Admin | Quick restock quantity |
| `POST` | `/api/inventory/{productId}/remove` | Admin | Decrease stock quantity |

### Shopping Cart APIs (`/api/cart`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cart` | User | Get current user's shopping cart with live price recalculation |
| `POST` | `/api/cart/items` | User | Add product to cart with inventory stock validation |
| `PUT` | `/api/cart/items/{itemId}` | User | Update item quantity in cart |
| `DELETE` | `/api/cart/items/{itemId}` | User | Remove item from cart |
| `DELETE` | `/api/cart/clear` | User | Empty the user's cart |

### Order APIs (`/api/orders`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders` | User | Place order: atomic inventory deduction, price snapshots, clears cart |
| `GET` | `/api/orders` | User | Paginated order history for current user |
| `GET` | `/api/orders/{id}` | User | Get detailed order status and line items (User-restricted) |

### Admin Management APIs (`/api/admin` & `/api/users`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/dashboard` | Admin | Summary KPIs (Revenue, Total Orders, Stock Alerts, Recent Orders) |
| `GET` | `/api/admin/orders` | Admin | Search all customer orders with status filter |
| `GET` | `/api/admin/orders/{id}` | Admin | View full customer order details |
| `PUT` | `/api/admin/orders/{id}/status` | Admin | Update order fulfillment status (CREATED -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED / CANCELLED) |
| `GET` | `/api/users` | Admin | Search and list registered users |
| `GET` | `/api/users/{id}` | Admin | Get user details |
| `PUT` | `/api/users/{id}` | Admin | Update user details and roles |
| `PATCH` | `/api/users/{id}/status` | Admin | Activate or deactivate user account |
| `GET` | `/api/users/profile/me` | User | Get current user's profile |
| `PUT` | `/api/users/profile/me` | User | Update own name, phone, or password |

---

## 8. Business Rules & Concurrency Safeguards

1. **Atomic Inventory Deduction**:
   - Implemented via `@Transactional` and JPA pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`).
   - If stock is insufficient when placing an order, `OutOfStockException` is thrown and the transaction rolls back cleanly.
2. **Snapshot Pricing in Orders**:
   - Unit prices and product names are captured as an immutable snapshot in `order_items` at the moment of order placement.
   - Any subsequent product price changes will never corrupt historical invoices.
3. **Restocking on Order Cancellation**:
   - When an Admin changes an order's status to `CANCELLED`, the system automatically restores the deducted quantities back to the `Inventory` table.
4. **Price Integrity**:
   - Order subtotals, discounts, and totals are computed strictly on the backend. Frontend prices are never trusted.
5. **Account Status Enforcement**:
   - Deactivated users (`active = false`) are rejected at authentication time.
   - Deactivated products and categories are excluded from public catalog browsing.
6. **Data Isolation**:
   - Users can only access their own carts and orders. Admin APIs are restricted by Spring Security `@PreAuthorize("hasRole('ADMIN')")`.

---

## 9. Running Automated Tests

Run the complete test suite:
```bash
cd d:/Ecommerce_Project/backend
mvn test
```
The test suite validates:
- User registration, duplicate email/username rejection, and BCrypt verification (`AuthServiceTest`).
- Cart calculations, stock threshold checks, and inventory deduction during checkout (`CartAndOrderServiceTest`).
- Out-of-stock rejection and order cancellation inventory replenishment (`CartAndOrderServiceTest`).

---

## 10. Future Extensibility Guide

The architecture has been designed with clean extension points for future modules:
- **Payment Gateway Integration**: Add a `PaymentGatewayService` (e.g. Stripe, Razorpay) inside `OrderServiceImpl`.
- **Product Reviews & Ratings**: Create a `Review` entity with `@ManyToOne` to `Product` and `User`.
- **Wishlist**: Create a `Wishlist` entity mapping `User` to `Set<Product>`.
- **Coupons & Promo Codes**: Add a `Coupon` entity with validation rules evaluated in `CartService` and `OrderService`.
- **Multiple Delivery Addresses**: Extend `AddressRepository` and add an address picker during checkout.
