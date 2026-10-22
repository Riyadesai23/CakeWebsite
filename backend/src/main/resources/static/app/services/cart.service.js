/**
 * Shopping Cart Service
 */
app.factory('CartService', ['$http', '$rootScope', 'API_BASE_URL', function($http, $rootScope, API_BASE_URL) {
    $rootScope.cartCount = 0;

    return {
        getCart: function() {
            return $http.get(API_BASE_URL + '/cart')
                .then(function(r) {
                    if (r.data.success && r.data.data) {
                        $rootScope.cartCount = r.data.data.totalItems || 0;
                    }
                    return r.data;
                });
        },

        addToCart: function(productId, quantity) {
            var payload = {
                productId: productId,
                quantity: quantity || 1
            };
            return $http.post(API_BASE_URL + '/cart/items', payload)
                .then(function(r) {
                    if (r.data.success && r.data.data) {
                        $rootScope.cartCount = r.data.data.totalItems || 0;
                    }
                    return r.data;
                });
        },

        updateCartItem: function(itemId, quantity) {
            return $http.put(API_BASE_URL + '/cart/items/' + itemId, { quantity: quantity })
                .then(function(r) {
                    if (r.data.success && r.data.data) {
                        $rootScope.cartCount = r.data.data.totalItems || 0;
                    }
                    return r.data;
                });
        },

        removeCartItem: function(itemId) {
            return $http.delete(API_BASE_URL + '/cart/items/' + itemId)
                .then(function(r) {
                    if (r.data.success && r.data.data) {
                        $rootScope.cartCount = r.data.data.totalItems || 0;
                    }
                    return r.data;
                });
        },

        clearCart: function() {
            return $http.delete(API_BASE_URL + '/cart/clear')
                .then(function(r) {
                    $rootScope.cartCount = 0;
                    return r.data;
                });
        },

        refreshCartCount: function() {
            var token = localStorage.getItem('novastore_token');
            if (token) {
                this.getCart();
            }
        }
    };
}]);
