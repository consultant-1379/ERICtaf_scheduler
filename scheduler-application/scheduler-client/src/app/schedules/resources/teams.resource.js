angular
    .module('app.schedules')
    .factory('Teams', Teams);

function Teams($http, $q, API_ROOT) {

    return {
        getAllTeams: getAllTeams
    };

    function getAllTeams() {
        var deferredAbort = $q.defer();

        var request = $http({
            method: 'get',
            url: API_ROOT + '/schedules/teams',
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                var teams = [];

                angular.forEach(response.data, function(value) {
                    var team = {};
                    team.name = value;
                    teams.push(team);
                });
                return teams;
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
