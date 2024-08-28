angular
    .module('app.schedules')
    .factory('Suite', Suite);

function Suite($resource, API_ROOT) {
    return $resource(API_ROOT + '/testware/:id/suites', {id: '@id'}, {
        get: {method: 'get', isArray: true, timeout: 10000}
    });
}
