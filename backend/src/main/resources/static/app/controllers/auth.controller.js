/**
 * Authentication Controller (Login, Register, Admin Login)
 */
app.controller('AuthController', ['$scope', '$location', 'AuthService', 'CartService', 'ToastService',
function($scope, $location, AuthService, CartService, ToastService) {
    $scope.submitting = false;

    $scope.loginData = {
        usernameOrEmail: '',
        password: ''
    };

    $scope.registerData = {
        username: '',
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        phone: ''
    };

    $scope.doLogin = function() {
        if (!$scope.loginData.usernameOrEmail || !$scope.loginData.password) {
            ToastService.warning('Please enter your username/email and password.');
            return;
        }

        $scope.submitting = true;
        AuthService.login($scope.loginData).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success('Welcome back, ' + res.data.user.firstName + '!');
                CartService.refreshCartCount();

                if (res.data.user.roles && res.data.user.roles.indexOf('ROLE_ADMIN') !== -1) {
                    $location.path('/admin/dashboard');
                } else {
                    $location.path('/home');
                }
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Invalid credentials. Please try again.';
            ToastService.error(msg);
        });
    };

    $scope.doAdminLogin = function() {
        if (!$scope.loginData.usernameOrEmail || !$scope.loginData.password) {
            ToastService.warning('Please enter admin username and password.');
            return;
        }

        $scope.submitting = true;
        AuthService.login($scope.loginData).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                if (res.data.user.roles && res.data.user.roles.indexOf('ROLE_ADMIN') !== -1) {
                    ToastService.success('Admin login verified.');
                    $location.path('/admin/dashboard');
                } else {
                    AuthService.logout();
                    ToastService.error('Access denied: You do not have Administrator privileges.');
                }
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Admin authentication failed.';
            ToastService.error(msg);
        });
    };

    $scope.doRegister = function() {
        if (!$scope.registerData.username || !$scope.registerData.email || !$scope.registerData.password || !$scope.registerData.firstName) {
            ToastService.warning('Please fill in all required fields.');
            return;
        }

        $scope.submitting = true;
        AuthService.register($scope.registerData).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success('Registration successful! You can now log in.');
                $location.path('/login');
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Registration failed.';
            ToastService.error(msg);
        });
    };
}]);
