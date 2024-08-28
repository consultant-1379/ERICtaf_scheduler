angular
    .module('app.schedules')
    .controller('DeleteModalController', DeleteModalController);

function DeleteModalController($modalInstance, schedule) {
    var vm = this;

    vm.schedule = schedule;
    vm.deleteSchedule = deleteSchedule;
    vm.cancelAction = cancelAction;

    function deleteSchedule() {
        $modalInstance.close();
    }

    function cancelAction() {
        $modalInstance.dismiss('cancel');
    }

}
