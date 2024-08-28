angular
    .module('app.core')
    .service('Session', SessionService);

function SessionService(logger) {
    var session = this;

    session.create = create;
    session.destroy = destroy;
    session.hasRole = hasRole;

    function init() {
        session.name = null;
        session.authenticated = false;
        session.roles = [];
    }

    init();

    function create(name, authenticated, roles) {
        session.name = name;
        session.authenticated = authenticated;
        session.roles = roles;
        logger.info('Session created', session);
    }

    function destroy() {
        session.name = null;
        session.authenticated = false;
        session.roles = [];
        logger.info('Session destroyed');
    }

    function hasRole(role) {
        return session.roles.indexOf(role) > -1;
    }
}
