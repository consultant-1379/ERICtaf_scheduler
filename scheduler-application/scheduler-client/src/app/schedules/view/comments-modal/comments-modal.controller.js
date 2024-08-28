angular
    .module('app.schedules')
    .controller('CommentsModalController', CommentsModalController);

function CommentsModalController($modalInstance, logger, CommentsService, usSpinnerService, schedule, Session) {
    var vm = this,
        addCommentRequestSent = false,
        commentsRequestDone = true,
        spinnerName = 'commentsModal-spinner';

    vm.comment = '';

    vm.isAddCommentEnabled = isAddCommentEnabled;
    vm.addCommentAction = addCommentAction;
    vm.closeAction = closeAction;
    vm.showSpinner = showSpinner;
    vm.hasRole = Session.hasRole;
    vm.schedule = schedule;

    activate();

    function activate() {
        populateComments();
    }

    function isAddCommentEnabled() {
        return vm.comment.length > 0 && !addCommentRequestSent;
    }

    function addCommentAction() {
        if (!vm.isAddCommentEnabled()) {
            return;
        }
        addCommentRequestSent = true;
        CommentsService.addComment(schedule.id, schedule.version, vm.comment).then(function() {
            vm.comment = '';
            populateComments();
        }, function(errorObject) {
            logger.errorWithToast('Failed to add a comment.', errorObject);
        }).finally(function() {
            addCommentRequestSent = false;
        });
    }

    function populateComments() {
        startSpinner();
        CommentsService.getAllComments(schedule.id).then(function(comments) {
            vm.commentsList = comments;
        }).finally(function() {
            stopSpinner();
        });
    }

    function closeAction() {
        $modalInstance.dismiss('cancel');
    }

    function startSpinner() {
        commentsRequestDone = false;
        usSpinnerService.spin(spinnerName);
    }

    function stopSpinner() {
        commentsRequestDone = true;
        usSpinnerService.stop(spinnerName);
    }

    function showSpinner() {
        return !commentsRequestDone;
    }

}
