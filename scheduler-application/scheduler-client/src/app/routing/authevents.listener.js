angular
    .module('app.routing')
    .run(AuthEventsListener);

function AuthEventsListener($state, $rootScope, AUTH_EVENTS) {
    $rootScope.$on(AUTH_EVENTS.loginSuccess, function(event) {
        event.preventDefault();
        $state.go('schedules');
    });
    $rootScope.$on(AUTH_EVENTS.logoutSuccess, function(event) {
        event.preventDefault();
        $state.go('login');
    });
}
