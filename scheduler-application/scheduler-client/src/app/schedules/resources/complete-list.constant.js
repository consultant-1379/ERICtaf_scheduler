angular
    .module('app.schedules')
    .constant('COMPLETE_LIST', {
        item: [
            {name: 'stop-on-fail', meta: 'item', valueType: 'boolean'},
            {name: 'timeout-in-seconds', meta: 'item', valueType: 'int'}
        ],
        'item-group': [
            {name: 'parallel', meta: 'item-group', valueType: 'boolean'}
        ]
    });
