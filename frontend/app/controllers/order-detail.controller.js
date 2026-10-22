/**
 * User Order Detail & Tracking Controller
 */
app.controller('OrderDetailController', ['$scope', '$routeParams', '$location', 'OrderService', 'ToastService',
function($scope, $routeParams, $location, OrderService, ToastService) {
    $scope.loading = true;
    $scope.order = null;

    var orderId = $routeParams.id;

    $scope.steps = [
        { key: 'CREATED', label: 'Order Placed', icon: 'fa-file-invoice' },
        { key: 'CONFIRMED', label: 'Confirmed', icon: 'fa-check' },
        { key: 'PROCESSING', label: 'Processing', icon: 'fa-box-open' },
        { key: 'SHIPPED', label: 'Shipped', icon: 'fa-truck-fast' },
        { key: 'DELIVERED', label: 'Delivered', icon: 'fa-house-chimney-user' }
    ];

    function loadOrder() {
        $scope.loading = true;
        OrderService.getOrderDetails(orderId).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.order = res.data;
            }
        }, function(err) {
            $scope.loading = false;
            ToastService.error('Order not found or unauthorized.');
            $location.path('/orders');
        });
    }

    $scope.isStepCompleted = function(stepKey) {
        if (!$scope.order || $scope.order.orderStatus === 'CANCELLED') return false;
        var orderMap = { 'CREATED': 0, 'CONFIRMED': 1, 'PROCESSING': 2, 'SHIPPED': 3, 'DELIVERED': 4 };
        var currentIdx = orderMap[$scope.order.orderStatus];
        var stepIdx = orderMap[stepKey];
        return stepIdx <= currentIdx;
    };

    $scope.isStepActive = function(stepKey) {
        if (!$scope.order) return false;
        return $scope.order.orderStatus === stepKey;
    };

    loadOrder();
}]);
