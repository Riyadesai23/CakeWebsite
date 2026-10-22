/**
 * Admin Service (Dashboard & Users)
 */
app.factory('AdminService', ['$http', 'API_BASE_URL', function($http, API_BASE_URL) {
    return {
        getDashboardStats: function() {
            return $http.get(API_BASE_URL + '/admin/dashboard').then(function(r) { return r.data; });
        },
        getUsers: function(params) {
            return $http.get(API_BASE_URL + '/users', { params: params }).then(function(r) { return r.data; });
        },
        getUserById: function(id) {
            return $http.get(API_BASE_URL + '/users/' + id).then(function(r) { return r.data; });
        },
        updateUser: function(id, userData) {
            return $http.put(API_BASE_URL + '/users/' + id, userData).then(function(r) { return r.data; });
        },
        toggleUserStatus: function(id, active) {
            return $http.patch(API_BASE_URL + '/users/' + id + '/status?active=' + active).then(function(r) { return r.data; });
        }
    };
}]);
