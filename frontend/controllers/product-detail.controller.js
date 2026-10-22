/**
 * Product Detail Controller
 */
app.controller('ProductDetailController', ['$scope', '$routeParams', '$location', 'ProductService', 'CartService', 'ToastService', 'AuthService',
function($scope, $routeParams, $location, ProductService, CartService, ToastService, AuthService) {
    $scope.loading = true;
    $scope.product = null;
    $scope.quantity = 1;
    $scope.relatedProducts = [];

    var productId = $routeParams.id;

    function loadProduct() {
        $scope.loading = true;
        ProductService.getProductById(productId).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.product = res.data;
                loadRelated($scope.product.categoryId);
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Product not found');
            $location.path('/products');
        });
    }

    function loadRelated(categoryId) {
        if (!categoryId) return;
        ProductService.getProductsPublic({ categoryId: categoryId, page: 0, size: 4 })
            .then(function(res) {
                if (res.success && res.data) {
                    $scope.relatedProducts = res.data.content.filter(function(p) {
                        return p.id !== parseInt(productId);
                    });
                }
            });
    }

    $scope.incrementQty = function() {
        if ($scope.product && $scope.quantity < $scope.product.availableQuantity) {
            $scope.quantity++;
        }
    };

    $scope.decrementQty = function() {
        if ($scope.quantity > 1) {
            $scope.quantity--;
        }
    };

    $scope.addToCart = function() {
        if (!AuthService.isLoggedIn()) {
            ToastService.warning('Please log in to add items to your cart.');
            $location.path('/login');
            return;
        }

        if (!$scope.product.inStock) {
            ToastService.error('This product is currently out of stock.');
            return;
        }

        CartService.addToCart($scope.product.id, $scope.quantity).then(function(res) {
            if (res.success) {
                ToastService.success('Added ' + $scope.quantity + ' item(s) to your cart!');
            }
        }, function(err) {
            var msg = (err.data && err.data.message) ? err.data.message : 'Could not add to cart';
            ToastService.error(msg);
        });
    };

    loadProduct();
}]);
