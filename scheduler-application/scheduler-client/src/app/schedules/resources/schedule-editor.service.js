angular
    .module('app.schedules')
    .service('ScheduleEditorService', ScheduleEditorService);

function ScheduleEditorService(logger, ELEMENT_TYPE, COMPLETE_TYPE, COMPLETE_LIST) {
    var editor = null;
    var editorsMap = [];

    this.initEditor = initEditor;
    this.addEditor = addEditor;
    this.removeEditor = removeEditor;
    this.clearEditors = clearEditors;
    this.getEditor = getEditor;
    this.getContent = getContent;
    this.setContent = setContent;
    this.initContent = initContent;
    this.insertContent = insertContent;
    this.removeSelection = removeSelection;
    this.getUniqueTestwareName = getUniqueTestwareName;
    this.highlightErrors = highlightErrors;
    this.resetErrors = resetErrors;
    this.findIncludedScheduleIds = findIncludedScheduleIds;

    this.errorRanges = [];

    // editor actions for autocomplete
    this.isInsideTag = isInsideTag;
    this.getCompleteInfo = getCompleteInfo;
    this.getAttributeList = getAttributeList;

    // editor actions from icons
    this.addItemGroupTags = addItemGroupTags;
    this.addItemTags = addItemTags;
    this.addManualItemTags = addManualItemTags;
    this.addTestCampaignsTags = addTestCampaignsTags;
    this.addTestCampaignTags = addTestCampaignTags;
    this.addEnvironmentTags = addEnvironmentTags;
    this.addPropertyTags = addPropertyTags;
    this.addCommentsBlock = addCommentsBlock;
    this.addStopOnFailAttribute = addStopOnFailAttribute;
    this.addTimeoutAttribute = addTimeoutAttribute;
    this.addParallelAttribute = addParallelAttribute;
    this.addTestCampaignAttribute = addTestCampaignAttribute;
    this.insertIncludeTag = insertIncludeTag;
    this.addPropertyAttributes = addPropertyAttributes;
    this.formatTestCampaignErrors = formatTestCampaignErrors;

    function initEditor(loadedEditor) {
        editor = loadedEditor;
        editor.$blockScrolling = Infinity;
    }

    function addEditor(scheduleId, loadedEditor) {
        var entry = {};
        entry.scheduleId = scheduleId;
        entry.editor = loadedEditor;
        entry.editor.$blockScrolling = Infinity;
        editorsMap.push(entry);
        editorsMap = _.uniq(editorsMap, 'scheduleId');
    }

    function removeEditor(scheduleId) {
        var toBeRemoved = _.findWhere(editorsMap, {scheduleId: scheduleId});
        editorsMap = _.without(editorsMap, toBeRemoved);
    }

    function clearEditors() {
        editorsMap = [];
    }

    function getEditor() {
        return editor;
    }

    function highlightErrors(scheduleId, errors) {
        var Range = ace.require('ace/range').Range;

        for (var key in errors) {
            if (errors.hasOwnProperty(key)) {
                var error = errors[key];

                var errorRange = error.errorRange;
                var errorMessage = error.message;
                logger.info('Error in Schedule XML:' + errorMessage, errorRange);

                var range = new Range(
                    errorRange.startLine - 1,
                    errorRange.startColumn - 1,
                    errorRange.endLine - 1,
                    errorRange.endColumn - 1);

                var relevantEditor = _.findWhere(editorsMap, {scheduleId: scheduleId}).editor;
                var marker = relevantEditor.session.addMarker(range, 'scheduleForm-xmlError', 'text', true);
                this.errorRanges.push(marker);
            }
        }
    }

    function resetErrors() {
        editorsMap.forEach(function(entry) {
            var session = entry.editor.getSession();
            for (var key in this.errorRanges) {
                if (this.errorRanges.hasOwnProperty(key)) {
                    session.removeMarker(this.errorRanges[key]);
                }
            }
        }, this);
    }

    function insertContent(testwareObj, name, suites) {
        var startColumn = editor.getCursorPosition().column,
            content = getTestwareXMLContent(startColumn, testwareObj, name, suites) + getPadding(startColumn);

        editor.insert(content);
    }

    function insertIncludeTag(schedules) {
        var startColumn = editor.getCursorPosition().column;
        schedules.forEach(function(schedule) {
            var content =
                '<include>urn:taf-scheduler:' + schedule.id + '</include>\r\n' + getPadding(startColumn);
            editor.insert(content);
        });
    }

    function findIncludedScheduleIds() {
        var Search = ace.require('ace/search').Search;
        var search = new Search().set({
            needle: /<include>(.+?)<\/include>/,
            regExp: true
        });

        var ranges = search.findAll(editor.getSession());
        var includedScheduleIds = [];
        ranges.forEach(function(range) {
            range = adjustRange(range);
            var scheduleUrn = editor.getSession().getDocument().getTextRange(range);
            var scheduleId = parseInt(scheduleUrn.split(':')[2]);
            if (!isNaN(scheduleId)) {
                includedScheduleIds.push(scheduleId);
            }
        });
        return includedScheduleIds;
    }

    function adjustRange(range) {
        range.start.column = range.start.column + 9;
        range.end.column = range.end.column - 10;
        return range;
    }

    function initContent() {
        var content = getDefaultScheduleXmlTemplate();
        setContent(content);
    }

    function setContent(xmlContent) {
        if (!editor) {
            return;
        }
        editor.setValue(xmlContent);
    }

    function getContent() {
        if (!editor) {
            return null;
        }
        return editor.getValue();
    }

    function removeSelection() {
        if (!editor) {
            return;
        }
        editor.gotoLine(0, 1, false);
    }

    function getDefaultScheduleXmlTemplate() {

        return '<?xml version="1.0"?>\r\n' +
        '<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"\r\n' +
        '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\r\n' +
        '          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te ' +
            'http://taf.lmera.ericsson.se/schema/te/schedule/xml">\r\n\r\n' +
        '    <item-group parallel="true">\r\n' +
        '        ' + getTestwareXMLContent(8) +
        '    </item-group>\r\n\r\n' +
        '</schedule>';
    }

    function getTestwareXMLContent(startColumn, testware, name, suites) {
        var testwareObj = testware || {
                artifactId: 'artifactId',
                groupId: 'groupId'
            };

        name = name || testwareObj.artifactId;

        var suitesStr = 'suite.xml';
        if (suites) {
            suitesStr = suites.join(', ');
        }

        return '<item>\r\n' +
            getPadding(startColumn + 4) + '<name>' + name + '</name>\r\n' +
            getPadding(startColumn + 4) + '<component>' + testwareObj.groupId + ':' + testwareObj.artifactId +
            '</component>\r\n' +
            getPadding(startColumn + 4) + '<suites>' + suitesStr + '</suites>\r\n' +
            getPadding(startColumn) + '</item>\r\n';
    }

    function getUniqueTestwareName(testwareObj) {
        var foundCount = countStrings(editor.getValue(), '>' + testwareObj.artifactId) + 1;
        return testwareObj.artifactId + ' ' + foundCount;
    }

    function getPadding(count) {
        return new Array(count + 1).join(' ');
    }

    function countStrings(content, str) {
        if (!content) {
            return 0;
        }
        return (content.match(new RegExp(str, 'g')) || []).length;
    }

    /**
     * Returns a string without new lines from the left side of the cursor
     *
     * @param position object of {row, col}
     * @param session contains details of content
     * @returns {string}
     */
    function getLeftPartOfCursor(position, session) {
        var leftOfCursor = '',
            row = position.row,
            column = position.column;

        for (var r = 0; r <= row; r++) {
            if (r === row) {
                var line = session.getLine(r);
                for (var c = 0; c < column; c++) {
                    leftOfCursor += line[c];
                }
            } else {
                leftOfCursor += session.getLine(r);
            }
        }
        return leftOfCursor;
    }

    /**
     * Returns a string without new lines from the right side of the cursor
     *
     * @param position object of {row, col}
     * @param session contains details of content
     * @returns {string}
     */
    function getRightPartOfCursor(position, session) {
        var rightOfCursor = '',
            row = position.row,
            column = position.column;

        for (var r = row; r <= session.getLength(); r++) {
            if (r === row) {
                var line = session.getLine(r);
                for (var c = column; c < line.length; c++) {
                    rightOfCursor += line[c];
                }
            } else {
                rightOfCursor += session.getLine(r);
            }
        }
        return rightOfCursor;
    }

    /**
     * Returns a string with full tag from the left side of the cursor until the opened angle bracket "<"
     */
    function getLeftOpenTag(leftOfCursor) {
        var leftTagPart = '';
        if (leftOfCursor == null || leftOfCursor.length === 0) {
            return '';
        }

        var isCloseTagFound = false;
        for (var i = leftOfCursor.length - 1; i >= 0; i--) {
            var currentChar = leftOfCursor[i];
            if (currentChar === '>') {
                isCloseTagFound = true;
            }
            if (!isCloseTagFound) {
                continue;
            }

            if (currentChar === '\t') {
                leftTagPart = ' ' + leftTagPart;
                continue;
            } else {
                leftTagPart = currentChar + leftTagPart;
            }
            if (currentChar === '<') {
                return leftTagPart;
            }
        }
        return leftTagPart;
    }

    /**
     * Returns a string without new lines from the left side of the cursor until the opened angle bracket "<"
     */
    function getLeftTagPart(leftOfCursor) {
        var leftTagPart = '';
        if (leftOfCursor == null || leftOfCursor.length === 0) {
            return '';
        }

        for (var i = leftOfCursor.length - 1; i >= 0; i--) {
            var currentChar = leftOfCursor[i];
            if (currentChar === ' ' || currentChar === '\t') {
                leftTagPart = ' ' + leftTagPart;
                continue;
            }
            if (currentChar === '>') {
                return '';
            } else {
                leftTagPart = currentChar + leftTagPart;
            }
            if (currentChar === '<') {
                return leftTagPart;
            }
        }
        return leftTagPart;
    }

    /**
     * Returns a string without new lines from the right side of the cursor until the closed angle bracket ">"
     */
    function getRightTagPart(rightOfCursor) {
        var rightTagPart = '';
        if (rightOfCursor == null || rightOfCursor.length === 0) {
            return '';
        }

        for (var i = 0; i < rightOfCursor.length; i++) {
            var currentChar = rightOfCursor[i];
            if (currentChar === ' ' || currentChar === '\t') {
                rightTagPart += ' ';
                continue;
            }
            if (currentChar === '<') {
                return '';
            } else {
                rightTagPart += currentChar;
            }
            if (currentChar === '>') {
                return rightTagPart;
            }
        }
        return rightTagPart;
    }

    /**
     * Sets the left type depending on first non-whitespace character to the
     * left of the cursor position. We look for a right angle or a quotation.
     * If a right angle we assume the cursor is inside a tag. If quotation the
     * cursor is inside an attribute. We set the left type value to 'value'
     * or 'attribute'.
     */
    function getLeftType(leftOfCursor) {
        if (leftOfCursor === null || leftOfCursor.length === 0) {
            return '';
        }
        var leftType = '';
        for (var i = leftOfCursor.length - 1; i >= 0; i--) {
            var currentChar = leftOfCursor[i];
            if (currentChar === '>') {
                leftType = ELEMENT_TYPE.value;
            } else if (i > 1 && leftOfCursor[i - 1] + currentChar === '="') {
                leftType = ELEMENT_TYPE.attribute;
            }

            if (leftType.length === 0) {
                continue;
            }
            return leftType;
        }
    }

    /**
     * Sets the right type depending on first non-whitespace character to the
     * right of the cursor position. We look for a left angle or a quotation.
     * If a left angle we assume the cursor is inside a tag. If quotation the
     * cursor is inside an attribute. We set the right type value to 'value'
     * or 'attribute'.
     */
    function getRightType(rightOfCursor) {
        if (rightOfCursor === null || rightOfCursor.length === 0) {
            return '';
        }
        var rightType = '';
        for (var i = 0; i < rightOfCursor.length; i++) {
            var currentChar = rightOfCursor[i];
            if (currentChar === '<') {
                rightType = ELEMENT_TYPE.value;
            } else if (currentChar === '"') {
                rightType = ELEMENT_TYPE.attribute;
            }

            if (rightType.length === 0) {
                continue;
            }
            return rightType;
        }
    }

    function isInsideTag(position, session) {
        var leftPart = getLeftPartOfCursor(position, session),
            leftTagPart = getLeftTagPart(leftPart);
        var rightPart = getRightPartOfCursor(position, session),
            rightTagPart = getRightTagPart(rightPart);

        return leftTagPart.length > 0 && rightTagPart.length > 0;
    }

    function getCompleteInfo(position, session) {
        var leftOfCursor = getLeftPartOfCursor(position, session),
            rightOfCursor = getLeftPartOfCursor(position, session);

        var leftType = getLeftType(leftOfCursor),
            rightType = getRightType(rightOfCursor);

        var tagName = '';
        if (isInsideTag(position, session)) {
            var leftTagPart = getLeftTagPart(leftOfCursor);

            tagName = leftTagPart.trim().replace(
                new RegExp('^.*<([a-z:\-]+) *(?:.*?([a-z:\-]+)*\s*=\s*\"*(?:.+)\"* *)*$'), '$1'
            );
            if (leftType === rightType && leftType === ELEMENT_TYPE.attribute) {
                var attributeName = leftTagPart.trim().replace(
                    new RegExp('^.*<([a-z:\-]+).*?([a-z:\-]+)\s*=\s*\"$'), '$2'
                );
                return {type: COMPLETE_TYPE.attributeValue, tagName: tagName, attributeName: attributeName};
            } else {
                return {type: COMPLETE_TYPE.tagAttribute, tagName: tagName};
            }
        } else {
            if (leftType !== rightType) {
                return '';
            }
            if (leftType === ELEMENT_TYPE.value) {
                var leftOpenTag = getLeftOpenTag(leftOfCursor);
                tagName = leftOpenTag.trim().replace(new RegExp('^.*<([a-z:\-]+).*?>$'), '$1');
                return {type: COMPLETE_TYPE.tagValue, tagName: tagName};
            }
            return null;
        }
    }

    /*
     * Adjusts the start position of the highlighted area to show an error to allow
     * for the use of a single '<test-campaign/>' tag
     */
    function formatTestCampaignErrors(testCampaignErrors) {
        if (testCampaignErrors !== null) {
            testCampaignErrors.forEach(function(error) {
                var invalidTestCampaign = error.message.match(/(?:"[^"]*"|^[^"]*$)/)[0];
                error.errorRange.startColumn = parseInt(error.errorRange.startColumn) - invalidTestCampaign.length - 6;
            });
        }
        return testCampaignErrors;
    }

    function getAttributeList(tagName) {
        if (!_.has(COMPLETE_LIST, tagName)) {
            return [];
        }
        return COMPLETE_LIST[tagName];
    }

    function getPreviousSymbol(session, selection) {
        var position = selection.getCursor(),
            line = session.getLine(position.row);
        if (position.column < 1) {
            return '';
        }
        return line[position.column - 1];
    }

    function getNextSymbol(session, selection) {
        var position = selection.getCursor(),
            line = session.getLine(position.row);
        if (position.column >= line.length) {
            return '';
        }
        return line[position.column];
    }

    function addAttribute(textToAdd) {
        editor.focus();

        var selection = editor.getSelection(),
            range = selection.getRange(),
            session = editor.session,
            previousSymbol = getPreviousSymbol(session, selection),
            nextSymbol = getNextSymbol(session, selection);

        if (!(previousSymbol === '' || previousSymbol === ' ' || previousSymbol === '\t')) {
            textToAdd = ' ' + textToAdd;
        }
        if (!(nextSymbol === '' || nextSymbol === ' ' || nextSymbol === '\t' || nextSymbol === '>')) {
            textToAdd = textToAdd + ' ';
        }
        session.replace(range, textToAdd);
        selection.moveCursorLeft();
        selection.clearSelection();
    }

    function addTags(startText, endText) {
        editor.focus();

        var selection = editor.getSelection(),
            session = editor.session,
            range = selection.getRange(),
            textRange = session.getTextRange(range);

        if (endText === null) {
            session.replace(range, startText + textRange);
        } else {
            session.replace(range, startText + textRange + endText);
        }
    }

    function addItemGroupTags() {
        addTags('<item-group>', '</item-group>');
    }

    function addItemTags() {
        addTags('<item>', '</item>');
    }

    function addManualItemTags() {
        addTags('<manual-item>', '</manual-item>');
    }

    function addTestCampaignsTags() {
        addTags('<test-campaigns>', '</test-campaigns>');
    }

    function addTestCampaignTags() {
        addTags('<test-campaign/>', null);
    }

    function addEnvironmentTags() {
        addTags('<env-properties>', '</env-properties>');
    }

    function addPropertyTags() {
        addTags('<property>', '</property>');
    }

    function addCommentsBlock() {
        addTags('<!--', '-->');
    }

    function addStopOnFailAttribute() {
        addAttribute('stop-on-fail=""');
    }

    function addTimeoutAttribute() {
        addAttribute('timeout-in-seconds=""');
    }

    function addParallelAttribute() {
        addAttribute('parallel=""');
    }

    function addPropertyAttributes() {
        addAttribute('type="" key=""');
    }

    function addTestCampaignAttribute() {
        addAttribute('id=""');
    }
}
