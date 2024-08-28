angular
    .module('app.schedules')
    .controller('SchedulesModalController', SchedulesModalController);

function SchedulesModalController($scope, $modalInstance, ScheduleService, DROP_SELECTOR_EVENTS, selectedSchedules) {
    var vm = this;

    vm.disableConfirmButton = true;
    vm.scheduleList = [];
    vm.selectedDrop = null;
    vm.selectedSchedules = [];

    vm.addOrRemoveSchedule = addOrRemoveSchedule;
    vm.confirmAction = confirmAction;
    vm.cancelAction = cancelAction;

    $scope.$on(DROP_SELECTOR_EVENTS.dropSelectedInModal, onDropChange);

    activate();

    function activate() {
        vm.selectedSchedules = selectedSchedules;
    }

    function onDropChange(event, drop) {
        if (!angular.equals(vm.selectedDrop, drop)) {
            vm.selectedDrop = drop;
            if (vm.selectedDrop !== null) {
                getScheduleList();
            }
        }
    }

    function getScheduleList() {
        if (!vm.selectedDrop) {
            return;
        }
        ScheduleService.getApprovedScheduleSummaries(vm.selectedDrop.productName, vm.selectedDrop.name)
            .then(function(res) {
            vm.scheduleList = res;
            setChecked();
        });
    }

    function setChecked() {
        vm.selectedSchedules.forEach(function(included) {
            vm.scheduleList.forEach(function(schedule) {
                if (included.id === schedule.id) {
                    schedule.included = true;
                }
            });
        });
    }

    function addOrRemoveSchedule(schedule) {
        schedule.included = !schedule.included;
        if (schedule.included) {
            addSchedule(schedule);
        } else {
            removeSchedule(schedule);
        }
    }

    function addSchedule(schedule) {
        vm.selectedSchedules.push(schedule);
        vm.disableConfirmButton = false;
    }

    function removeSchedule(schedule) {
        var toBeRemoved = _.findWhere(vm.selectedSchedules, {id: schedule.id});
        vm.selectedSchedules = _.without(vm.selectedSchedules, toBeRemoved);
        if (vm.selectedSchedules.length < 1) {
            vm.disableConfirmButton = true;
        }
    }

    function confirmAction() {
        $modalInstance.close({
            selectedSchedules: vm.selectedSchedules
        });
    }

    function cancelAction() {
        $modalInstance.dismiss('cancel');
    }

}
