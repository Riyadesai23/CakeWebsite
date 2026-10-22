/**
 * Admin Order Management Controller
 */
app.controller('AdminOrdersController', ['$scope', '$routeParams', 'OrderService', 'ToastService',
function($scope, $routeParams, OrderService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.orders = [];
    $scope.currentOrder = null;
    $scope.selectedOrderForStatus = null;

    $scope.filters = {
        keyword: '',
        status: '',
        page: 0,
        size: 10
    };

    $scope.pagination = {
        totalPages: 0,
        totalElements: 0
    };

    $scope.statusForm = {
        status: '',
        comments: ''
    };

    var orderIdParam = $routeParams.id;

    $scope.loadOrders = function(pageIndex) {
        $scope.loading = true;
        $scope.filters.page = pageIndex !== undefined ? pageIndex : $scope.filters.page;

        var params = {
            keyword: $scope.filters.keyword,
            status: $scope.filters.status || null,
            page: $scope.filters.page,
            size: $scope.filters.size
        };

        OrderService.getAdminOrders(params).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.orders = res.data.content;
                $scope.pagination.totalPages = res.data.totalPages;
                $scope.pagination.totalElements = res.data.totalElements;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load orders');
        });
    };

    $scope.loadSingleOrder = function(id) {
        $scope.loading = true;
        OrderService.getAdminOrderDetails(id).then(function(res) {
            $scope.loading = false;
            if (res.success) {
                $scope.currentOrder = res.data;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load order details');
        });
    };

    $scope.openStatusModal = function(order) {
        $scope.selectedOrderForStatus = order;
        $scope.statusForm = {
            status: order.orderStatus,
            comments: ''
        };
        var modal = new bootstrap.Modal(document.getElementById('statusModal'));
        modal.show();
    };

    $scope.saveOrderStatus = function() {
        if (!$scope.selectedOrderForStatus || !$scope.statusForm.status) {
            ToastService.warning('Please select a valid order status.');
            return;
        }

        $scope.submitting = true;
        OrderService.updateOrderStatus($scope.selectedOrderForStatus.id, $scope.statusForm).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success('Order #' + $scope.selectedOrderForStatus.orderNumber + ' status updated to ' + $scope.statusForm.status);
                var modalEl = document.getElementById('statusModal');
                var modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();

                if (orderIdParam) {
                    $scope.loadSingleOrder(orderIdParam);
                } else {
                    $scope.loadOrders();
                }
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Failed to update order status';
            ToastService.error(msg);
        });
    };

    if (orderIdParam) {
        $scope.loadSingleOrder(orderIdParam);
    } else {
        $scope.loadOrders(0);
    }
}]);
