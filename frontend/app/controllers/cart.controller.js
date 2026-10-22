/**
 * Shopping Cart Controller
 */
app.controller('CartController', ['$scope', '$location', 'CartService', 'ToastService',
function($scope, $location, CartService, ToastService) {
    $scope.loading = true;
    $scope.cart = { items: [], totalItems: 0, subtotal: 0, discountAmount: 0, finalAmount: 0 };

    $scope.loadCart = function() {
        $scope.loading = true;
        CartService.getCart().then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.cart = res.data;
            }
        }, function(err) {
            $scope.loading = false;
            ToastService.error('Failed to load shopping cart');
        });
    };

    $scope.updateQuantity = function(item, change) {
        var newQty = item.quantity + change;
        if (newQty <= 0) {
            $scope.removeItem(item);
            return;
        }

        if (item.availableStock && newQty > item.availableStock) {
            ToastService.warning('Only ' + item.availableStock + ' items available in stock.');
            return;
        }

        CartService.updateCartItem(item.id, newQty).then(function(res) {
            if (res.success && res.data) {
                $scope.cart = res.data;
            }
        }, function(err) {
            var msg = (err.data && err.data.message) ? err.data.message : 'Could not update quantity';
            ToastService.error(msg);
        });
    };

    $scope.removeItem = function(item) {
        CartService.removeCartItem(item.id).then(function(res) {
            if (res.success && res.data) {
                $scope.cart = res.data;
                ToastService.info('Removed "' + item.productName + '" from cart');
            }
        }, function(err) {
            ToastService.error('Failed to remove item');
        });
    };

    $scope.clearCart = function() {
        if (confirm('Are you sure you want to clear your entire cart?')) {
            CartService.clearCart().then(function(res) {
                if (res.success) {
                    $scope.cart = { items: [], totalItems: 0, subtotal: 0, discountAmount: 0, finalAmount: 0 };
                    ToastService.info('Cart cleared');
                }
            });
        }
    };

    $scope.proceedToCheckout = function() {
        if (!$scope.cart.items || $scope.cart.items.length === 0) {
            ToastService.warning('Your shopping cart is empty!');
            return;
        }
        $location.path('/checkout');
    };

    $scope.loadCart();
}]);
