angular
    .module('app.schedules')
    .service('TestwareService', TestwareService);

function TestwareService(Testware) {
    this.cancelRequest = cancelRequest;
    this.getTestwareListBySchedule = getTestwareListBySchedule;
    this.getTestwareListByScheduleXml = getTestwareListByScheduleXml;
    this.getLatestTestware = getLatestTestware;

    function cancelRequest(promise) {
        if (promise && promise.abort) {
            promise.abort();
        }
    }

    function getTestwareListBySchedule(productName, dropName, scheduleId) {
        return Testware.getTestwareListBySchedule(productName, dropName, scheduleId);
    }

    function getTestwareListByScheduleXml(productName, dropName, scheduleXml) {
        return Testware.getTestwareListByScheduleXml(productName, dropName, scheduleXml);
    }

    function getLatestTestware(scheduleId) {
        return Testware.getLatestTestware(scheduleId);
    }

}
