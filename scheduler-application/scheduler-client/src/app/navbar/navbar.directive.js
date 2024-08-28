angular
    .module('app.navbar')
    .directive('navbar', function() {
        return {
            templateUrl: 'app/navbar/navbar.html',
            scope: {},
            controller: NavigationBar,
            controllerAs: 'vm',
            replace: true
        };
    });

function NavigationBar($rootScope, $state, AuthService, AUTH_EVENTS) {
    var vm = this;
    vm.isAuthenticated = AuthService.isAuthenticated;
    vm.signOut = signOut;
    vm.goToMain = goToMain;
    vm.isOnOpenPage = isOnOpenPage;

    function signOut() {
        AuthService.signOut()
            .then(function() {
                $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
            });
    }

    function goToMain() {
        $state.go('schedules');
    }

    function isOnOpenPage() {
        if ($state.current.data) {
            return !$state.current.data.loginRequired;
        }
    }
}
