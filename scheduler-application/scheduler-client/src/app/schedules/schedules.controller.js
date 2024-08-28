angular
    .module('app.schedules')
    .controller('schedulesController', schedulesController);

function schedulesController($scope, logger, ScheduleService, usSpinnerService, TeamsService, Session,
                             DROP_SELECTOR_EVENTS, APP_EVENTS, $uibModal, MODE_CONSTANTS, $localStorage) {
    var vm = this,
        schedulesRequestDone = true,
        spinnerName = 'schedules-spinner',
        storage = $localStorage;

    vm.scheduleList = [];
    vm.kgbSchedulesList = [];
    vm.selectedDrop = null;
    vm.selectedTeam = null;
    vm.teams = [];

    vm.clearTable = clearTable;
    vm.showConfirmDeleteDialog = showConfirmDeleteDialog;
    vm.showSpinner = showSpinner;
    vm.filterByTeam = filterByTeam;
    vm.hasRole = Session.hasRole;
    vm.refreshValidationStatus = refreshValidationStatus;
    vm.onTabSelect = onTabSelect;
    vm.showDropSelector = true;

    storage.$default({
        mode: MODE_CONSTANTS.normal
    });

    $scope.$on(DROP_SELECTOR_EVENTS.dropSelected, onDropChange);
    $scope.$on(DROP_SELECTOR_EVENTS.clearFilterResults, clearTable);

    // Need to reload states after success login.
    $scope.$on(APP_EVENTS.reloadModels, reloadModels);

    activate();

    function activate() {
        onTabSelect(MODE_CONSTANTS.normal);
    }

    function onTabSelect(mode) {
        storage.mode = mode;
        showDropSelector();
    }

    function showDropSelector() {
        vm.showDropSelector = storage.mode === MODE_CONSTANTS.normal ? true : false;
    }

    function reloadModels() {
        vm.selectedDrop = null;
    }

    function onDropChange(event, drop) {
        if (!angular.equals(vm.selectedDrop, drop)) {
            vm.selectedDrop = drop;
            if (vm.selectedDrop !== null) {
                getAllTeams();
                getScheduleList();
            }
        }
    }

    function getAllTeams() {
        TeamsService.getAllTeams().then(function(res) {
            vm.teams = res;
        });
    }

    function getScheduleList() {
        if (!vm.selectedDrop) {
            return;
        }
        startSpinner();
        ScheduleService.getByDrop(vm.selectedDrop.id).then(function(res) {
            vm.scheduleList = res;
        }).finally(function() {
            stopSpinner();
        });
        getKgbSchedulesList();
    }

    function getKgbSchedulesList() {
        ScheduleService.getKgbSchedules().then(function(res) {
            vm.kgbSchedulesList = res;
        });
    }

    function clearTable() {
        vm.scheduleList = [];
    }

    function showConfirmDeleteDialog(schedule) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/schedules/delete-modal/delete-modal.html',
            controller: 'DeleteModalController',
            controllerAs: 'vm',
            size: 'sm',
            resolve: {
                schedule: function() {
                    return schedule;
                }
            }
        });

        modalInstance.result.then(function() {
            deleteSchedule(schedule);
        }, function() {
            modalInstance.close();
            logger.info('Modal dismissed at: ', new Date());
        });
    }

    function deleteSchedule(schedule) {
        ScheduleService.deleteSchedule(schedule)
            .then(function(schedule) {
                logger.successWithToast('Schedule "' + schedule.name + '.xml" was deleted successfully.');
                getScheduleList();
            }, function(schedule) {
                logger.errorWithToast('Failed to delete schedule.', schedule);
            });
    }

    function startSpinner() {
        schedulesRequestDone = false;
        usSpinnerService.spin(spinnerName);
    }

    function stopSpinner() {
        schedulesRequestDone = true;
        usSpinnerService.stop(spinnerName);
    }

    function showSpinner() {
        return !schedulesRequestDone;
    }

    function filterByTeam(schedule) {
        if (vm.selectedTeam !== null) {
            return (schedule.team === vm.selectedTeam.name);
        } else {
            return true;
        }
    }

    function refreshValidationStatus() {
        startSpinner();
        ScheduleService.getByDrop(vm.selectedDrop.id).then(function(res) {
            vm.scheduleList = res;
            checkForUpdateToValidationStatus();
        });
    }

    function checkForUpdateToValidationStatus() {
        vm.scheduleList.forEach(function(schedule) {
            if (schedule.valid === false) {
                ScheduleService.validate(vm.selectedDrop, schedule.name,
                    schedule.type, schedule.xmlContent)
                    .then(function() {
                        schedule.valid = true;
                        updateScheduleValidationStatus(schedule);
                    }, function() {
                        logger.info('Schedule is still not validated for ', schedule.name);
                    });
            }
        });
        stopSpinner();
    }

    function updateScheduleValidationStatus(schedule) {
        ScheduleService.updateSchedule(schedule)
            .then(function(schedule) {
                logger.info('Schedule has been updated and is now valid for ', schedule.name);
            }, function(schedule) {
                logger.info('Failed to update schedule validataion status for ', schedule.name);
            });
    }

}
