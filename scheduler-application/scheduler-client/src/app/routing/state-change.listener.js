angular
    .module('app.routing')
    .run(StateChangeListener);

function StateChangeListener($state, $rootScope, AUTH_EVENTS, AuthService, EDITOR_EVENTS) {
    $rootScope.$on('$stateChangeStart', function(event, next) {
        $rootScope.$broadcast(EDITOR_EVENTS.clearEditors);
        if (next.redirectTo) {
            event.preventDefault();
            $state.go(next.redirectTo);
        }
        switch (next.name) {
            case 'login':
                if (AuthService.isAuthenticated()) {
                    event.preventDefault();
                    $state.go('schedules');
                }
                break;
            default:
                if (!AuthService.isAuthenticated()) {
                    checkAuthentication(event);
                }
        }
    });

    function checkAuthentication(event) {
        AuthService.checkCurrentUser().then(function(res) {
            if (!res.data.authenticated) {
                $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                event.preventDefault();
            }
        });
    }
}
