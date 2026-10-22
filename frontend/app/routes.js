/**
 * NovaStore Client Routing Configuration
 */
app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');

    $routeProvider
        // Public Routes
        .when('/', {
            templateUrl: 'app/views/home.html',
            controller: 'HomeController'
        })
        .when('/home', {
            templateUrl: 'app/views/home.html',
            controller: 'HomeController'
        })
        .when('/products', {
            templateUrl: 'app/views/product-list.html',
            controller: 'ProductListController'
        })
        .when('/products/:id', {
            templateUrl: 'app/views/product-detail.html',
            controller: 'ProductDetailController'
        })
        .when('/login', {
            templateUrl: 'app/views/login.html',
            controller: 'AuthController'
        })
        .when('/register', {
            templateUrl: 'app/views/register.html',
            controller: 'AuthController'
        })
        .when('/admin/login', {
            templateUrl: 'app/views/admin-login.html',
            controller: 'AuthController'
        })

        // Authenticated User Routes
        .when('/cart', {
            templateUrl: 'app/views/cart.html',
            controller: 'CartController',
            data: { requiresAuth: true }
        })
        .when('/checkout', {
            templateUrl: 'app/views/checkout.html',
            controller: 'CheckoutController',
            data: { requiresAuth: true }
        })
        .when('/orders', {
            templateUrl: 'app/views/order-history.html',
            controller: 'OrderHistoryController',
            data: { requiresAuth: true }
        })
        .when('/orders/:id', {
            templateUrl: 'app/views/order-detail.html',
            controller: 'OrderDetailController',
            data: { requiresAuth: true }
        })
        .when('/profile', {
            templateUrl: 'app/views/profile.html',
            controller: 'ProfileController',
            data: { requiresAuth: true }
        })

        // Admin Routes
        .when('/admin/dashboard', {
            templateUrl: 'app/views/admin/dashboard.html',
            controller: 'AdminDashboardController',
            data: { requiresAdmin: true }
        })
        .when('/admin/categories', {
            templateUrl: 'app/views/admin/categories.html',
            controller: 'AdminCategoriesController',
            data: { requiresAdmin: true }
        })
        .when('/admin/products', {
            templateUrl: 'app/views/admin/products.html',
            controller: 'AdminProductsController',
            data: { requiresAdmin: true }
        })
        .when('/admin/inventory', {
            templateUrl: 'app/views/admin/inventory.html',
            controller: 'AdminInventoryController',
            data: { requiresAdmin: true }
        })
        .when('/admin/orders', {
            templateUrl: 'app/views/admin/orders.html',
            controller: 'AdminOrdersController',
            data: { requiresAdmin: true }
        })
        .when('/admin/orders/:id', {
            templateUrl: 'app/views/admin/order-detail.html',
            controller: 'AdminOrdersController',
            data: { requiresAdmin: true }
        })
        .when('/admin/users', {
            templateUrl: 'app/views/admin/users.html',
            controller: 'AdminUsersController',
            data: { requiresAdmin: true }
        })

        .otherwise({
            redirectTo: '/home'
        });
}]);
