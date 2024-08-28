angular
    .module('app.login')
    .controller('loginController', loginController);

function loginController($rootScope, AuthService, AUTH_EVENTS) {
    var vm = this;

    checkAuthentication();

    vm.credentials = {username: undefined, password: undefined}; //undefined for validation
    vm.errorMessages = {
        credentials: false,
        unexpected: false,
        requiredCredentials: false,
        requiredUsername: false,
        requiredPassword: false
    };

    vm.isLoginPage = true;
    vm.loginSubmit = loginSubmit;
    vm.resetForm = resetForm;
    vm.clearLoginError = clearLoginError;
    vm.hasErrors = hasErrors;
    vm.usernameHasError = usernameHasError;
    vm.passwordHasError = passwordHasError;

    function loginSubmit(isValid) {
        vm.loginSubmitted = true;
        if (isValid) {
            AuthService.login(vm.credentials)
                .then(loginSuccessful, loginUnsuccessful);
        } else {
            vm.errorMessages.requiredUsername = vm.credentials.username === undefined;
            vm.errorMessages.requiredPassword = vm.credentials.password === undefined;
            vm.errorMessages.requiredCredentials =
                vm.errorMessages.requiredUsername && vm.errorMessages.requiredPassword;
        }
    }

    function loginSuccessful() {
        vm.resetForm();
        $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
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

    function checkAuthentication() {
        AuthService.checkCurrentUser().then(function(res) {
            if (res.data.authenticated) {
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
            }
        });
    }
}
