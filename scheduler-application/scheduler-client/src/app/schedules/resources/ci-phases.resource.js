angular
    .module('app.schedules')
    .factory('CiPhases', CiPhases);

function CiPhases($http, $q, API_ROOT) {

    return {
        getPhases: getPhases
    };

    function getPhases() {
        var deferredAbort = $q.defer();

        var request = $http({
            method: 'get',
            url: API_ROOT + '/schedules/types',
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                var buildPhases = [];

                angular.forEach(response.data, function(value, index) {
                    var type = {};
                    type.id = index;
                    type.name = value;
                    buildPhases.push(type);
                });
                return buildPhases;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(
            function() {
                promise.abort = angular.noop;
                deferredAbort = request = promise = null;
            }
        );

        return promise;
    }

}
