angular
    .module('app.core')
    .factory('AuthInterceptor', AuthInterceptor);

function AuthInterceptor($rootScope, $q, AUTH_EVENTS) {
    return {
        responseError: returnError
    };

    function returnError(response) {
        $rootScope.$broadcast({
            401: AUTH_EVENTS.notAuthenticated,
            403: AUTH_EVENTS.notAuthorized,
            419: AUTH_EVENTS.sessionTimeout,
            440: AUTH_EVENTS.sessionTimeout
        }[response.status], response);
        return $q.reject(response);
    }
}
