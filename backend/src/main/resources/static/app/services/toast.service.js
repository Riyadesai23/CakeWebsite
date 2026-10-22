/**
 * Toast Notification Service
 */
app.factory('ToastService', ['$rootScope', '$timeout', function($rootScope, $timeout) {
    $rootScope.toasts = [];

    function show(message, type, title) {
        var id = new Date().getTime();
        var icon = 'fa-circle-info';
        if (type === 'success') icon = 'fa-circle-check';
        else if (type === 'danger') icon = 'fa-circle-exclamation';
        else if (type === 'warning') icon = 'fa-triangle-exclamation';

        var toast = {
            id: id,
            title: title || (type.charAt(0).toUpperCase() + type.slice(1)),
            message: message,
            type: type || 'info',
            icon: icon
        };

        $rootScope.toasts.push(toast);

        $timeout(function() {
            for (var i = 0; i < $rootScope.toasts.length; i++) {
                if ($rootScope.toasts[i].id === id) {
                    $rootScope.toasts.splice(i, 1);
                    break;
                }
            }
        }, 4000);
    }

    return {
        success: function(msg, title) { show(msg, 'success', title || 'Success'); },
        error: function(msg, title) { show(msg, 'danger', title || 'Error'); },
        warning: function(msg, title) { show(msg, 'warning', title || 'Warning'); },
        info: function(msg, title) { show(msg, 'info', title || 'Information'); },
        remove: function(id) {
            for (var i = 0; i < $rootScope.toasts.length; i++) {
                if ($rootScope.toasts[i].id === id) {
                    $rootScope.toasts.splice(i, 1);
                    break;
                }
            }
        }
    };
}]);
