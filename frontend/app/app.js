/**
 * NovaStore AngularJS Application Initialization
 * Version: 1.8.x Compatible
 */
var app = angular.module('ecommerceApp', ['ngRoute']);

// Base API URL configuration (relative for same-origin or Tomcat WAR context)
app.constant('API_BASE_URL', '/api');

// Configure HTTP Interceptor for JWT authentication and error handling
app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push(['$q', '$location', '$window', function($q, $location, $window) {
        return {
            'request': function(config) {
                var token = $window.localStorage.getItem('novastore_token');
                if (token && config.url.indexOf('/api/') !== -1) {
                    config.headers['Authorization'] = 'Bearer ' + token;
                }
                return config;
            },
            'responseError': function(rejection) {
                if (rejection.status === 401) {
                    $window.localStorage.removeItem('novastore_token');
                    $window.localStorage.removeItem('novastore_user');
                    // Avoid redirect loops if already on login
                    if ($location.path().indexOf('/login') === -1) {
                        $location.path('/login');
                    }
                } else if (rejection.status === 403) {
                    $location.path('/home');
                }
                return $q.reject(rejection);
            }
        };
    }]);
}]);

// Global Route Guard & Auth listener
app.run(['$rootScope', '$location', 'AuthService', 'CartService', function($rootScope, $location, AuthService, CartService) {
    $rootScope.currentUser = AuthService.getCurrentUser();
    $rootScope.isLoggedIn = AuthService.isLoggedIn();
    $rootScope.isAdmin = AuthService.isAdmin();

    // Refresh cart count on app load if user logged in
    if ($rootScope.isLoggedIn) {
        CartService.refreshCartCount();
    }

    $rootScope.$on('$routeChangeStart', function(event, next) {
        var user = AuthService.getCurrentUser();
        var loggedIn = AuthService.isLoggedIn();
        var admin = AuthService.isAdmin();

        $rootScope.currentUser = user;
        $rootScope.isLoggedIn = loggedIn;
        $rootScope.isAdmin = admin;

        if (next && next.data) {
            // Admin only routes
            if (next.data.requiresAdmin) {
                if (!loggedIn) {
                    event.preventDefault();
                    $location.path('/admin/login');
                } else if (!admin) {
                    event.preventDefault();
                    $location.path('/home');
                }
            }
            // User authentication required routes
            else if (next.data.requiresAuth) {
                if (!loggedIn) {
                    event.preventDefault();
                    $location.path('/login');
                }
            }
        }
    });
}]);
