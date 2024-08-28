angular
    .module('app.dropselector')
    .factory('Products', Products);

function Products($http, $q, API_ROOT) {

    return {
        getDrops: getDrops
    };

    function getDrops(productId) {
        var deferredAbort = $q.defer();

        var request = $http({
            method: 'get',
            url: API_ROOT + '/products/' + productId + '/drops',
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                return response.data;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(function() {
            promise.abort = angular.noop;
            deferredAbort = request = promise = null;
        });

        return promise;
    }

}
