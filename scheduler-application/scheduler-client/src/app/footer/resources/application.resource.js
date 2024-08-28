angular
    .module('app.footer')
    .factory('Application', Application);

function Application($resource, API_ROOT) {
    return $resource(API_ROOT + '/application', {}, {
        get: {method: 'get'}
    });
}

