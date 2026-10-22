/**
 * User Profile Controller
 */
app.controller('ProfileController', ['$scope', 'AuthService', 'ToastService',
function($scope, AuthService, ToastService) {
    $scope.loading = true;
    $scope.saving = false;
    $scope.user = null;
    $scope.form = {
        firstName: '',
        lastName: '',
        phone: '',
        password: ''
    };

    function loadProfile() {
        $scope.loading = true;
        AuthService.fetchCurrentUserProfile().then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.user = res.data;
                $scope.form.firstName = res.data.firstName || '';
                $scope.form.lastName = res.data.lastName || '';
                $scope.form.phone = res.data.phone || '';
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load profile');
        });
    }

    $scope.saveProfile = function() {
        $scope.saving = true;
        AuthService.updateProfile($scope.form).then(function(res) {
            $scope.saving = false;
            if (res.success) {
                $scope.user = res.data;
                $scope.form.password = '';
                ToastService.success('Profile updated successfully!');
            }
        }, function(err) {
            $scope.saving = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Failed to update profile';
            ToastService.error(msg);
        });
    };

    loadProfile();
}]);
