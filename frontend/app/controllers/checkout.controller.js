/**
 * Checkout & Order Placement Controller
 */
app.controller('CheckoutController', ['$scope', '$location', 'CartService', 'OrderService', 'AuthService', 'ToastService',
function($scope, $location, CartService, OrderService, AuthService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.cart = null;

    var currentUser = AuthService.getCurrentUser();

    $scope.form = {
        recipientName: currentUser ? (currentUser.firstName + ' ' + (currentUser.lastName || '')).trim() : '',
        contactPhone: currentUser ? currentUser.phone || '' : '',
        addressLine1: '',
        addressLine2: '',
        city: '',
        state: '',
        postalCode: '',
        country: 'India',
        paymentMethod: 'COD',
        notes: ''
    };

    function init() {
        CartService.getCart().then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.cart = res.data;
                if (!$scope.cart.items || $scope.cart.items.length === 0) {
                    ToastService.warning('Your shopping cart is empty.');
                    $location.path('/cart');
                }
            }
        }, function() {
            $scope.loading = false;
            $location.path('/cart');
        });
    }

    $scope.placeOrder = function() {
        if (!$scope.form.recipientName || !$scope.form.contactPhone || !$scope.form.addressLine1 || !$scope.form.city || !$scope.form.postalCode) {
            ToastService.warning('Please complete all required shipping address fields.');
            return;
        }

        $scope.submitting = true;

        var fullAddress = $scope.form.addressLine1 + 
            ($scope.form.addressLine2 ? ', ' + $scope.form.addressLine2 : '') + 
            ', ' + $scope.form.city + ', ' + $scope.form.state + ' - ' + $scope.form.postalCode + ', ' + $scope.form.country;

        var checkoutPayload = {
            recipientName: $scope.form.recipientName,
            contactPhone: $scope.form.contactPhone,
            shippingAddress: fullAddress,
            billingAddress: fullAddress,
            paymentMethod: $scope.form.paymentMethod,
            notes: $scope.form.notes
        };

        OrderService.placeOrder(checkoutPayload).then(function(res) {
            $scope.submitting = false;
            if (res.success && res.data) {
                ToastService.success('Order placed successfully! Order #' + res.data.orderNumber);
                $location.path('/orders/' + res.data.id);
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Failed to place order. Please try again.';
            ToastService.error(msg);
        });
    };

    init();
}]);
