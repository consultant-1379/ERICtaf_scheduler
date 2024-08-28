angular
    .module('app.schedules')
    .service('SuitesService', SuitesService);

function SuitesService(Suite) {
    this.getByTestware = getByTestware;

    function getByTestware(testware) {
        return Suite.get({id: testware.id}).$promise;
    }
}
