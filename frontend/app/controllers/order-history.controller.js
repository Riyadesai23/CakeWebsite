/**
 * User Order History Controller
 */
app.controller('OrderHistoryController', ['$scope', '$location', 'OrderService', 'ToastService',
function($scope, $location, OrderService, ToastService) {
    $scope.loading = true;
    $scope.orders = [];
    $scope.page = 0;
    $scope.size = 10;
    $scope.totalPages = 0;
    $scope.totalElements = 0;

    $scope.loadOrders = function(pageIndex) {
        $scope.loading = true;
        $scope.page = pageIndex || 0;

        OrderService.getMyOrders({ page: $scope.page, size: $scope.size })
            .then(function(res) {
                $scope.loading = false;
                if (res.success && res.data) {
                    $scope.orders = res.data.content;
                    $scope.totalPages = res.data.totalPages;
                    $scope.totalElements = res.data.totalElements;
                }
            }, function() {
                $scope.loading = false;
                ToastService.error('Failed to load your orders.');
            });
    };

    $scope.viewOrder = function(orderId) {
        $location.path('/orders/' + orderId);
    };

    $scope.loadOrders(0);
}]);
