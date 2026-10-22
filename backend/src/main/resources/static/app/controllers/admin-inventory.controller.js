/**
 * Admin Inventory Management Controller
 */
app.controller('AdminInventoryController', ['$scope', 'InventoryService', 'ToastService',
function($scope, InventoryService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.inventoryList = [];
    $scope.selectedItem = null;

    $scope.filters = {
        keyword: '',
        lowStockOnly: false,
        page: 0,
        size: 10
    };

    $scope.pagination = {
        totalPages: 0,
        totalElements: 0
    };

    $scope.stockForm = {
        quantity: 10,
        operation: 'ADD',
        minimumStockLevel: 5,
        reason: 'Restock shipment'
    };

    $scope.loadInventory = function(pageIndex) {
        $scope.loading = true;
        $scope.filters.page = pageIndex !== undefined ? pageIndex : $scope.filters.page;

        InventoryService.searchInventory($scope.filters).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.inventoryList = res.data.content;
                $scope.pagination.totalPages = res.data.totalPages;
                $scope.pagination.totalElements = res.data.totalElements;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load inventory');
        });
    };

    $scope.openAdjustModal = function(item) {
        $scope.selectedItem = item;
        $scope.stockForm = {
            quantity: 10,
            operation: 'ADD',
            minimumStockLevel: item.minimumStockLevel,
            reason: 'Regular restock'
        };
        var modal = new bootstrap.Modal(document.getElementById('inventoryModal'));
        modal.show();
    };

    $scope.saveStockAdjustment = function() {
        if (!$scope.selectedItem || $scope.stockForm.quantity === null || $scope.stockForm.quantity < 0) {
            ToastService.warning('Please provide a valid quantity.');
            return;
        }

        $scope.submitting = true;
        var payload = {
            quantity: $scope.stockForm.quantity,
            operation: $scope.stockForm.operation,
            minimumStockLevel: $scope.stockForm.minimumStockLevel,
            reason: $scope.stockForm.reason
        };

        InventoryService.updateStock($scope.selectedItem.productId, payload).then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success('Stock updated for ' + $scope.selectedItem.productName);
                var modalEl = document.getElementById('inventoryModal');
                var modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                $scope.loadInventory();
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Failed to update stock';
            ToastService.error(msg);
        });
    };

    $scope.quickAddStock = function(item, amount) {
        InventoryService.addStock(item.productId, amount).then(function(res) {
            if (res.success) {
                ToastService.success('Added +' + amount + ' units to stock!');
                $scope.loadInventory();
            }
        }, function(err) {
            var msg = (err.data && err.data.message) ? err.data.message : 'Error adding stock';
            ToastService.error(msg);
        });
    };

    $scope.loadInventory(0);
}]);
