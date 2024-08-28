angular
    .module('app.schedules')
    .factory('Schedule', Schedule);

function Schedule($resource, API_ROOT) {
    return $resource(API_ROOT + '/schedules/:id',
        {id: '@id', version: '@version', productName: '@productName', dropName: '@dropName',
            scheduleIds: '@scheduleIds'}, {

        get: {method: 'get', timeout: 10000},
        getVersion: {
            url: API_ROOT + '/schedules/:id/versions/:version',
            method: 'get',
            timeout: 10000
        },
        getSummaries: {
            url: API_ROOT + '/schedules/approved?product=:productName&drop=:dropName',
            method: 'get',
            isArray: true,
            timeout: 10000
        },
        create: {method: 'post', timeout: 10000},
        update: {method: 'put', timeout: 10000},
        approveOrReject: {
            url: API_ROOT + '/schedules/:id/approval',
            method: 'put',
            timeout: 10000
        },
        revokeApproval: {
            url: API_ROOT + '/schedules/:id/versions/:version/approval',
            method: 'delete',
            timeout: 10000
        },
        delete: {
            url: API_ROOT + '/schedules/delete',
            // PUT because angular cannot send a request body with DELETE
            method: 'put',
            timeout: 10000},
        query: {
            url: API_ROOT + '/schedules?dropId=:id',
            method: 'get',
            isArray: true,
            timeout: 10000
        },
        getSchedulesInXml: {
            url: API_ROOT + '/schedules/summaries',
            method: 'get',
            isArray: true,
            timeout: 10000
        },
        getKgbSchedules: {
            url: API_ROOT + '/schedules/kgb',
            method: 'get',
            isArray: true,
            timeout: 10000
        }
    });
}
