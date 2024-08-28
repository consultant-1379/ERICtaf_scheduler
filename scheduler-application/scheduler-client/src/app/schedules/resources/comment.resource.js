/*jshint unused: false*/
angular
    .module('app.schedules')
    .factory('Comment', Comment);

function Comment($resource, API_ROOT) {
    return $resource(API_ROOT + '/schedules/:scheduleId/versions/:version/comments',
        {
            scheduleId: '@scheduleId',
            version: '@version'
        },
        {
            create: {
                method: 'post',
                timeout: 10000
            },
            query: {
                url: API_ROOT + '/schedules/:scheduleId/comments',
                method: 'get',
                isArray: true,
                timeout: 10000
            }
        }
    );
}
