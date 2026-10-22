/**
 * Admin Dashboard Controller
 */
app.controller('AdminDashboardController', ['$scope', '$location', 'AdminService', 'InventoryService', 'ToastService',
function($scope, $location, AdminService, InventoryService, ToastService) {
    $scope.loading = true;
    $scope.stats = null;
    $scope.lowStockItems = [];

    function init() {
        $scope.loading = true;
        AdminService.getDashboardStats().then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.stats = res.data;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load dashboard metrics');
        });

        InventoryService.getLowStock().then(function(res) {
            if (res.success) {
                $scope.lowStockItems = res.data;
            }
        });
    }

    $scope.viewOrder = function(orderId) {
        $location.path('/admin/orders/' + orderId);
    };

    init();
}]);
