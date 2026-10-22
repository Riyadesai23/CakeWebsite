/**
 * Authentication Service
 */
app.factory('AuthService', ['$http', '$window', '$rootScope', 'API_BASE_URL', function($http, $window, $rootScope, API_BASE_URL) {
    var TOKEN_KEY = 'novastore_token';
    var USER_KEY = 'novastore_user';

    return {
        login: function(credentials) {
            return $http.post(API_BASE_URL + '/auth/login', credentials)
                .then(function(response) {
                    if (response.data.success && response.data.data) {
                        var authData = response.data.data;
                        $window.localStorage.setItem(TOKEN_KEY, authData.token);
                        $window.localStorage.setItem(USER_KEY, JSON.stringify(authData.user));
                        $rootScope.currentUser = authData.user;
                        $rootScope.isLoggedIn = true;
                        $rootScope.isAdmin = authData.user.roles && authData.user.roles.indexOf('ROLE_ADMIN') !== -1;
                    }
                    return response.data;
                });
        },

        register: function(userData) {
            return $http.post(API_BASE_URL + '/auth/register', userData)
                .then(function(response) {
                    return response.data;
                });
        },

        logout: function() {
            $window.localStorage.removeItem(TOKEN_KEY);
            $window.localStorage.removeItem(USER_KEY);
            $rootScope.currentUser = null;
            $rootScope.isLoggedIn = false;
            $rootScope.isAdmin = false;
            $rootScope.cartCount = 0;
        },

        getCurrentUser: function() {
            var userStr = $window.localStorage.getItem(USER_KEY);
            if (userStr) {
                try {
                    return JSON.parse(userStr);
                } catch (e) {
                    return null;
                }
            }
            return null;
        },

        getToken: function() {
            return $window.localStorage.getItem(TOKEN_KEY);
        },

        isLoggedIn: function() {
            return !!$window.localStorage.getItem(TOKEN_KEY);
        },

        isAdmin: function() {
            var user = this.getCurrentUser();
            return !!(user && user.roles && user.roles.indexOf('ROLE_ADMIN') !== -1);
        },

        fetchCurrentUserProfile: function() {
            return $http.get(API_BASE_URL + '/users/profile/me')
                .then(function(response) {
                    if (response.data.success) {
                        $window.localStorage.setItem(USER_KEY, JSON.stringify(response.data.data));
                        $rootScope.currentUser = response.data.data;
                    }
                    return response.data;
                });
        },

        updateProfile: function(profileData) {
            return $http.put(API_BASE_URL + '/users/profile/me', profileData)
                .then(function(response) {
                    if (response.data.success) {
                        $window.localStorage.setItem(USER_KEY, JSON.stringify(response.data.data));
                        $rootScope.currentUser = response.data.data;
                    }
                    return response.data;
                });
        }
    };
}]);
