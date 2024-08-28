angular
    .module('app.core')
    .factory('AuthService', AuthService);

function AuthService($http, Session, API_ROOT, logger) {
    return {
        login: login,
        signOut: signOut,
        setAuthorized: setAuthorized,
        isAuthenticated: isAuthenticated,
        checkCurrentUser: checkCurrentUser
    };

    function login(credentials) {
        return $http
            .post(API_ROOT + '/login', credentials)
            .then(function(res) {
                logger.info('Login successful');
                Session.create(res.data.userId, res.data.authenticated, res.data.roles);
            });
    }

    function signOut() {
        return $http
            .delete(API_ROOT + '/login')
            .then(function() {
                logger.info('SignOut successful');
                Session.destroy();
            });
    }

    function setAuthorized(isAuthorized) {
        Session.authenticated = isAuthorized;
    }

    function isAuthenticated() {
        return Session.authenticated;
    }

    function checkCurrentUser() {
        return $http
            .get(API_ROOT + '/login')
            .then(function(res) {
                if (res.data.authenticated) {
                    logger.info('User is authenticated');
                    Session.create(res.data.userId, res.data.authenticated, res.data.roles);
                } else {
                    logger.info('User is not authenticated');
                }
                return res;
            });
    }
}
