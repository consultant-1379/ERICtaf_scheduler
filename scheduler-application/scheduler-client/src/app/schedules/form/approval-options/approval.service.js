angular
    .module('app.schedules')
    .service('ScheduleApprovalService', ScheduleApprovalService);

function ScheduleApprovalService(ApprovedUsers, $http, API_ROOT, $q) {
    this.getAvailableScheduleApprovers = getAvailableScheduleApprovers;
    this.validateReviewers = validateReviewers;
    var users = [];

    function getAvailableScheduleApprovers(input, callback) {
        var buildPromise = ApprovedUsers.getUsers(input);
        buildPromise.then(callback);
        users = buildPromise;
        return users;
    }

    function validateReviewers(reviewers) {
        return $http.post(API_ROOT + '/users/validate', reviewers).then(
            function(result) {
                return result.data;
            },
            function(result) {
                return $q.reject(result.data);
            }
        );
    }
}
