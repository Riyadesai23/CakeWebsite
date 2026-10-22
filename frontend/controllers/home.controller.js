/**
 * Home Page Controller
 */
app.controller('HomeController', ['$scope', '$location', 'ProductService', 'CategoryService', 'CartService', 'ToastService', 'AuthService',
function($scope, $location, ProductService, CategoryService, CartService, ToastService, AuthService) {
    $scope.loading = true;
    $scope.categories = [];
    $scope.featuredProducts = [];
    $scope.dealsProducts = [];

    function init() {
        CategoryService.getActiveCategories().then(function(res) {
            if (res.success) {
                $scope.categories = res.data;
            }
        });

        ProductService.getProductsPublic({ page: 0, size: 8, sortBy: 'id', sortDir: 'desc' })
            .then(function(res) {
                $scope.loading = false;
                if (res.success) {
                    $scope.featuredProducts = res.data.content;
                }
            }, function() {
                $scope.loading = false;
            });
    }

    $scope.addToCart = function(product, $event) {
        if ($event) $event.stopPropagation();
        if (!AuthService.isLoggedIn()) {
            ToastService.warning('Please log in to add items to your shopping cart.');
            $location.path('/login');
            return;
        }

        CartService.addToCart(product.id, 1).then(function(res) {
            if (res.success) {
                ToastService.success('Added "' + product.name + '" to cart!');
            }
        }, function(err) {
            var msg = (err.data && err.data.message) ? err.data.message : 'Could not add to cart';
            ToastService.error(msg);
        });
    };

    $scope.selectCategory = function(catId) {
        $location.path('/products').search({ categoryId: catId });
    };

    init();
}]);
