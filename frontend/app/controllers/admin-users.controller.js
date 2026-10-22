/**
 * Admin User Management Controller
 */
app.controller('AdminUsersController', ['$scope', 'AdminService', 'ToastService',
function($scope, AdminService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.users = [];
    $scope.selectedUser = null;

    $scope.filters = {
        keyword: '',
        page: 0,
        size: 10
    };

    $scope.pagination = {
        totalPages: 0,
        totalElements: 0
    };

    $scope.editForm = {
        firstName: '',
        lastName: '',
        phone: '',
        password: '',
        active: true,
        roles: []
    };

    $scope.loadUsers = function(pageIndex) {
        $scope.loading = true;
        $scope.filters.page = pageIndex !== undefined ? pageIndex : $scope.filters.page;

        AdminService.getUsers($scope.filters).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.users = res.data.content;
                $scope.pagination.totalPages = res.data.totalPages;
                $scope.pagination.totalElements = res.data.totalElements;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load users');
        });
    };

    $scope.openEditModal = function(user) {
        $scope.selectedUser = user;
        $scope.editForm = {
            firstName: user.firstName,
            lastName: user.lastName,
            phone: user.phone,
            password: '',
            active: user.active,
            roles: angular.copy(user.roles || [])
        };
        var modal = new bootstrap.Modal(document.getElementById('userModal'));
        modal.show();
    };

    $scope.saveUser = function() {
        if (!$scope.editForm.firstName) {
            ToastService.warning('First name is required.');
            return;
        }

        $scope.submitting = true;
        AdminService.updateUser($scope.selectedUser.id, $scope.editForm).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success('User updated successfully!');
                var modalEl = document.getElementById('userModal');
                var modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                $scope.loadUsers();
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Failed to update user';
            ToastService.error(msg);
        });
    };

    $scope.toggleUserStatus = function(user) {
        var newStatus = !user.active;
        AdminService.toggleUserStatus(user.id, newStatus).then(function(res) {
            if (res.success) {
                user.active = newStatus;
                ToastService.success(newStatus ? 'User activated' : 'User deactivated');
            }
        }, function(err) {
            ToastService.error('Could not update user status');
        });
    };

    $scope.loadUsers(0);
}]);
