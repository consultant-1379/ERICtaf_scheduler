angular
    .module('app.schedules')
    .controller('SuitesModalController', SuitesModalController);

function SuitesModalController($modalInstance, logger, usSpinnerService, SuitesService, testware) {
    var vm = this,
        spinnerName = 'suitesModal-spinner';

    vm.suites = [];
    vm.checkedSuites = [];
    vm.selectedSuites = [];
    vm.showSuitesLoadSpinner = false;
    vm.disableConfirmButton = true;

    vm.isSelectedSuite = isSelectedSuite;
    vm.suitesChanged = suitesChanged;
    vm.confirmAction = confirmAction;
    vm.cancelAction = cancelAction;

    activate();

    function activate() {
        if (testware.existingSuites.length > 0) {
            vm.suites = testware.existingSuites.slice();
            vm.selectedSuites = testware.selectedSuites.slice();
            vm.checkedSuites = testware.existingSuites.slice();

            suitesChanged();
            return;
        }

        startSpinner();
        SuitesService.getByTestware(testware).then(function(data) {
            vm.suites = data;
            vm.checkedSuites = data.slice();
            suitesChanged();
        }, function(data) {
            logger.errorWithToast('Suites load error!', data);
        }).finally(function() {
            stopSpinner();
        });
    }

    function isSelectedSuite(suiteName) {
        return _.contains(vm.selectedSuites, suiteName);
    }

    function suitesChanged() {
        vm.disableConfirmButton = vm.checkedSuites.length < 1;
    }

    function confirmAction() {
        $modalInstance.close({
            testware: testware,
            testwareSuites: vm.suites,
            selectedSuites: vm.checkedSuites
        });
    }

    function cancelAction() {
        $modalInstance.dismiss('cancel');
    }

    function startSpinner() {
        vm.showSuitesLoadSpinner = true;
        usSpinnerService.spin(spinnerName);
    }

    function stopSpinner() {
        vm.showSuitesLoadSpinner = false;
        usSpinnerService.stop(spinnerName);
    }
}
