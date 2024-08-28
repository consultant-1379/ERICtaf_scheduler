angular
    .module('app.schedules')
    .service('ScheduleService', ScheduleService);

function ScheduleService(Schedule, $http, API_ROOT, $q) {
    this.getLatest = getLatest;
    this.getByDrop = getByDrop;
    this.getApprovedScheduleSummaries = getApprovedScheduleSummaries;
    this.createSchedule = createSchedule;
    this.updateSchedule = updateSchedule;
    this.deleteSchedule = deleteSchedule;
    this.validateSchedules = validateSchedules;
    this.getVersion = getVersion;
    this.getSchedulesByIds = getSchedulesByIds;
    this.getXmlRestCallUrl = getXmlRestCallUrl;
    this.approveOrReject = approveOrReject;
    this.revokeApproval = revokeApproval;
    this.getAllTeams = getAllTeams;
    this.buildSchedule = buildSchedule;
    this.getKgbSchedules = getKgbSchedules;

    function getLatest(scheduleId) {
        return Schedule.get({id: scheduleId}).$promise;
    }

    function getVersion(scheduleId, scheduleVersion) {
        return Schedule.getVersion({id: scheduleId, version: scheduleVersion}).$promise;
    }

    function getSchedulesByIds(scheduleIds) {
        return Schedule.getSchedulesInXml({scheduleIds: scheduleIds}).$promise;
    }

    function getByDrop(dropId) {
        return Schedule.query({id: dropId}).$promise;
    }

    function getKgbSchedules() {
        return Schedule.getKgbSchedules().$promise;
    }

    function getApprovedScheduleSummaries(productName, dropName) {
        return Schedule.getSummaries({productName: productName, dropName: dropName}).$promise;
    }

    function getAllTeams() {
        return Schedule.getAllTeams().$promise;
    }

    function createSchedule(drop, scheduleName, scheduleType, scheduleXml, reviewers, approvalStatus, team, valid) {
        var schedule = buildSchedule(drop, null, scheduleName, scheduleType, scheduleXml, null,
                                     reviewers, approvalStatus, team, valid);

        return Schedule.create(schedule).$promise;
    }

    function updateSchedule(schedule) {
        return Schedule.update({id: schedule.id}, schedule).$promise;
    }

    function deleteSchedule(schedule) {
        return Schedule.delete(schedule).$promise;
    }

    function buildSchedule(drop, scheduleId, scheduleName, scheduleType, scheduleXml, version, reviewers,
                           approvalStatus, team, valid) {

        var schedule = new Schedule({drop: drop});
        schedule.id = scheduleId;
        schedule.name = scheduleName;
        schedule.type = scheduleType;
        schedule.xmlContent = scheduleXml;
        schedule.version = version;
        schedule.reviewers = [];
        if (reviewers) {
            schedule.reviewers = reviewers;
        }
        if (approvalStatus) {
            schedule.approvalStatus = approvalStatus;
        }
        if (team) {
            schedule.team = team;
        }
        if (valid) {
            schedule.valid = valid;
        }
        return schedule;
    }

    function validateSchedules(schedules) {
        return $http.post(API_ROOT + '/schedules/validation', schedules).then(
            function(result) {
                return result.data;
            },
            function(result) {
                return $q.reject(result.data);
            }
        );
    }

    function getXmlRestCallUrl(scheduleName, dropName, version) {
        var url = window.location.href;
        url = url.split('/#')[0];
        return url + API_ROOT + '/schedules/' + scheduleName + '/content?drop=' + dropName + '&version=' + version;
    }

    function approveOrReject(schedule) {
        return Schedule.approveOrReject({id: schedule.id}, schedule).$promise;
    }

    function revokeApproval(scheduleId, scheduleVersion) {
        return Schedule.revokeApproval({id: scheduleId, version: scheduleVersion}).$promise;
    }
}
