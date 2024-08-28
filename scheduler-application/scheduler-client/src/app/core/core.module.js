angular.module('app.core', [
    /*
     * Angular modules
     */
    'ngAnimate', 'ngCookies', 'ngTouch',
    'ngSanitize', 'ngResource', 'ngMessages',
    'ngClipboard', 'ngStorage', 'ngTagsInput',

    /*
     * 3PP
     */
    'ncy-angular-breadcrumb',
    'ui.router',
    'ui.bootstrap',
    'ui.ace',
    'angularSpinner',
    'rt.debounce',
    'toastr',
    'checklist-model',

    /*
     * Reusable modules
     */
    'blocks.logger',
    'blocks.popups'
]);
