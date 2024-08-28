angular
    .module('app.dropselector')
    .directive('dropselector', function() {
        return {
            templateUrl: 'app/dropselector/dropselector.html',
            scope: {
                disableControls: '=',
                inModal: '='
            },
            bindToController: true,
            controller: DropSelector,
            controllerAs: 'vm'
        };
    });

function DropSelector($rootScope, $scope, $attrs, $state, $stateParams, DropService, DROP_SELECTOR_EVENTS, APP_EVENTS,
                      logger) {
    var vm = this,
        latestRequest = null,
        initFinished = false,
        dropsCleared = false;

    vm.myState = $state;

    vm.products = DropService.getProductsList();
    vm.drops = DropService.getDropsList();

    vm.selectedProduct = DropService.getSelectedProduct();
    vm.selectedDrop = DropService.getSelectedDrop();

    vm.onProductChange = onProductChange;
    vm.onDropChange = onDropChange;
    vm.clearFilterResults = clearFilterResults;
    vm.clearDrops = clearDrops;

    $scope.$watch('vm.selectedProduct', onSelectedProductChange);
    $scope.$watch('vm.selectedDrop', onSelectedDropChange);

    $scope.$on('$destroy', cancelRequest);
    $scope.$on('$stateChangeStart', stateChangeStart);

    // Need to reload states after success login.
    $scope.$on(APP_EVENTS.reloadModels, reloadModels);

    function reloadModels() {
        vm.selectedDrop = null;
        activate();
    }

    function stateChangeStart(event, toState, toParams, fromState) {
        if (toState.name !== fromState.name) {
            return;
        }
        event.preventDefault();

        if (!(_.isUndefined(toParams.product)) && !(_.isUndefined(toParams.product) || _.isUndefined(toParams.drop))) {
            initFinished = false;
            initDrops(toParams);
        }
    }

    activate();

    function activate() {
        if (!vm.selectedDrop) {
            DropService.setStateName(vm.myState.current.name);
            initDrops($stateParams);
        } else {
            initFinished = true;
            var broadCastMsg =
                $attrs.inModal ? DROP_SELECTOR_EVENTS.dropSelectedInModal : DROP_SELECTOR_EVENTS.dropSelected;
            $rootScope.$broadcast(broadCastMsg, vm.selectedDrop);

            if (!DropService.isSameState(vm.myState.current.name)) {
                DropService.setStateName(vm.myState.current.name);
                _.defer(function() {
                    changePageState(
                        vm.selectedProduct ? vm.selectedProduct.name : undefined,
                        vm.selectedDrop ? vm.selectedDrop.name : undefined
                    );
                    initDrops({
                        product: vm.selectedProduct ? vm.selectedProduct.name : undefined,
                        drop: vm.selectedDrop ? vm.selectedDrop.name : undefined
                    });
                });
            }
        }
    }

    function onSelectedProductChange(productNew, productOld) {
        if (initFinished && !angular.equals(productOld, productNew)) {
            logger.info('Selected product changed', productNew);
            vm.clearDrops();
            vm.onProductChange();
        }
    }

    function onSelectedDropChange(dropNew, dropOld) {
        if (initFinished && !angular.equals(dropOld, dropNew) && !dropsCleared) {
            logger.info('Selected drop changed', dropNew);
            vm.onDropChange();
        } else {
            dropsCleared = false;
        }
    }

    function initDrops(stateParams) {
        vm.selectedProduct = DropService.getProductByName(stateParams.product) || vm.selectedProduct;

        DropService.setSelectedProduct(vm.selectedProduct);
        latestRequest = DropService.requestDrops(vm.selectedProduct.name, function(res) {
            vm.drops = res;
            DropService.setDropsByKey(vm.selectedProduct.name, vm.drops);
            vm.selectedDrop = DropService.getDropByName(stateParams.drop) || res[0];
            initFinished = true;
        });
    }

    function onProductChange() {
        clearByProductChange();
        if (!$attrs.inModal) {
            DropService.setSelectedProduct(vm.selectedProduct);
        }

        cancelRequest();
        latestRequest = DropService.requestDrops(vm.selectedProduct.name, function(res) {
            vm.drops = res;
            vm.selectedDrop = res[0];
            DropService.setDropsByKey(vm.selectedProduct.name, vm.drops);
            onDropChange();
        });
    }

    function onDropChange() {
        if (!vm.selectedDrop) {
            return;
        }
        if (!$attrs.inModal) {
            DropService.setSelectedDrop(vm.selectedDrop);
        }

        cancelRequest();
        clearFilterResults();

        changePageState(
            vm.selectedProduct ? vm.selectedProduct.name : undefined,
            vm.selectedDrop ? vm.selectedDrop.name : undefined
        );
        var broadCastMsg = //TODO: find better way
            $attrs.inModal ? DROP_SELECTOR_EVENTS.dropSelectedInModal : DROP_SELECTOR_EVENTS.dropSelected;
        $rootScope.$broadcast(broadCastMsg, vm.selectedDrop);
    }

    function clearFilterResults() {
        $rootScope.$broadcast(DROP_SELECTOR_EVENTS.clearFilterResults);
    }

    function clearDrops() {
        dropsCleared = true;
        vm.drops = [];
        vm.selectedDrop = null;
        DropService.setSelectedDrop(vm.selectedDrop);
    }

    function clearByProductChange() {
        vm.drops = [];
        vm.selectedDrop = null;
    }

    function cancelRequest() {
        DropService.cancelRequest(latestRequest);
        latestRequest = null;
    }

    function changePageState(productName, dropName) {
        vm.myState.go(
            vm.myState.current.name,
            {product: productName, drop: dropName},
            {notify: false}
        );
    }

}
