/**
 * Category Service
 */
app.factory('CategoryService', ['$http', 'API_BASE_URL', function($http, API_BASE_URL) {
    return {
        getActiveCategories: function() {
            return $http.get(API_BASE_URL + '/categories').then(function(r) { return r.data; });
        },
        getAllCategoriesAdmin: function() {
            return $http.get(API_BASE_URL + '/categories/all').then(function(r) { return r.data; });
        },
        getCategoryById: function(id) {
            return $http.get(API_BASE_URL + '/categories/' + id).then(function(r) { return r.data; });
        },
        createCategory: function(category) {
            return $http.post(API_BASE_URL + '/categories', category).then(function(r) { return r.data; });
        },
        updateCategory: function(id, category) {
            return $http.put(API_BASE_URL + '/categories/' + id, category).then(function(r) { return r.data; });
        },
        deleteCategory: function(id) {
            return $http.delete(API_BASE_URL + '/categories/' + id).then(function(r) { return r.data; });
        }
    };
}]);
