angular
    .module('app.routing')
    .config(RoutingConfiguration);

function RoutingConfiguration($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('login', {
            url: '/login',
            templateUrl: 'app/login/login.html',
            controller: 'loginController',
            controllerAs: 'vm'
        })
        .state('schedules', {
            url: '/schedules',
            templateUrl: 'app/schedules/schedules.html',
            controller: 'schedulesController',
            controllerAs: 'vm',
            ncyBreadcrumb: {
                label: 'Schedules'
            }
        })
        .state('schedules-create', {
            url: '/schedules/create',
            templateUrl: 'app/schedules/form/schedule-form.html',
            controller: 'scheduleFormController',
            controllerAs: 'vm',
            ncyBreadcrumb: {
                label: 'Create',
                parent: 'schedules'
            }
        })
        .state('schedules-view', {
            url: '/schedules/:id?version',
            templateUrl: 'app/schedules/view/view-schedule.html',
            controller: 'viewScheduleController',
            controllerAs: 'vm',
            data: {
                showReview: false
            },
            ncyBreadcrumb: {
                label: 'View',
                parent: 'schedules'
            }
        })
        .state('schedules-review', {
            url: '/schedules/:id/review?version',
            templateUrl: 'app/schedules/view/view-schedule.html',
            controller: 'viewScheduleController',
            controllerAs: 'vm',
            data: {
                showReview: true
            },
            ncyBreadcrumb: {
                label: 'Review',
                parent: 'schedules-view'
            }
        })
        .state('schedules-edit', {
            url: '/schedules/:id/edit',
            templateUrl: 'app/schedules/form/schedule-form.html',
            controller: 'scheduleFormController',
            controllerAs: 'vm',
            ncyBreadcrumb: {
                label: 'Edit',
                parent: 'schedules-view'
            }
        })
        .state('documentation', {
            url: '/documentation',
            templateUrl: 'app/docs/docs.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Documentation'
            },
            redirectTo: 'documentation.overview'
        })
        .state('documentation.overview', {
            url: '/overview',
            templateUrl: 'app/docs/templates/overview.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Overview',
                parent: 'documentation'
            }
        })
        .state('documentation.schedule-list', {
            url: '/schedule-list',
            templateUrl: 'app/docs/templates/schedule-list.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Schedule List',
                parent: 'documentation'
            }
        })
        .state('documentation.schedule-view', {
            url: '/schedule-view',
            templateUrl: 'app/docs/templates/schedule-view.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'View a Schedule',
                parent: 'documentation'
            }
        })
        .state('documentation.schedule-create', {
            url: '/schedule-creation',
            templateUrl: 'app/docs/templates/schedule-creation.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Schedule Creation',
                parent: 'documentation'
            }
        })
        .state('documentation.schedule-review', {
            url: '/schedule-review',
            templateUrl: 'app/docs/templates/schedule-review.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Review a Schedule',
                parent: 'documentation'
            }
        })
        .state('documentation.changelog', {
            url: '/changelog',
            templateUrl: 'app/docs/templates/changelog.html',
            controller: 'DocsController',
            controllerAs: 'vm',
            data: {
                loginRequired: false
            },
            ncyBreadcrumb: {
                label: 'Changelog',
                parent: 'documentation'
            }
        });

    $urlRouterProvider.otherwise('/login');
}
