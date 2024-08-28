angular
    .module('app.schedules')
    .factory('ApprovedUsers', ApprovedUsers);

function ApprovedUsers($http, $q, API_ROOT) {

    return {
        getUsers: getUsers
    };

    function getUsers(input) {
        var deferredAbort = $q.defer();

        var request = $http({
            method: 'get',
            url: API_ROOT + '/users?name=' + input,
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                var users = [];

                angular.forEach(response.data, function(value) {
                    var user = {};
                    user.userId = value.userId;
                    user.name = value.name;
                    user.email = value.email;
                    users.push(user);
                });
                return users;
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
