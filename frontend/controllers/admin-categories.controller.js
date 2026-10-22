/**
 * Admin Category Management Controller
 */
app.controller('AdminCategoriesController', ['$scope', 'CategoryService', 'ToastService',
function($scope, CategoryService, ToastService) {
    $scope.loading = true;
    $scope.submitting = false;
    $scope.categories = [];
    $scope.selectedCategory = null;
    $scope.isEditing = false;

    $scope.formData = {
        name: '',
        code: '',
        description: '',
        imageUrl: '',
        active: true
    };

    $scope.loadCategories = function() {
        $scope.loading = true;
        CategoryService.getAllCategoriesAdmin().then(function(res) {
            $scope.loading = false;
            if (res.success) {
                $scope.categories = res.data;
            }
        }, function() {
            $scope.loading = false;
            ToastService.error('Failed to load categories');
        });
    };

    $scope.openCreateModal = function() {
        $scope.isEditing = false;
        $scope.selectedCategory = null;
        $scope.formData = {
            name: '',
            code: '',
            description: '',
            imageUrl: '',
            active: true
        };
        var modal = new bootstrap.Modal(document.getElementById('categoryModal'));
        modal.show();
    };

    $scope.openEditModal = function(cat) {
        $scope.isEditing = true;
        $scope.selectedCategory = cat;
        $scope.formData = {
            name: cat.name,
            code: cat.code,
            description: cat.description,
            imageUrl: cat.imageUrl,
            active: cat.active
        };
        var modal = new bootstrap.Modal(document.getElementById('categoryModal'));
        modal.show();
    };

    $scope.saveCategory = function() {
        if (!$scope.formData.name || !$scope.formData.code) {
            ToastService.warning('Name and Code are required.');
            return;
        }

        $scope.submitting = true;
        var action = $scope.isEditing ?
            CategoryService.updateCategory($scope.selectedCategory.id, $scope.formData) :
            CategoryService.createCategory($scope.formData);

        action.then(function(res) {
            $scope.submitting = false;
            if (res.success) {
                ToastService.success($scope.isEditing ? 'Category updated!' : 'Category created!');
                var modalEl = document.getElementById('categoryModal');
                var modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                $scope.loadCategories();
            }
        }, function(err) {
            $scope.submitting = false;
            var msg = (err.data && err.data.message) ? err.data.message : 'Error saving category';
            ToastService.error(msg);
        });
    };

    $scope.toggleStatus = function(cat) {
        var newStatus = !cat.active;
        var updatePayload = {
            name: cat.name,
            code: cat.code,
            description: cat.description,
            imageUrl: cat.imageUrl,
            active: newStatus
        };

        CategoryService.updateCategory(cat.id, updatePayload).then(function(res) {
            if (res.success) {
                cat.active = newStatus;
                ToastService.success('Category status updated.');
            }
        });
    };

    $scope.loadCategories();
}]);
