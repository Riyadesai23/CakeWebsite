/**
 * Product Catalog & Search Controller
 */
app.controller('ProductListController', ['$scope', '$routeParams', '$location', 'ProductService', 'CategoryService', 'CartService', 'ToastService', 'AuthService',
function($scope, $routeParams, $location, ProductService, CategoryService, CartService, ToastService, AuthService) {
    $scope.loading = true;
    $scope.products = [];
    $scope.categories = [];
    $scope.brands = [];

    // Filter models
    $scope.filters = {
        keyword: $routeParams.keyword || '',
        categoryId: $routeParams.categoryId ? parseInt($routeParams.categoryId) : null,
        brand: $routeParams.brand || null,
        minPrice: $routeParams.minPrice || null,
        maxPrice: $routeParams.maxPrice || null,
        sortBy: $routeParams.sortBy || 'id',
        sortDir: $routeParams.sortDir || 'desc',
        page: $routeParams.page ? parseInt($routeParams.page) : 0,
        size: 12
    };

    $scope.pagination = {
        page: 0,
        size: 12,
        totalElements: 0,
        totalPages: 0,
        last: true
    };

    function loadFilters() {
        CategoryService.getActiveCategories().then(function(res) {
            if (res.success) $scope.categories = res.data;
        });

        ProductService.getBrands().then(function(res) {
            if (res.success) $scope.brands = res.data;
        });
    }

    $scope.fetchProducts = function() {
        $scope.loading = true;
        var params = {
            keyword: $scope.filters.keyword,
            categoryId: $scope.filters.categoryId,
            brand: $scope.filters.brand,
            minPrice: $scope.filters.minPrice,
            maxPrice: $scope.filters.maxPrice,
            page: $scope.filters.page,
            size: $scope.filters.size,
            sortBy: $scope.filters.sortBy,
            sortDir: $scope.filters.sortDir
        };

        ProductService.getProductsPublic(params).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.products = res.data.content;
                $scope.pagination.page = res.data.page;
                $scope.pagination.size = res.data.size;
                $scope.pagination.totalElements = res.data.totalElements;
                $scope.pagination.totalPages = res.data.totalPages;
                $scope.pagination.last = res.data.last;
            }
        }, function(err) {
            $scope.loading = false;
            ToastService.error('Failed to load products');
        });
    };

    $scope.applyFilter = function() {
        $scope.filters.page = 0;
        $scope.fetchProducts();
    };

    $scope.clearFilters = function() {
        $scope.filters = {
            keyword: '',
            categoryId: null,
            brand: null,
            minPrice: null,
            maxPrice: null,
            sortBy: 'id',
            sortDir: 'desc',
            page: 0,
            size: 12
        };
        $location.search({});
        $scope.fetchProducts();
    };

    $scope.changeSort = function(sortOption) {
        if (sortOption === 'price_asc') {
            $scope.filters.sortBy = 'finalPrice';
            $scope.filters.sortDir = 'asc';
        } else if (sortOption === 'price_desc') {
            $scope.filters.sortBy = 'finalPrice';
            $scope.filters.sortDir = 'desc';
        } else if (sortOption === 'name_asc') {
            $scope.filters.sortBy = 'name';
            $scope.filters.sortDir = 'asc';
        } else {
            $scope.filters.sortBy = 'id';
            $scope.filters.sortDir = 'desc';
        }
        $scope.applyFilter();
    };

    $scope.goToPage = function(pageIndex) {
        if (pageIndex >= 0 && pageIndex < $scope.pagination.totalPages) {
            $scope.filters.page = pageIndex;
            $scope.fetchProducts();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    $scope.addToCart = function(product, $event) {
        if ($event) $event.stopPropagation();
        if (!AuthService.isLoggedIn()) {
            ToastService.warning('Please log in to add items to your cart.');
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

    loadFilters();
    $scope.fetchProducts();
}]);
