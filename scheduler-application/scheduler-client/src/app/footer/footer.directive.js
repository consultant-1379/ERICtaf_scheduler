angular
    .module('app.footer')
    .directive('footer', function() {
        return {
            templateUrl: 'app/footer/footer.html',
            scope: {},
            controller: Footer,
            controllerAs: 'vm',
            replace: true
        };
    });

function Footer(Application, FooterService) {
    var vm = this;

    vm.date = new Date();
    vm.application = Application.get();
    vm.href = null;

    vm.onSwaggerUrlClick = onSwaggerUrlClick;

    function onSwaggerUrlClick() {
        var updatedHref = FooterService.getSwaggerUrl();
        if (updatedHref != null) {
            vm.href = updatedHref;
        }
    }
}
