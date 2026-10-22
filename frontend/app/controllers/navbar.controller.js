/**
 * Navigation Bar Controller
 */
app.controller('NavbarController', ['$scope', '$location', 'AuthService', 'CategoryService', 'CartService', 'ToastService',
function($scope, $location, AuthService, CategoryService, CartService, ToastService) {
    $scope.searchQuery = '';
    $scope.categories = [];

    function loadCategories() {
        CategoryService.getActiveCategories().then(function(res) {
            if (res.success) {
                $scope.categories = res.data;
            }
        });
    }

    loadCategories();

    $scope.submitSearch = function() {
        if ($scope.searchQuery && $scope.searchQuery.trim() !== '') {
            $location.path('/products').search({ keyword: $scope.searchQuery.trim() });
        }
    };

    $scope.filterByCategory = function(catId) {
        $location.path('/products').search({ categoryId: catId });
    };

    $scope.logout = function() {
        AuthService.logout();
        ToastService.info('You have been logged out.');
        $location.path('/login');
    };
}]);
