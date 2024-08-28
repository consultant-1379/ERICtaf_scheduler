angular
    .module('app.footer')
    .service('FooterService', FooterService);

function FooterService($location) {

    var BASE_URL_LOCAL = 'http://localhost:8080',
        BASE_URL_TEST = 'http://atvts2827.athtem.eei.ericsson.se:8888',
        BASE_URL_PROD_CLIENT = 'https://taf-scheduler.lmera.ericsson.se',
        SWAGGER_RESOURCE = '/api/swagger-ui.html';

    this.getSwaggerUrl = getSwaggerUrl;

    function getSwaggerUrl() {
        var absUrl = $location.absUrl();
        if (absUrl.indexOf(BASE_URL_LOCAL) !== -1) {
            return BASE_URL_LOCAL + SWAGGER_RESOURCE;
        } else if (absUrl.indexOf(BASE_URL_TEST) !== -1) {
            return BASE_URL_TEST + SWAGGER_RESOURCE;
        } else if (absUrl.indexOf(BASE_URL_PROD_CLIENT) !== -1) {
            return BASE_URL_PROD_CLIENT + SWAGGER_RESOURCE;
        }
        return null;
    }
}
