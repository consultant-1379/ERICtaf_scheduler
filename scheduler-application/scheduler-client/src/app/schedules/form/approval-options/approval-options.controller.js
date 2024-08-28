angular
    .module('app.schedules')
    .controller('ApprovalOptionsController', ApprovalOptionsController);

function ApprovalOptionsController(isValidSchedule, $modalInstance, ScheduleApprovalService, logger, $q) {
    var vm = this;

    vm.cancelAction = cancelAction;
    vm.saveSchedule = saveSchedule;
    vm.sendAndSaveSchedule = sendAndSaveSchedule;
    vm.getApprovers = getApprovers;
    vm.hasSelectedApprovers = hasSelectedApprovers;

    vm.typingReviewer = '';
    vm.selected = [];
    vm.users = [];
    vm.confirmScheduleSent = false;
    vm.approvalStatus = 'UNAPPROVED';
    vm.autoCompleteStrings = [];
    vm.isValidSchedule = isValidSchedule;

    function sendAndSaveSchedule() {
        vm.confirmScheduleSent = true;
        vm.approvalStatus = 'PENDING';
        vm.selectedUsers = [];
        vm.usersToValidate = [];

        if (vm.selected != null) {
            vm.selected.forEach(function(externalId) {
                vm.usersToValidate.push(externalId.text.trim());
                vm.selectedUser = {
                    userId: null,
                    name: null,
                    email: null
                };

                if (externalId.text.indexOf('@') !== -1) {
                    vm.selectedUser.email = externalId.text;
                } else {
                    vm.selectedUser.userId = externalId.text;
                }
                vm.selectedUsers.push(vm.selectedUser);
            });
        }

        ScheduleApprovalService.validateReviewers(vm.usersToValidate)
            .then(function() {
                    logger.successWithToast('Reviewers are Valid');
                    saveSchedule();
                },
                function(data) {
                    var invalidUsers = data.invalidUsers.join(', ');
                    logger.errorWithToast('The following reviewers are invalid: ' + invalidUsers, invalidUsers);
                    return $q.reject('Validation failed');
                });
    }

    function saveSchedule() {
        $modalInstance.close({
            reviewers: vm.selectedUsers,
            approvalStatus: vm.approvalStatus
        });
        if (vm.confirmScheduleSent) {
            var selectedApprovers = _.pluck(vm.selected, 'text').join(', ');
            logger.successWithToast('Schedule has been sent for approval to: ' + selectedApprovers, vm.selected);
        }
    }

    function cancelAction() {
        $modalInstance.dismiss('cancel');
    }

    function getApprovers(input) {
        vm.usersForAutocomplete = [];

        ScheduleApprovalService.getAvailableScheduleApprovers(input)
            .then(function(res) {
                vm.reviewers = res;
                setAutoCompleteStrings(vm.reviewers);
            });
        return vm.autoCompleteStrings;
    }

    function setAutoCompleteStrings(reviewers) {
        vm.autoCompleteStrings = [];
        reviewers.forEach(function(reviewer) {
            vm.autoCompleteStrings.push(reviewer.userId);
        });
    }

    function hasSelectedApprovers() {
        return vm.typingReviewer.length > 0 || vm.selected.length > 0;
    }
}
