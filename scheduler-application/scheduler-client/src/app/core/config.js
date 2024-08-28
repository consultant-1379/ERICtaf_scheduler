angular
    .module('app.core')
    .config(['usSpinnerConfigProvider', function(usSpinnerConfigProvider) {
        usSpinnerConfigProvider.setDefaults({
            color: '#333333',
            radius: 30,
            width: 8,
            length: 16
        });
    }])
    .config(function(toastrConfig) {
        angular.extend(toastrConfig, {
            timeOut: 1500,
            preventOpenDuplicates: true
        });
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push([
            '$injector',
            function($injector) {
                return $injector.get('AuthInterceptor');
            }
        ]);
    })
    .config(['$localStorageProvider', function($localStorageProvider) {
        $localStorageProvider.setKeyPrefix('TafScheduler_');
    }]);
