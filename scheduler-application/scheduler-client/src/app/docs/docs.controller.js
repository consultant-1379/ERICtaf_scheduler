angular
    .module('app.docs')
    .controller('DocsController', DocsController);

function DocsController($sce) {
    var vm = this;

    vm.schedulesList = $sce.trustAsResourceUrl('images/schedules-list.png');
    vm.scheduleView = $sce.trustAsResourceUrl('images/schedule-view.png');
    vm.scheduleEdit = $sce.trustAsResourceUrl('images/schedule-edit.png');
    vm.scheduleEditInclude = $sce.trustAsResourceUrl('images/schedule-include.png');
    vm.validationErrors = $sce.trustAsResourceUrl('images/validation-errors.png');
    vm.selectedTestware = $sce.trustAsResourceUrl('images/selected-testware.png');
    vm.suitesPopup = $sce.trustAsResourceUrl('images/suites-popup.png');
    vm.sendForApproval = $sce.trustAsResourceUrl('images/send-for-approval.png');
    vm.reviewEmail = $sce.trustAsResourceUrl('images/review-email.png');
    vm.scheduleReview = $sce.trustAsResourceUrl('images/schedule-review.png');

}
