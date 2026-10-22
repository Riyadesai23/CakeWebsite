/**
 * Order Service
 */
app.factory('OrderService', ['$http', 'API_BASE_URL', function($http, API_BASE_URL) {
    return {
        placeOrder: function(checkoutData) {
            return $http.post(API_BASE_URL + '/orders', checkoutData).then(function(r) { return r.data; });
        },
        getMyOrders: function(params) {
            return $http.get(API_BASE_URL + '/orders', { params: params }).then(function(r) { return r.data; });
        },
        getOrderDetails: function(id) {
            return $http.get(API_BASE_URL + '/orders/' + id).then(function(r) { return r.data; });
        },
        getAdminOrders: function(params) {
            return $http.get(API_BASE_URL + '/admin/orders', { params: params }).then(function(r) { return r.data; });
        },
        getAdminOrderDetails: function(id) {
            return $http.get(API_BASE_URL + '/admin/orders/' + id).then(function(r) { return r.data; });
        },
        updateOrderStatus: function(id, statusData) {
            return $http.put(API_BASE_URL + '/admin/orders/' + id + '/status', statusData).then(function(r) { return r.data; });
        }
    };
}]);
