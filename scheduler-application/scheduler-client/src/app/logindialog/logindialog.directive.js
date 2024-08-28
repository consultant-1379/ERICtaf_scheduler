angular
    .module('app.logindialog')
    .directive('loginDialog', function(AUTH_EVENTS, AuthService) {
        return {
            template: '<div ng-if="!vm.shouldBeHidden()" ng-include="\'app/login/login.html\'" class="login-Dialog">',
            link: function($scope) {
                var showDialog = function() {
                    AuthService.setAuthorized(false);
                };

                $scope.$on(AUTH_EVENTS.notAuthenticated, showDialog);
                $scope.$on(AUTH_EVENTS.sessionTimeout, showDialog);
            },
            controller: loginDialogController,
            controllerAs: 'vm'
        };
    });

// TODO: VOV: Need to think, how to use loginController. Source is mostly same.
function loginDialogController($rootScope, $state, AuthService, AUTH_EVENTS, APP_EVENTS) {
    var vm = this;

    vm.credentials = {username: undefined, password: undefined}; //undefined for validation
    vm.errorMessages = {
        credentials: false,
        unexpected: false,
        requiredCredentials: false,
        requiredUsername: false,
        requiredPassword: false
    };

    vm.loginSubmit = loginSubmit;
    vm.resetForm = resetForm;
    vm.clearLoginError = clearLoginError;
    vm.hasErrors = hasErrors;
    vm.usernameHasError = usernameHasError;
    vm.passwordHasError = passwordHasError;
    vm.shouldBeHidden = shouldBeHidden;

    function shouldBeHidden() {
        return AuthService.isAuthenticated() ||
            $state.current.name === 'login' || $state.current.name.includes('documentation');
    }

    function loginSubmit(isValid) {
        vm.loginSubmitted = true;
        if (isValid) {
            AuthService.login(vm.credentials).then(
                loginSuccessful,
                loginUnsuccessful
            );
        } else {
            vm.errorMessages.requiredUsername = vm.credentials.username === undefined;
            vm.errorMessages.requiredPassword = vm.credentials.password === undefined;
            vm.errorMessages.requiredCredentials =
                vm.errorMessages.requiredUsername && vm.errorMessages.requiredPassword;
        }
    }

    function loginSuccessful() {
        vm.resetForm();
        $rootScope.$broadcast(APP_EVENTS.reloadModels);
    }

    function loginUnsuccessful(res) {
        vm.loginError = true;
        if (res.status === 401) {
            vm.errorMessages.credentials = true;
        } else {
            vm.errorMessages.unexpected = true;
        }
        $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
    }

    function clearLoginError() {
        vm.loginError = false;
        vm.errorMessages.credentials = false;
        vm.errorMessages.unexpected = false;
        vm.errorMessages.requiredCredentials = false;
        vm.errorMessages.requiredUsername = false;
        vm.errorMessages.requiredPassword = false;
    }

    function resetForm() {
        vm.credentials = {};
        vm.loginSubmitted = false;
        vm.loginError = null;
    }

    function hasErrors() {
        return usernameHasError() || passwordHasError();
    }

    function usernameHasError() {
        return (vm.loginForm.username.$invalid || vm.loginError) && vm.loginSubmitted;
    }

    function passwordHasError() {
        return (vm.loginForm.password.$invalid || vm.loginError) && vm.loginSubmitted;
    }
}
