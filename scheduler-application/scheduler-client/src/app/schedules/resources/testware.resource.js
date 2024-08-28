angular
    .module('app.schedules')
    .factory('Testware', Testware);

function Testware($http, $q, API_ROOT) {

    return {
        get: get,
        getTestwareListBySchedule: getTestwareListBySchedule,
        getTestwareListByScheduleXml: getTestwareListByScheduleXml,
        getLatestTestware: getLatestTestware
    };

    function get(isoName, isoVersion) {
        var deferredAbort = $q.defer();

        var request = $http({
            method: 'get',
            url: API_ROOT + '/testware?isoName=' + isoName + '&isoVersion=' + isoVersion,
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                return response.data;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(function() {
            promise.abort = angular.noop;
            deferredAbort = request = promise = null;
        });

        return promise;
    }

    function getTestwareListBySchedule(productName, dropName, scheduleId) {
        var deferredAbort = $q.defer();

        var url = '/testware/latest?product=' + productName + '&drop=' + dropName;
        if (!_.isUndefined(scheduleId)) {
            url += '&scheduleId=' + scheduleId;
        }
        var request = $http({
            method: 'get',
            url: API_ROOT + url,
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                return response.data;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(function() {
            promise.abort = angular.noop;
            deferredAbort = request = promise = null;
        });

        return promise;
    }

    function getTestwareListByScheduleXml(productName, dropName, scheduleXml) {
        var deferredAbort = $q.defer();

        var url = '/testware/analyzeXml?';
        if (productName) {
            url += 'product=' + productName;
        }
        if (dropName) {
            url += '&drop=' + dropName;
        }

        var request = $http({
            method: 'post',
            url: API_ROOT + url,
            timeout: deferredAbort.promise,
            data: scheduleXml
        });

        var promise = request.then(
            function(response) {
                return response.data;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(function() {
            promise.abort = angular.noop;
            deferredAbort = request = promise = null;
        });

        return promise;
    }

    function getLatestTestware(scheduleId) {
        var deferredAbort = $q.defer();

        var url = '/testware/current';
        if (!_.isUndefined(scheduleId)) {
            url += '?scheduleId=' + scheduleId;
        }

        var request = $http({
            method: 'get',
            url: API_ROOT + url,
            timeout: deferredAbort.promise
        });

        var promise = request.then(
            function(response) {
                return response.data;
            },
            function() {
                return $q.reject('Something went wrong');
            }
        );

        promise.abort = function() {
            deferredAbort.resolve();
        };

        promise.finally(function() {
            promise.abort = angular.noop;
            deferredAbort = request = promise = null;
        });

        return promise;
    }

}
