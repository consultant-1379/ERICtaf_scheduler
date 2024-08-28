angular
    .module('app.dropselector')
    .service('DropService', DropService);

function DropService(Products, debounce, $localStorage) {
    var products = [{name: 'CI'}, {name: 'ENM'}],
        drops = [],
        dropsHolder = {},
        storage = $localStorage,
        currentStateName = null;

    storage.$default({
        selectedProduct: products[1],
        selectedDrop: null
    });

    this.setStateName = setStateName;
    this.isSameState = isSameState;

    this.getProductByName = getProductByName;
    this.getDropByName = getDropByName;

    this.getProductsList = getProductsList;
    this.getDropsList = getDropsList;

    this.cancelRequest = cancelRequest;
    this.requestDrops = requestDrops;
    this.setDropsByKey = setDropsByKey;

    this.getSelectedProduct = getSelectedProduct;
    this.setSelectedProduct = setSelectedProduct;
    this.setSelectedDrop = setSelectedDrop;
    this.getSelectedDrop = getSelectedDrop;

    function setStateName(stateName) {
        currentStateName = stateName;
    }

    function isSameState(stateName) {
        return currentStateName === stateName;
    }

    function getProductByName(name) {
        return _.find(products, function(product) {
            return name === product.name;
        });
    }

    function getDropByName(name) {
        return _.find(drops, function(drop) {
            return name === drop.name;
        });
    }

    function getProductsList() {
        return products;
    }

    function getDropsList() {
        return drops;
    }

    function cancelRequest(promise) {
        if (promise && promise.abort) {
            promise.abort();
        }
    }

    function requestDrops(productName, callback) {
        var drops = dropsHolder[productName];
        if (drops === undefined) {
            var dropsPromise = Products.getDrops(productName);
            dropsPromise.then(callback);
            return dropsPromise;
        }
        var dropsFn = debounce(1, function() {
            callback(drops);
        });
        dropsFn();
    }

    function setDropsByKey(productName, dropsArray) {
        dropsHolder[productName] = dropsArray;
        drops = dropsArray;
    }

    function getSelectedProduct() {
        return storage.selectedProduct;
    }

    function setSelectedProduct(product) {
        storage.selectedProduct = product;
    }

    function getSelectedDrop() {
        return storage.selectedDrop;
    }

    function setSelectedDrop(drop) {
        storage.selectedDrop = drop;
    }
}
