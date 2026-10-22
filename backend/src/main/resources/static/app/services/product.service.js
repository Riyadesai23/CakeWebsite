/**
 * Product Service
 */
app.factory('ProductService', ['$http', 'API_BASE_URL', function($http, API_BASE_URL) {
    return {
        getProductsPublic: function(params) {
            return $http.get(API_BASE_URL + '/products', { params: params })
                .then(function(r) { return r.data; });
        },
        getProductsAdmin: function(params) {
            return $http.get(API_BASE_URL + '/products/admin', { params: params })
                .then(function(r) { return r.data; });
        },
        getProductById: function(id) {
            return $http.get(API_BASE_URL + '/products/' + id)
                .then(function(r) { return r.data; });
        },
        getProductBySku: function(sku) {
            return $http.get(API_BASE_URL + '/products/sku/' + sku)
                .then(function(r) { return r.data; });
        },
        getBrands: function() {
            return $http.get(API_BASE_URL + '/products/brands')
                .then(function(r) { return r.data; });
        },
        createProduct: function(product) {
            return $http.post(API_BASE_URL + '/products', product)
                .then(function(r) { return r.data; });
        },
        updateProduct: function(id, product) {
            return $http.put(API_BASE_URL + '/products/' + id, product)
                .then(function(r) { return r.data; });
        },
        deleteProduct: function(id) {
            return $http.delete(API_BASE_URL + '/products/' + id)
                .then(function(r) { return r.data; });
        }
    };
}]);
