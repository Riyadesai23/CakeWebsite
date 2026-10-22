/**
 * Admin Product Management Controller
 */
app.controller('AdminProductsController', ['$scope', 'ProductService', 'CategoryService', 'ToastService',
function($scope, ProductService, CategoryService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.products = [];
    $scope.categories = [];
    $scope.selectedProduct = null;
    $scope.isEditing = false;

    $scope.filters = {
        keyword: '',
        categoryId: null,
        active: null,
        page: 0,
        size: 10
    };

    $scope.pagination = {
        totalPages: 0,
        totalElements: 0
    };

    $scope.formData = {
        name: '',
        sku: '',
        description: '',
        categoryId: null,
        price: null,
        discountPercentage: 0,
        minimumStockLevel: 5,
        initialQuantity: 10,
        imageUrl: '',
        brand: '',
        unit: 'Piece',
        active: true
    };

    function loadCategories() {
        CategoryService.getAllCategoriesAdmin().then(function(res) {
            if (res.success) $scope.categories = res.data;
        });
    }

    $scope.loadProducts = function(pageIndex) {
        $scope.loading = true;
        $scope.filters.page = pageIndex !== undefined ? pageIndex : $scope.filters.page;

        ProductService.getProductsAdmin($scope.filters).then(function(res) {
            $scope.loading = false;
            if (res.success && res.data) {
                $scope.products = res.data.content;
                $scope.pagination.totalPages = res.data.totalPages;
                $scope.pagination.totalElements = res.data.totalElements;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load products');
        });
    };

    $scope.openCreateModal = function() {
        $scope.isEditing = false;
        $scope.selectedProduct = null;
        $scope.formData = {
            name: '',
            sku: 'SKU-' + Math.floor(1000 + Math.random() * 9000),
            description: '',
            categoryId: $scope.categories.length > 0 ? $scope.categories[0].id : null,
            price: null,
            discountPercentage: 0,
            minimumStockLevel: 5,
            initialQuantity: 10,
            imageUrl: '',
            brand: '',
            unit: 'Piece',
            active: true
        };
        var modal = new bootstrap.Modal(document.getElementById('productModal'));
        modal.show();
    };

    $scope.openEditModal = function(prod) {
        $scope.isEditing = true;
        $scope.selectedProduct = prod;
        $scope.formData = {
            name: prod.name,
            sku: prod.sku,
            description: prod.description,
            categoryId: prod.categoryId,
            price: prod.price,
            discountPercentage: prod.discountPercentage,
            minimumStockLevel: prod.minimumStockLevel,
            imageUrl: prod.imageUrl,
            brand: prod.brand,
            unit: prod.unit,
            active: prod.active
        };
        var modal = new bootstrap.Modal(document.getElementById('productModal'));
        modal.show();
    };

    $scope.saveProduct = function() {
        if (!$scope.formData.name || !$scope.formData.sku || !$scope.formData.categoryId || !$scope.formData.price) {
            ToastService.warning('Please fill in Name, SKU, Category, and Price.');
            return;
        }

        $scope.submitting = true;
        var action = $scope.isEditing ?
            ProductService.updateProduct($scope.selectedProduct.id, $scope.formData) :
            ProductService.createProduct($scope.formData);

        action.then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success($scope.isEditing ? 'Product updated successfully!' : 'Product created successfully!');
                var modalEl = document.getElementById('productModal');
                var modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                $scope.loadProducts();
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Error saving product';
            ToastService.error(msg);
        });
    };

    $scope.toggleStatus = function(prod) {
        var newStatus = !prod.active;
        var updatePayload = {
            name: prod.name,
            sku: prod.sku,
            description: prod.description,
            categoryId: prod.categoryId,
            price: prod.price,
            discountPercentage: prod.discountPercentage,
            minimumStockLevel: prod.minimumStockLevel,
            imageUrl: prod.imageUrl,
            brand: prod.brand,
            unit: prod.unit,
            active: newStatus
        };

        ProductService.updateProduct(prod.id, updatePayload).then(function(res) {
            if (res.success) {
                prod.active = newStatus;
                ToastService.success('Product status updated.');
            }
        });
    };

    loadCategories();
    $scope.loadProducts(0);
}]);
