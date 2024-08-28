angular
    .module('app.schedules')
    .service('CiPhasesService', CiPhasesService);

function CiPhasesService(CiPhases) {
    this.getScheduleTypes = getScheduleTypes;
    this.getSelectedScheduleType = getSelectedScheduleType;
    var scheduleTypes = [];

    function getScheduleTypes(callback) {
        var buildPromise = CiPhases.getPhases();
        buildPromise.then(callback);
        scheduleTypes = buildPromise;
        return buildPromise;
    }

    function getSelectedScheduleType() {
        return scheduleTypes[0];
    }
}
