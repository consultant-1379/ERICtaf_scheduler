angular.module('app.schedules')
    .controller('viewScheduleController', ViewScheduleController);

function ViewScheduleController($state, $stateParams, $uibModal, logger, ScheduleService, ScheduleEditorService,
                                Session) {
    var vm = this;

    vm.selectedVersion = undefined;
    vm.modelLoaded = false;
    vm.xmlEditorHiddenContent = null; // needed for acceptance tests
    vm.showReviewBlock = $state.current.data.showReview;
    vm.approvalStatus = '';
    vm.approvalMessage = '';
    vm.disableApproveButton = true;

    vm.approvalMessageIsPresent = approvalMessageIsPresent;
    vm.approveSchedule = approveSchedule;
    vm.rejectSchedule = rejectSchedule;
    vm.cancelAction = goToScheduleView;

    vm.isApprovedOrRejected = isApprovedOrRejected;
    vm.isUnapprovedOrPending = isUnapprovedOrPending;

    vm.xmlEditorLoaded = ScheduleEditorService.initEditor;
    vm.deleteSchedule = deleteSchedule;
    vm.onVersionChange = onVersionChange;
    vm.showReviewCommentsDialog = showReviewCommentsDialog;
    vm.approveOrReject = approveOrReject;
    vm.revokeApproval = revokeApproval;
    vm.getXmlRestCallUrl = getXmlRestCallUrl;
    vm.hasRole = Session.hasRole;
    vm.displayProductAndDrop = true;

    activate();

    function activate() {
        vm.selectedVersion = $stateParams.version;
        if (_.isUndefined(vm.selectedVersion)) {
            ScheduleService.getLatest($stateParams.id).then(onFetchSuccess, onFetchError);
        } else {
            ScheduleService.getVersion($stateParams.id, vm.selectedVersion).then(onFetchSuccess, onFetchError);
        }
    }

    function isKgb() {
        vm.displayProductAndDrop = vm.schedule.type.name !== 'KGB+N' ? true : false;
    }

    function deleteSchedule() {
        if (!vm.schedule) {
            logger.errorWithToast('Failed to delete schedule. Schedule is not found.');
            return;
        }
        if (window.confirm('Are you sure you want to delete the schedule?')) {
            ScheduleService.deleteSchedule(vm.schedule)
                .then(function(schedule) {
                    logger.successWithToast('Schedule "' + schedule.name + '.xml" is deleted successfully.');
                    $state.go('schedules');
                }, function(schedule) {
                    logger.errorWithToast('Failed to delete schedule.', schedule);
                });
        }
    }

    function onVersionChange() {
        goToScheduleViewByVersion($stateParams.id, vm.selectedVersion);
    }

    function onFetchSuccess(res) {
        vm.schedule = res;
        isKgb();
        ScheduleEditorService.setContent(vm.schedule.xmlContent);
        ScheduleEditorService.removeSelection();
        vm.xmlEditorHiddenContent = vm.schedule.xmlContent;
        vm.selectedVersion = vm.schedule.version;
        vm.modelLoaded = true;
        vm.approvalStatus = vm.schedule.approvalStatus;
        if (vm.schedule.valid === true) {
            vm.validated = 'VALIDATED';
        } else {
            vm.validated = 'UNVALIDATED';
        }
    }

    function onFetchError() {
        ScheduleEditorService.setContent('');
        vm.xmlEditorHiddenContent = '';
        logger.errorWithToast('Failed to load Schedule.', $stateParams.id);
    }

    function showReviewCommentsDialog() {
        $uibModal.open({
            animation: true,
            templateUrl: 'app/schedules/view/comments-modal/comments-modal.html',
            controller: 'CommentsModalController',
            controllerAs: 'vm',
            size: 'lg',
            resolve: {
                schedule: function() {
                    return vm.schedule;
                }
            }
        });
    }

    function approveOrReject() {
        ScheduleService.approveOrReject(vm.schedule)
            .then(function(schedule) {
                vm.schedule = schedule;
                logger.successWithToast('Schedule "' + schedule.name + '.xml" has been ' +
                    schedule.approvalStatus.toLowerCase());
                goToScheduleView();
            }, function(schedule) {
                vm.schedule.approvalStatus = vm.approvalStatus;
                logger.errorWithToast('Could not update ' + schedule.name + '.xml');
            });
    }

    function revokeApproval() {
        ScheduleService.revokeApproval($stateParams.id, vm.selectedVersion)
            .then(function(schedule) {
                vm.schedule = schedule;
                logger.infoWithToast('Schedule "' + schedule.name + '.xml" is no longer approved.');
                goToScheduleView();
            }, function(schedule) {
                vm.schedule.approvalStatus = vm.approvalStatus;
                logger.errorWithToast('Could not update ' + schedule.name + '.xml');
            });
    }

    function getXmlRestCallUrl() {
        var url = ScheduleService.getXmlRestCallUrl(
            vm.schedule.name, vm.schedule.drop.name, vm.selectedVersion
        );
        logger.successWithToast('Schedule url was saved to clipboard!', url);
        return url;
    }

    function isApprovedOrRejected() {
        return vm.schedule && _.indexOf(['APPROVED', 'REJECTED'], vm.schedule.approvalStatus) > -1;
    }

    function isUnapprovedOrPending() {
        return vm.schedule && _.indexOf(['UNAPPROVED', 'PENDING'], vm.schedule.approvalStatus) > -1;
    }

    function approvalMessageIsPresent() {
        return vm.approvalMessage.length > 0;
    }

    function rejectSchedule() {
        updateScheduleApprovalStatus('REJECTED');
    }

    function approveSchedule() {
        updateScheduleApprovalStatus('APPROVED');
    }

    function updateScheduleApprovalStatus(approvalStatus) {
        vm.schedule.approvalStatus = approvalStatus;
        vm.schedule.approvalMsg = vm.approvalMessage;
        approveOrReject();
    }

    function goToScheduleView() {
        $state.go('schedules-view', {id: vm.schedule.id, version: vm.selectedVersion});
    }

    function goToScheduleViewByVersion(scheduleId, version) {
        var pageName = 'schedules-review';
        if (!vm.showReviewBlock) {
            pageName = 'schedules-view';
        }
        $state.go(pageName, {id: scheduleId, version: version});
    }

}
