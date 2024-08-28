angular
    .module('app.schedules')
    .service('CommentsService', CommentsService);

function CommentsService(Comment) {
    var vm = this;

    vm.getAllComments = getAllComments;
    vm.addComment = addComment;

    function getAllComments(scheduleId) {
        return Comment.query({scheduleId: scheduleId}).$promise;
    }

    function addComment(scheduleId, version, comment) {
        return Comment.create({scheduleId: scheduleId, version: version}, comment).$promise;
    }

}
