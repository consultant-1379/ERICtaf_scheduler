angular
    .module('app.core')
    .constant('API_ROOT', '/api')
    .constant('AUTH_EVENTS', {
        loginSuccess: 'auth-login-success',
        loginFailed: 'auth-login-failed',
        logoutSuccess: 'auth-logout-success',
        sessionTimeout: 'auth-session-timeout',
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized'
    })
    .constant('APP_EVENTS', {
        reloadModels: 'reload-models'
    })
    .constant('EDITOR_EVENTS', {
        clearEditors: 'clear-editors'
    });
