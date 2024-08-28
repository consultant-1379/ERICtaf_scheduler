angular
    .module('app.schedules')
    .controller('scheduleFormController', ScheduleFormController);

function ScheduleFormController($scope, $state, $stateParams, $timeout, $q, $uibModal,
                                logger, DropService, usSpinnerService,
                                ScheduleService, TestwareService, ScheduleEditorService,
                                COMPLETE_TYPE, CiPhasesService, AuthService, Session, EDITOR_EVENTS,
                                MODE_CONSTANTS, $localStorage) {
    var vm = this,
        kgbType = {id: 1, name: 'KGB+N'},
        spinnerName = 'scheduleForm-spinner',
        suitesList = [],
        blurTimeout = null,
        isEditorFocused = false,
        storage = $localStorage;

    vm.selectedDrop = null;
    vm.testwareList = [];
    vm.scheduleName = null;
    vm.xmlEditorHiddenContent = null; // needed for acceptance tests
    vm.approvedBy = null;
    vm.errorMessages = {
        requiredName: false,
        requiredType: false,
        requiredXml: false,
        serverError: false,
        requiredTeam: false
    };
    vm.selectedPackage = null;
    vm.editedSchedule = null;
    vm.selectedTeam = null;
    vm.showTestwareSpinner = true;
    vm.availableRoles = [];
    vm.valid = false;
    vm.query = '';

    // editor status
    vm.editorStatus = {
        isActive: false,
        isInsideTag: false,
        tagName: ''
    };

    // variables
    vm.selectedType = {};
    vm.scheduleTypes = [];
    vm.includedSchedules = [];

    // UI actions
    vm.hasError = hasError;
    vm.save = save;
    vm.xmlEditorLoaded = xmlEditorLoaded;
    vm.xmlEditorChanged = xmlEditorChanged;
    vm.setSelectedTestware = setSelectedTestware;
    vm.addTestware = addTestware;
    vm.includeSchedules = includeSchedules;
    vm.displayIncludedScheduleXml = displayIncludedSchedulesXml;
    vm.validateSchedules = validateSchedules;
    vm.changeEditorContent = changeEditorContent; //FOR SELENIUM
    vm.showSendApprovalOptionsDialog = showSendApprovalOptionsDialog;
    vm.refreshTestwareList = refreshTestwareList;
    vm.hasRole = Session.hasRole;
    vm.showDropSelector = showDropSelector;

    // editor actions
    vm.addItemGroupTags = ScheduleEditorService.addItemGroupTags;
    vm.addItemTags = ScheduleEditorService.addItemTags;
    vm.addManualItemTags = ScheduleEditorService.addManualItemTags;
    vm.addTestCampaignsTags = ScheduleEditorService.addTestCampaignsTags;
    vm.addTestCampaignTags = ScheduleEditorService.addTestCampaignTags;
    vm.addTestCampaignAttribute = ScheduleEditorService.addTestCampaignAttribute;
    vm.addEnvironmentTags = ScheduleEditorService.addEnvironmentTags;
    vm.addPropertyTags = ScheduleEditorService.addPropertyTags;
    vm.addCommentsBlock = ScheduleEditorService.addCommentsBlock;
    vm.addStopOnFailAttribute = ScheduleEditorService.addStopOnFailAttribute;
    vm.addTimeoutAttribute = ScheduleEditorService.addTimeoutAttribute;
    vm.addParallelAttribute = ScheduleEditorService.addParallelAttribute;
    vm.addPropertyAttributes = ScheduleEditorService.addPropertyAttributes;

    vm.scheduleValidationFailed = false;

    vm.validationPopover = {
        template: 'validationPopover.html',
        results: [],
        title: 'XML Validation errors'
    };
    vm.editorPopover = {
        template: 'editorPopover.html',
        title: 'XML Editor Help'
    };

    $scope.$on(EDITOR_EVENTS.clearEditors, clearEditors);

    activate();

    function activate() {
        loadAppropriateTestware();
        getAvailableRoles();
        getScheduleTypes();
        if ($stateParams.id) {
            logger.info('Activating ScheduleModificationController [edit]');
            ScheduleService.getLatest($stateParams.id)
                .then(function(res) {
                    if (!Session.hasRole(res.team)) {
                        logger.infoWithToast('You are not allowed to edit this schedule', res);
                        $state.go('schedules-view', {id: $stateParams.id});
                    }
                    vm.editedSchedule = res;
                    ScheduleEditorService.setContent(vm.editedSchedule.xmlContent);
                    ScheduleEditorService.removeSelection();

                    vm.xmlEditorHiddenContent = vm.editedSchedule.xmlContent;
                    vm.scheduleName = vm.editedSchedule.name;
                    if (vm.editedSchedule.type != null) {
                        vm.selectedType.id = vm.editedSchedule.type.id;
                        vm.selectedType.name = vm.editedSchedule.type.name;
                    } else {
                        vm.selectedType = angular.copy(CiPhasesService.getSelectedScheduleType());
                    }
                    vm.scheduleVersion = vm.editedSchedule.version;
                    vm.modelLoaded = true;
                    vm.selectedTeam = vm.editedSchedule.team;
                    logger.info('Schedule for edit loaded', vm.editedSchedule);
                    fetchIncludedSchedules();
                }, function() {
                    ScheduleEditorService.setContent('');
                    vm.xmlEditorHiddenContent = '';
                    logger.errorWithToast('Failed to load Schedule', $stateParams.id);
                });
        } else {
            logger.info('Activating ScheduleModificationController [create]');
            vm.scheduleVersion = 1;
        }
    }

    function showDropSelector() {
        return storage.mode === MODE_CONSTANTS.normal;
    }

    function xmlEditorLoaded(editor) {
        var Autocomplete = ace.require('ace/autocomplete').Autocomplete;
        var attributesCompleter = {
            getCompletions: function(editor, session, pos, prefix, callback) {
                var completeInfo = ScheduleEditorService.getCompleteInfo(pos, session);
                var completeList = [];
                if (COMPLETE_TYPE.tagAttribute === completeInfo.type) {
                    var attributeList = ScheduleEditorService.getAttributeList(completeInfo.tagName);
                    completeList = attributeList.map(function(completeObj) {
                        return {
                            name: completeObj.name,
                            value: completeObj.name,
                            score: 100,
                            meta: completeObj.meta
                        };
                    });
                } else if (COMPLETE_TYPE.tagValue === completeInfo.type && 'suites' === completeInfo.tagName) {
                    completeList = suitesList.map(function(suiteObj) {
                        return {
                            name: suiteObj.name,
                            value: suiteObj.name,
                            score: 100,
                            meta: suiteObj.testware.artifactId
                        };
                    });
                }

                callback(null, completeList);
            }
        };

        editor.commands.addCommand({
            name: 'showAttributesCompletions',
            bindKey: 'Ctrl-.',
            exec: function(editor) {
                if (!editor.completer) {
                    editor.completer = new Autocomplete(editor);
                }

                var all = editor.completers;
                editor.completers = [attributesCompleter];
                editor.completer.showPopup(editor);
                editor.completers = all;
            }
        });

        editor.selection.on('changeCursor', function(event, selection) {
            var pos = selection.getCursor(),
                session = selection.session;

            var editorStatus = {
                isActive: true,
                isInsideTag: ScheduleEditorService.isInsideTag(pos, session),
                tagName: ''
            };
            if (editorStatus.isInsideTag) {
                var completeInfo = ScheduleEditorService.getCompleteInfo(pos, session);
                editorStatus.tagName = completeInfo.tagName;
            }

            setTimeout(function() {
                $scope.$apply(function() {
                    vm.editorStatus = editorStatus;
                });
            });
        });
        editor.on('focus', function() {
            isEditorFocused = true;
            $timeout.cancel([blurTimeout]);

            setTimeout(function() {
                $scope.$apply(function() {
                    vm.editorStatus.isActive = true;
                });
            });
        });
        editor.on('blur', function() {
            isEditorFocused = false;
            blurTimeout = $timeout(function() {
                if (!isEditorFocused) {
                    setTimeout(function() {
                        $scope.$apply(function() {
                            vm.editorStatus.isActive = false;
                        });
                    });
                }
            }, 200);
        });

        var scheduleId = $stateParams.id ? parseInt($stateParams.id) : null;
        ScheduleEditorService.initEditor(editor);
        ScheduleEditorService.addEditor(scheduleId, editor);
        ScheduleEditorService.initContent();
        ScheduleEditorService.removeSelection();
    }

    function xmlEditorChanged() {
        resetValidationErrors();
        vm.xmlEditorHiddenContent = ScheduleEditorService.getContent();

        var includedScheduleIds = parseXmlForIncludedSchedules();
        if (includedScheduleIds.length > vm.includedSchedules.length) {
            onManualAdditionOfIncludeTag(includedScheduleIds);
        } else {
            onManualRemovalOfIncludeTag(includedScheduleIds);
        }
    }

    function clearEditors() {
        ScheduleEditorService.clearEditors();
    }

    function onManualRemovalOfIncludeTag(scheduleIdsInXml) {
        var scheduleIdsInList = _.pluck(vm.includedSchedules, 'id');
        var removedScheduleIds = _.difference(scheduleIdsInList, scheduleIdsInXml);
        removedScheduleIds.forEach(function(id) {
            ScheduleEditorService.removeEditor(id);
        });
        vm.includedSchedules = _.filter(vm.includedSchedules, function(schedule) {
            return scheduleIdsInXml.indexOf(schedule.id) > -1;
        });
    }

    function onManualAdditionOfIncludeTag(scheduleIdsInXml) {
        var scheduleIdsInList = _.pluck(vm.includedSchedules, 'id');
        var addedScheduleIds = _.difference(scheduleIdsInXml, scheduleIdsInList);
        if (!_.isEmpty(addedScheduleIds)) {
            ScheduleService.getSchedulesByIds(addedScheduleIds).then(function(res) {
                var schedules = res;
                schedules.forEach(function(schedule) {
                    schedule.displayed = false;
                });
                vm.includedSchedules = _.union(vm.includedSchedules, schedules);
                vm.includedSchedules = _.uniq(vm.includedSchedules, 'id');
            });
        }
    }

    function loadAppropriateTestware() {
        if (storage.mode === MODE_CONSTANTS.normal) {
            loadTestwareList();
        } else {
            loadKgbTestwareList();
        }
    }

    function loadTestwareList() {
        vm.selectedDrop = DropService.getSelectedDrop();
        if (!vm.selectedDrop) {
            return;
        }
        startSpinner();
        TestwareService.getTestwareListBySchedule(vm.selectedDrop.productName, vm.selectedDrop.name, $stateParams.id)
            .then(function(res) {
                vm.testwareList = res;
                stopSpinner();
            });
    }

    function loadKgbTestwareList() {
        startSpinner();
        TestwareService.getLatestTestware($stateParams.id).then(function(res) {
            vm.testwareList = res;
            stopSpinner();
        });
    }

    function save() {
        vm.selectedDrop = DropService.getSelectedDrop();
        logger.clearToasts();
        if (validateFields(vm.selectedDrop, vm.scheduleName, vm.selectedType,
                ScheduleEditorService.getContent(), vm.selectedTeam)) {
            validateSchedules().then(
                function() {
                    vm.valid = true;
                    showSendApprovalOptionsDialog();
                },
                function() {
                    logger.info('This schedule has not been validated');
                    vm.valid = false;
                    showSendApprovalOptionsDialog();
                }
            );
        }
    }

    function refreshTestwareList() {
        startSpinner();

        var product = storage.mode === MODE_CONSTANTS.normal ? vm.selectedDrop.productName : null;
        var drop = storage.mode === MODE_CONSTANTS.normal ? vm.selectedDrop.name : null;

        TestwareService.getTestwareListByScheduleXml(product, drop, ScheduleEditorService.getContent())
            .then(function(res) {
                vm.testwareList.forEach(function(testware) {
                    testware.included = false;
                    testware.distinguishedSuites = false;
                    testware.selectedSuites = [];
                    testware.existingSuites = [];
                });
                res.forEach(function(testware) {
                    var foundTestware = _.find(vm.testwareList, function(currentItem) {
                        return currentItem.id === testware.id;
                    });
                    if (!_.isUndefined(foundTestware)) {
                        foundTestware.included = testware.included;
                        foundTestware.distinguishedSuites = testware.distinguishedSuites;
                        foundTestware.selectedSuites = testware.selectedSuites;
                        foundTestware.existingSuites = testware.existingSuites;
                    }
                });
                stopSpinner();
            });
    }

    function showSendApprovalOptionsDialog() {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/schedules/form/approval-options/approval-options.html',
            controller: 'ApprovalOptionsController',
            controllerAs: 'vm',
            size: 'sm',
            resolve: {
                isValidSchedule: function() {
                    return vm.valid;
                }
            }
        });

        modalInstance.result.then(function(responseObj) {
            vm.reviewers = responseObj.reviewers;
            vm.approvalStatus = responseObj.approvalStatus;

            if (vm.editedSchedule) {
                updateSchedule();
            } else {
                createSchedule();
            }
        }, function() {
            modalInstance.close();
            logger.info('Modal dismissed at: ', new Date());
        });
    }

    function updateSchedule() {
        logger.info('Attempt to update schedule ', vm.editedSchedule);
        vm.editedSchedule.name = vm.scheduleName;
        vm.editedSchedule.type =  storage.mode === MODE_CONSTANTS.normal ? vm.selectedType : kgbType;
        vm.editedSchedule.xmlContent = ScheduleEditorService.getContent();
        vm.editedSchedule.approvalStatus = vm.approvalStatus;
        vm.editedSchedule.team = vm.selectedTeam;
        vm.editedSchedule.valid = vm.valid;

        vm.editedSchedule.reviewers = [];
        if (vm.reviewers != null) {
            vm.reviewers.forEach(function(reviewer) {
                vm.editedSchedule.reviewers.push(reviewer);
            });
        }

        ScheduleService.updateSchedule(vm.editedSchedule)
            .then(function(schedule) {
                logger.successWithToast('Schedule "' + schedule.name + '.xml" is updated successfully.', schedule);
                $state.go('schedules-view', {id: schedule.id});
            }, function(schedule) {
                logger.errorWithToast('Failed to update schedule.', schedule);
            });
    }

    function createSchedule() {
        logger.info('Attempt to create new schedule ' + vm.scheduleName, vm.selectedIso);

        var drop = storage.mode === MODE_CONSTANTS.normal ? vm.selectedDrop : null;
        var type = storage.mode === MODE_CONSTANTS.normal ? vm.selectedType : kgbType;

        ScheduleService.createSchedule(drop, vm.scheduleName, type,
            ScheduleEditorService.getContent(), vm.reviewers, vm.approvalStatus, vm.selectedTeam, vm.valid)
            .then(function(schedule) {
                logger.successWithToast('Schedule "' + schedule.name + '.xml" is created successfully.', schedule);
                $state.go('schedules-view', {id: schedule.id});
            }, function(response) {
                logger.errorWithToast(response.data[0]);
                logger.errorWithToast('Failed to create schedule.', response);
            });
    }

    function validateFields(drop, name, type, xml, team) {
        resetErrors();
        if (!drop) {
            vm.errorMessages.requiredDrop = true;
            showErrorToast('Drop should be selected.');
        }
        if (!name) {
            vm.errorMessages.requiredName = true;
            showErrorToast('Please enter schedule name.');
        }
        if (!type) {
            vm.errorMessages.requiredType = true;
            showErrorToast('Please enter schedule type.');
        }
        if (!xml) {
            vm.errorMessages.requiredXml = true;
            showErrorToast('Please enter schedule XML.');
        }
        if (!team) {
            vm.errorMessages.requiredTeam = true;
            showErrorToast('Please select team.');
        }
        return !hasError();
    }

    function showErrorToast(message) {
        logger.errorWithToast(message, {}, {
            timeOut: 0,
            closeButton: true
        });
    }

    function hasError() {
        for (var error in vm.errorMessages) {
            if (vm.errorMessages.hasOwnProperty(error) && vm.errorMessages[error]) {
                return true;
            }
        }
        return false;
    }

    function validateSchedules() {
        resetValidationErrors();
        var schedulesList = formatIncludedSchedulesForValidation();
        var scheduleId = vm.editedSchedule ? parseInt($stateParams.id) : null;
        var scheduleVersion = vm.editedSchedule ? vm.editedSchedule.version : null;
        var drop = storage.mode === MODE_CONSTANTS.normal ? vm.selectedDrop : null;
        var schedule = ScheduleService.buildSchedule(drop, scheduleId, vm.scheduleName,
            vm.selectedType, ScheduleEditorService.getContent(), scheduleVersion);
        schedulesList.push(schedule);

        return ScheduleService.validateSchedules(schedulesList).then(function(data) {
            logger.successWithToast('Schedule validation succeeded', data);
            updateErrorsInformation(data);
        },
        function(data) {
            logger.errorWithToast('Schedule validation failed', data);
            updateErrorsInformation(data);
            highlightErrors(data);
            return $q.reject('Validation failed');
        });
    }

    function formatIncludedSchedulesForValidation() {
        var schedulesToValidate = [];
        vm.includedSchedules.forEach(function(schedule) {
            var formatted = ScheduleService.buildSchedule(vm.selectedDrop, schedule.id, schedule.name, schedule.type,
                schedule.xml, schedule.version);
            schedulesToValidate.push(formatted);
        });
        return schedulesToValidate;
    }

    function highlightErrors(validationResults) {
        validationResults.forEach(function(result) {
            ScheduleEditorService.highlightErrors(result.schedule.id, result.schemaErrors);
            ScheduleEditorService.highlightErrors(result.schedule.id, result.suiteErrors);
            ScheduleEditorService.highlightErrors(result.schedule.id, result.includeErrors);
            ScheduleEditorService.highlightErrors(result.schedule.id, result.testCampaignErrors);
        });
    }

    function updateErrorsInformation(validationResults) {
        var failedResults = _.filter(validationResults, function(result) {
            return result.valid === false;
        });
        failedResults.forEach(function(result) {
            var entry = {};
            entry.scheduleName = result.schedule.name === null ? 'New Schedule' : result.schedule.name;
            entry.scheduleVersion = result.schedule.version === null ? null : result.schedule.version;
            entry.schemaErrors = result.schemaErrors;
            entry.suiteErrors = result.suiteErrors;
            entry.includeErrors = result.includeErrors;
            entry.testCampaignErrors = ScheduleEditorService.formatTestCampaignErrors(result.testCampaignErrors);
            vm.validationPopover.results.push(entry);
        });
        vm.scheduleValidationFailed = failedResults.length > 0 ? true : false;
    }

    function resetValidationErrors() {
        ScheduleEditorService.resetErrors();
        vm.validationPopover.results = [];
    }

    function resetErrors() {
        for (var error in vm.errorMessages) {
            if (vm.errorMessages.hasOwnProperty(error)) {
                vm.errorMessages[error] = false;
            }
        }
    }

    function setSelectedTestware($event, selectedTestware) {
        $event.preventDefault();

        if (vm.selectedTestware) {
            vm.selectedTestware.selected = false;
        }

        vm.selectedTestware = selectedTestware;
        vm.selectedTestware.selected = true;
    }

    function addTestware() {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/schedules/form/suites-modal/suites-modal.html',
            controller: 'SuitesModalController',
            controllerAs: 'vm',
            size: 'sm',
            resolve: {
                testware: function() {
                    return vm.selectedTestware;
                }
            }
        });

        modalInstance.result.then(function(responseObj) {
            var testware = responseObj.testware,
                testwareSuites = responseObj.testwareSuites,
                suites = responseObj.selectedSuites,
                name = ScheduleEditorService.getUniqueTestwareName(testware);

            var suitesMap = testwareSuites.map(function(suite) {
                return {
                    name: suite,
                    testware: {
                        artifactId: testware.artifactId,
                        cxpNumber: testware.cxpNumber
                    }
                };
            });

            suitesList = _.union(suitesList, suitesMap);
            ScheduleEditorService.insertContent(vm.selectedTestware, name, suites);
        }, function() {
            modalInstance.close();
            logger.info('Modal dismissed at: ', new Date());
        });
    }

    function includeSchedules() {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/schedules/form/schedules-modal/schedules-modal.html',
            controller: 'SchedulesModalController',
            controllerAs: 'vm',
            size: 'lg',
            resolve: {
                selectedSchedules: function() {
                    return [];
                }
            }
        });

        modalInstance.result.then(function(responseObj) {
            responseObj.selectedSchedules.forEach(function(schedule) {
                var alreadyDisplayed = _.findWhere(vm.includedSchedules, {id: schedule.id, displayed: true});
                if (!alreadyDisplayed) {
                    schedule.displayed = false;
                    vm.includedSchedules.push(schedule);
                }
            });
            vm.includedSchedules = _.uniq(vm.includedSchedules, 'id');
            ScheduleEditorService.insertIncludeTag(responseObj.selectedSchedules);
        }, function() {
            modalInstance.close();
            logger.info('Modal dismissed at: ', new Date());
        });
    }

    function parseXmlForIncludedSchedules() {
        return ScheduleEditorService.findIncludedScheduleIds();
    }

    function fetchIncludedSchedules() {
        var scheduleIds = parseXmlForIncludedSchedules();
        if (scheduleIds.length > 0) {
            ScheduleService.getSchedulesByIds(scheduleIds).then(function(res) {
                vm.includedSchedules = res;
                vm.includedSchedules.forEach(function(schedule) {
                    schedule.displayed = false;
                });
            });
        }
    }

    function displayIncludedSchedulesXml(editor) {
        var scheduleToDisplay = _.findWhere(vm.includedSchedules, {displayed: false});
        editor.insert(scheduleToDisplay.xml);
        scheduleToDisplay.displayed = true;
        ScheduleEditorService.addEditor(scheduleToDisplay.id, editor);
    }

    function startSpinner() {
        vm.showTestwareSpinner = true;
        usSpinnerService.spin(spinnerName);
    }

    function stopSpinner() {
        vm.showTestwareSpinner = false;
        usSpinnerService.stop(spinnerName);
    }

    function changeEditorContent() { //FOR SELENIUM
        ScheduleEditorService.setContent(vm.xmlEditorHiddenContent);
    }

    function getScheduleTypes() {
        CiPhasesService.getScheduleTypes(function(res) {
            var withoutKgb = _.filter(res, function(type) {
                return type.name !== 'KGB+N';
            });
            vm.scheduleTypes = withoutKgb;
            vm.selectedType = vm.scheduleTypes[0];
        });
    }

    function getAvailableRoles() {
        AuthService.checkCurrentUser().then(function(res) {
            vm.availableRoles = res.data.roles;
        });
    }
}
