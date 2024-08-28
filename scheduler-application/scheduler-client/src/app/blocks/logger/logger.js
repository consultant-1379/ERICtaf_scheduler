angular
    .module('blocks.logger')
    .factory('logger', logger);

function logger($log, toastr) {
    return {
        log: $log.log,

        warn: warn,
        info: info,
        error: error,
        success: success,

        warnWithToast: warnWithToast,
        infoWithToast: infoWithToast,
        errorWithToast: errorWithToast,
        successWithToast: successWithToast,
        clearToasts: clearToasts
    };

    function warnWithToast(msg, data) {
        $log.warn('Warning: ' + msg, data);
        toastr.warning(msg, '', {
            timeOut: 10000
        });
    }

    function warn(msg, data) {
        $log.warn('Warning: ' + msg, data);
    }

    function infoWithToast(msg, data) {
        $log.info('Info: ' + msg, data);
        toastr.info(msg);
    }

    function info(msg, data) {
        $log.info('Info: ' + msg, data);
    }

    function success(msg, data) {
        $log.info('Success: ' + msg, data);
    }

    function successWithToast(msg, data) {
        $log.info('Success: ' + msg, data);
        toastr.success(msg);
    }

    function error(msg, data) {
        $log.error('Error: ' + msg, data);
    }

    function errorWithToast(msg, data) {
        $log.error('Error: ' + msg, data);
        toastr.error(msg, {
            timeOut: 10000
        });
    }

    function clearToasts() {
        toastr.clear();
    }
}
