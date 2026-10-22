/**
 * Inventory Service
 */
app.factory('InventoryService', ['$http', 'API_BASE_URL', function($http, API_BASE_URL) {
    return {
        searchInventory: function(params) {
            return $http.get(API_BASE_URL + '/inventory', { params: params })
                .then(function(r) { return r.data; });
        },
        getLowStock: function() {
            return $http.get(API_BASE_URL + '/inventory/low-stock')
                .then(function(r) { return r.data; });
        },
        getByProductId: function(productId) {
            return $http.get(API_BASE_URL + '/inventory/' + productId)
                .then(function(r) { return r.data; });
        },
        updateStock: function(productId, stockData) {
            return $http.put(API_BASE_URL + '/inventory/' + productId, stockData)
                .then(function(r) { return r.data; });
        },
        addStock: function(productId, quantity) {
            return $http.post(API_BASE_URL + '/inventory/' + productId + '/add?quantity=' + quantity)
                .then(function(r) { return r.data; });
        },
        removeStock: function(productId, quantity) {
            return $http.post(API_BASE_URL + '/inventory/' + productId + '/remove?quantity=' + quantity)
                .then(function(r) { return r.data; });
        }
    };
}]);
