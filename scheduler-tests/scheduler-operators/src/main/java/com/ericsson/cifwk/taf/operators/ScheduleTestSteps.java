package com.ericsson.cifwk.taf.operators;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.annotations.Shared;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.operators.view.DropSelector;
import com.ericsson.cifwk.taf.operators.view.ReviewCommentsPopup;
import com.ericsson.cifwk.taf.operators.view.ScheduleEditPage;
import com.ericsson.cifwk.taf.ui.core.GenericPredicate;
import com.google.common.base.Strings;

import javax.inject.Inject;
import java.util.List;

import static org.testng.Assert.*;

@Operator
@Shared
public class ScheduleTestSteps {

    @Inject
    private ScheduleOperator scheduleOperator;

    public static final int WAIT_FOR_ANIMATION_TIMEOUT = 300;

    // Action steps
    public static final String LOGIN = "login";
    public static final String SELECT_DROP = "selectDrop";
    public static final String GO_TO_CREATE_SCHEDULE = "goToCreateSchedule";
    public static final String CREATE_SCHEDULE_FOR_TESTING = "createScheduleForTesting";
    public static final String NAVIGATE_AND_REMOVE_SCHEDULE_BY_NAME = "navigateAndRemoveScheduleByName";
    public static final String REMOVE_SCHEDULE_BY_NAME = "removeScheduleByName";
    public static final String REMOVE_DEFAULT_EDITOR_CONTENT = "removeDefaultEditorContent";
    public static final String ADD_COMMENT_BLOCK_BY_ICON_IN_EDITOR = "addCommentBlockByIconInEditor";
    public static final String ADD_COMMENT_BLOCK_BY_SHORTCUT_IN_EDITOR = "addCommentBlockByShortcutInEditor";
    public static final String MOVE_CURSOR_TO_THE_END_OF_LINE_IN_EDITOR = "moveCursorToTheEndOfLineInEditor";
    public static final String START_NEW_LINE_IN_EDITOR = "startNewLineInEditor";
    public static final String ADD_ITEM_GROUP_BY_ICON_IN_EDITOR = "addItemGroupByIconInEditor";
    public static final String MOVE_CURSOR_INSIDE_ITEM_GROUP_TO_ADD_ATTRIBUTE = "moveCursorInsideItemGroupToAddAttribute";
    public static final String MOVE_CURSOR_INSIDE_SINGLE_TAG_TO_ADD_ATTRIBUTE = "moveCursorInsideSingleTagToAddAttribute";
    public static final String MOVE_CURSOR_INSIDE_TEST_CAMPAIGN_OR_PROPERTY_TAG_TO_ADD_ATTRIBUTE = "moveCursorInsideTestCampaignOrPropertyTagToAddAttribute";
    public static final String MOVE_CURSOR_BETWEEN_PROPERTY_TAGS_TO_ADD_VALUE = "moveCursorBetweenPropertyTagsToAddValue";
    public static final String MOVE_CURSOR_INSIDE_DOUBLE_NAMED_TAG_TO_ADD_VALUE = "moveCursorInsideDoubleNamedTagToAddValue";
    public static final String MOVE_CURSOR_OUT_OF_ENVIRONMENT_TAGS = "moveCursorOutOfEnvironmentTags";
    public static final String MOVE_CURSOR_INSIDE_ITEM_TO_ADD_VALUE = "moveCursorInsideItemToAddValue";
    public static final String MOVE_CURSOR_INSIDE_MANUAL_ITEM_TO_ADD_VALUE = "moveCursorInsideManualItemToAddValue";
    public static final String MOVE_CURSOR_TO_ADD_NEW_ITEM = "moveCursorToAddNewItem";
    public static final String ADD_ITEM_BY_ICON_IN_EDITOR = "addItemByIconInEditor";
    public static final String ADD_MANUAL_ITEM_BY_ICON_IN_EDITOR = "addManualItemByIconInEditor";
    public static final String ADD_TEST_CAMPAIGNS_BY_ICON_IN_EDITOR = "addTestCampaignsByIconInEditor";
    public static final String ADD_TEST_CAMPAIGN_BY_ICON_IN_EDITOR = "addTestCampaignByIconInEditor";
    public static final String ADD_PROPERTY_BY_ICON_IN_EDITOR = "addPropertyByIconInEditor";
    public static final String ADD_ENVIRONMENT_BY_ICON_IN_EDITOR = "addEnvironmentByIconInEditor";
    public static final String ADD_PARALLEL_ATTRIBUTE_BY_ICON_IN_EDITOR = "addParallelAttributeByIconInEditor";
    public static final String ADD_PARALLEL_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR = "addParallelAttributeByShortcutInEditor";
    public static final String ADD_STOP_ON_FAIL_ATTRIBUTE_BY_ICON_IN_EDITOR = "addStopOnFailAttributeByIconInEditor";
    public static final String ADD_PROPERTY_VALUE_IN_EDITOR = "addPropertyValueInEditor";
    public static final String ADD_PROPERTY_ATTRIBUTES_BY_ICON_IN_EDITOR = "addPropertyAttributesByIconInEditor";
    public static final String ADD_TIMEOUT_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR = "addTimeoutAttributeByShortcutInEditor";
    public static final String OPEN_REVIEW_COMMENTS = "openReviewComments";
    public static final String CLOSE_REVIEW_COMMENTS = "closeReviewComments";
    public static final String WRITE_REVIEW_COMMENT = "writeReviewComment";
    public static final String SEND_REVIEW_COMMENT_WITH_DOUBLE_CLICK = "sendReviewCommentWithDoubleClick";
    public static final String ADD_REVIEWERS = "addReviewers";
    public static final String SEND_AND_SAVE_SCHEDULE = "sendAndSaveSchedule";
    public static final String REMOVE_INVALID_APPROVERS = "removeInvalidApprovers";
    public static final String CONFIRM_SAVE_SCHEDULE = "confirmSaveSchedule";
    public static final String FILTER_SCHEDULES_BY_TEAM = "filterSchedulesByTeam";
    public static final String OPEN_EDIT_SCHEDULE_FROM_TABLE = "openEditScheduleFromTable";
    public static final String SELECT_TESTWARE_TO_ADD = "selectTestwareToAdd";
    public static final String OPEN_SUITES_POPUP = "openSuitesPopup";
    public static final String DESELECT_SUITES_BY_NAMES = "deselectSuitesByNames";
    public static final String CONFIRM_SUITES_SELECTION = "confirmSuitesSelection";
    public static final String REFRESH_TESTWARE_LIST = "refreshTestwareList";
    public static final String LOGIN_WITH_DEFINED_USER = "loginWithDefinedUser";
    public static final String OPEN_SEND_AND_SAVE_DIALOG = "openSendAndSaveDialog";
    public static final String MOVE_TO_SCHEDULE_LIST_VIEW = "moveToScheduleListView";
    public static final String EDIT_SCHEDULE_NAME = "editScheduleName";
    public static final String EDIT_SCHEDULE_TEAM_NAME = "editScheduleTeamName";
    public static final String LOGOUT = "logout";
    public static final String VIEW_SCHEDULE_FROM_TABLE = "openViewScheduleFromTable";
    public static final String CHANGE_SCHEDULE_VERSION_DROPDOWN = "changeScheduleVersionDropDown";
    public static final String SHOW_APPROVAL_FORM = "showApprovalForm";
    public static final String EDIT_SCHEDULE_XML = "editScheduleXml";
    public static final String CLICK_INCLUDE_SCHEDULE_BUTTON = "clickScheduleIncludeButton";
    public static final String SELECT_SCHEDULE_TO_INCLUDE = "selectScheduleToInclude";
    public static final String ADD_SCHEDULE_DETAILS = "addScheduleDetails";
    public static final String SELECT_TAB_BY_NAME = "selectTabByName";
    public static final String CANCEL_EDIT_SCHEDULE = "cancelEditSchedule";
    public static final String CLICK_DOCS_LINK_BY_NAME = "clickDocsLinkByName";
    public static final String GO_TO_HELP = "goToHelp";


    // Verify steps
    public static final String VERIFY_NEW_SCHEDULE_IS_CREATED = "verifyNewScheduleIsCreated";
    public static final String VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED = "verifyAddTagsBlockIsDisplayed";
    public static final String VERIFY_ADD_ITEM_GROUP_ATTRIBUTES_BLOCK_IS_DISPLAYED = "verifyAddItemGroupAttributesBlockIsDisplayed";
    public static final String VERIFY_ADD_ITEM_ATTRIBUTES_BLOCK_IS_DISPLAYED = "verifyAddItemAttributesBlockIsDisplayed";
    public static final String VERIFY_ADD_PROPERTY_ATTRIBUTES_BLOCK_IS_DISPLAYED = "verifyAddpropertyAttributesBlockIsDisplayed";
    public static final String VERIFY_ADD_TEST_CAMPAIGN_ATTRIBUTES_BLOCK_IS_DISPLAYED = "verifyTestCampaignAttributesBlockIsDisplayed";
    public static final String VERIFY_ICONS_BLOCKS_ARE_HIDDEN = "verifyIconsBlocksAreHidden";
    public static final String VERIFY_COMMENTS_BLOCK_IS_ADDED = "verifyCommentsBlockIsAdded";
    public static final String VERIFY_ITEM_GROUP_TAGS_ARE_ADDED = "verifyItemGroupTagsAreAdded";
    public static final String VERIFY_ITEM_TAGS_ARE_ADDED = "verifyItemTagsAreAdded";
    public static final String VERIFY_MANUAL_ITEM_TAGS_ARE_ADDED = "verifyManualItemTagsAreAdded";
    public static final String VERIFY_TEST_CAMPAIGNS_TAGS_ARE_ADDED = "verifyTestCampaignsTagsAreAdded";
    public static final String VERIFY_TEST_CAMPAIGN_TAG_IS_ADDED = "verifyTestCampaignTagIsAdded";
    public static final String VERIFY_PROPERTY_TAGS_ARE_ADDED = "verifyPropertyTagsAreAdded";
    public static final String VERIFY_ENVIRONMENT_TAGS_ARE_ADDED = "verifyEnvironmentTagsAreAdded";
    public static final String VERIFY_PARALLEL_ATTRIBUTE_IS_ADDED = "verifyParallelAttributeIsAdded";
    public static final String VERIFY_STOP_ON_FAIL_ATTRIBUTE_IS_ADDED = "verifyStopOnFailAttributeIsAdded";
    public static final String VERIFY_PROPERTY_VALUE_ADDED = "verifyPropertyValueIsAdded";
    public static final String VERIFY_PROPERTY_ATTRIBUTES_ARE_ADDED = "verifyPropertyAttributesAreAdded";
    public static final String VERIFY_TIMEOUT_ATTRIBUTE_IS_ADDED = "verifyTimeoutAttributeIsAdded";
    public static final String VERIFY_REVIEW_COMMENTS_POPUP = "verifyReviewCommentsPopup";
    public static final String VERIFY_REVIEW_COMMENTS_POPUP_IS_CLOSED = "verifyReviewCommentsPopupIsClosed";
    public static final String VERIFY_REVIEW_COMMENT = "verifyReviewComment";
    public static final String VERIFY_REVIEW_COMMENT_IS_ADDED = "verifyReviewCommentIsAdded";
    public static final String VERIFY_REVIEWERS = "verifyReviewers";
    public static final String VERIFY_SCHEDULES_DISPLAYED = "verifySchedulesDisplayed";
    public static final String VERIFY_SCHEDULE_EDIT_PAGE_DISPLAYED = "verifyScheduleEditPageDisplayed";
    public static final String VERIFY_TESTWARE_LIST_FOR_HIGHLIGHT = "verifyTestwareListForHighlight";
    public static final String VERIFY_SCHEDULE_TEAM = "verifyScheduleTeam";
    public static final String VERIFY_TESTWARE_IS_SELECTED = "verifyTestwareIsSelected";
    public static final String VERIFY_SUITES_POPUP_ITEMS = "verifySuitesPopupItems";
    public static final String VERIFY_ADDED_SUITES_HAVE_ICONS = "verifyAddedSuitesHaveIcons";
    public static final String VERIFY_SELECTED_SUITES_ARE_ADDED = "verifySelectedSuitesAreAdded";
    public static final String VERIFY_SCHEDULE_VERSION = "verifyScheduleVersion";
    public static final String VERIFY_EDIT_SCHEDULE_BUTTON_IS_DISPLAYED = "verifyEditScheduleButtonIsDisplayed";
    public static final String VERIFY_EDIT_SCHEDULE_BUTTON_IS_NOT_DISPLAYED = "verifyEditScheduleButtonIsNotDisplayed";
    public static final String VERIFY_REVIEW_ICON_IS_DISPLAYED = "verifyReviewIconIsDisplayed";
    public static final String VERIFY_REVIEW_ICON_IS_NOT_DISPLAYED = "verifyReviewIconIsNotDisplayed";
    public static final String VERIFY_APPROVE_REJECT_BUTTONS_DISABLED = "verifyApproveRejectButtonsDisabled";
    public static final String VERIFY_UNVALIDATED_WARNING_IS_DISPLAYED = "verifyUnvalidatedWarningDisplayed";
    public static final String VERIFY_SCHEDULE_LABEL_IS_UNVALIDATED = "verifyUnvalidatedScheduleLabelDisplayed";
    public static final String VERIFY_SCHEDULE_LABEL_IS_VALIDATED = "verifyValidatedScheduleLabelDisplayed";
    public static final String VERIFY_INCLUDED_SCHEDULE_TAB_IS_DISPLAYED = "verifyIncludedScheduleTabIsDisplayed";
    public static final String VERIFY_DROP_SELECTOR_DISPLAYED = "verifyDropSelectorDisplayed";
    public static final String VERIFY_DROP_SELECTOR_HIDDEN = "verifyDropSelectorHidden";
    public static final String VERIFY_KGB_SCHEDULES_ARE_IN_LIST = "verifyKgbSchedulesAreInList";
    public static final String VERIFY_KGB_MODE_IS_ENABLED_IN_FORM = "verifyKgbModeIsEnabledInForm";
    public static final String VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED = "verifyCorrectHeadingIsDisplayed";

    @TestStep(id = LOGIN)
    public void login() {
        Host host = scheduleOperator.getHost();
        scheduleOperator.login(host.getUser(), host.getPass());
    }

    @TestStep(id = LOGIN_WITH_DEFINED_USER)
    public void loginWithDefinedUser(@Input("name") String name,
                                     @Input("password") String password) {
        scheduleOperator.login(name, password);
    }

    @TestStep(id = LOGOUT)
    public void logout() {
        scheduleOperator.logout();
    }

    @TestStep(id = SELECT_DROP)
    public void selectDrop(@Input("drop") DropSelection drop) {
        DropSelector dropSelector = scheduleOperator.getDropSelector();
        scheduleOperator.getBrowserTab().waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                return dropSelector.areDropOptionsPresent();
            }
        }, ScheduleOperator.WAIT_SHORT_TIMEOUT);
        dropSelector.selectDrop(drop, scheduleOperator.getBrowserTab());
    }

    @TestStep(id = GO_TO_CREATE_SCHEDULE)
    public void goToCreateSchedule() {
        scheduleOperator.getScheduleListPage().clickCreateScheduleButton();
    }

    @TestStep(id = CREATE_SCHEDULE_FOR_TESTING)
    public void createScheduleForTesting(
            @Input("name") String name,
            @Input("xml") String xml,
            @Input("type") String type,
            @Input("team") String team) {

        scheduleOperator.setScheduleFormNameText(name);
        scheduleOperator.setScheduleFormTypeDropdown(type);
        scheduleOperator.setScheduleTeamNameDropdown(team);
        scheduleOperator.setScheduleFormXml(xml);

        scheduleOperator.saveSchedule();
    }

    @TestStep(id = OPEN_SEND_AND_SAVE_DIALOG)
    public void openSendAndSaveDialog() {
        scheduleOperator.saveSchedule();
    }

    @TestStep(id = CONFIRM_SAVE_SCHEDULE)
    public void confirmSaveSchedule() {
        scheduleOperator.confirmSaveSchedule();
    }

    @TestStep(id = OPEN_EDIT_SCHEDULE_FROM_TABLE)
    public void openEditScheduleFromTable(@Input("scheduleName") String scheduleName) {
        scheduleOperator.goToEditScheduleByXmlName(scheduleName + ".xml");
        scheduleOperator.waitForScheduleEditPageShown();
    }

    @TestStep(id = VIEW_SCHEDULE_FROM_TABLE)
    public void openViewScheduleFromTable(@Input("scheduleName") String scheduleName) {
        scheduleOperator.goToViewScheduleByXmlName(scheduleName + ".xml");
        scheduleOperator.waitForScheduleViewPageShown();
    }

    @TestStep(id = EDIT_SCHEDULE_NAME)
    public void editScheduleName(@Input("scheduleName") String scheduleName) {
        scheduleOperator.setScheduleFormNameText(scheduleName);
    }

    @TestStep(id = EDIT_SCHEDULE_TEAM_NAME)
    public void editScheduleTeamName(@Input("scheduleTeam") String scheduleTeam) {
        scheduleOperator.setScheduleTeamNameDropdown(scheduleTeam);
    }

    @TestStep(id = EDIT_SCHEDULE_XML)
    public void editScheduleXml(@Input("scheduleXml") String scheduleXml) {
        scheduleOperator.setScheduleFormXml(scheduleXml);
    }

    @TestStep(id = SELECT_TESTWARE_TO_ADD)
    public void selectTestwareToAdd(@Input("name") String testwareName) {
        scheduleOperator.selectTestwareForScheduleEdit(testwareName);
    }

    @TestStep(id = OPEN_SUITES_POPUP)
    public void openSuitesPopup() {
        scheduleOperator.openSuitesPopupForScheduleEdit();
    }

    @TestStep(id = DESELECT_SUITES_BY_NAMES)
    public void deselectSuitesByNames(@Input("suitesNames") String[] suitesNames) {
        scheduleOperator.deselectSuitesByNamesInPopup(suitesNames);
    }

    @TestStep(id = CONFIRM_SUITES_SELECTION)
    public void confirmSuitesSelection() {
        scheduleOperator.confirmSuitesSelectionInPopup();
    }

    @TestStep(id = REFRESH_TESTWARE_LIST)
    public void refreshTestwareList() {
        scheduleOperator.refreshTestwareList();
    }

    @TestStep(id = FILTER_SCHEDULES_BY_TEAM)
    public void filterSchedulesByTeam(@Input("team") String team) {
        scheduleOperator.getScheduleListPage().selectTeam(team);
    }

    @TestStep(id = NAVIGATE_AND_REMOVE_SCHEDULE_BY_NAME)
    public void navigateAndRemoveScheduleByName(@Input("name") String name) {
        scheduleOperator.goToSchedulesList();
        scheduleOperator.deleteScheduleByXmlName(name + ".xml");
    }

    @TestStep(id = MOVE_TO_SCHEDULE_LIST_VIEW)
    public void moveToScheduleListView() {
        scheduleOperator.goToSchedulesList();
    }

    @TestStep(id = REMOVE_SCHEDULE_BY_NAME)
    public void removeScheduleByName(@Input("name") String name) {
        scheduleOperator.deleteScheduleByXmlName(name + ".xml");
    }

    @TestStep(id = REMOVE_DEFAULT_EDITOR_CONTENT)
    public void removeDefaultItemContentFromEditor() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().removeDefaultItemContent();
    }

    @TestStep(id = ADD_COMMENT_BLOCK_BY_ICON_IN_EDITOR)
    public void addCommentBlockByIconInEditor(@Input("comment") String comment) {
        scheduleOperator.getScheduleEditPage().addCommentsBlock();
        addCommentIntoCommentsBlock(comment);
    }

    @TestStep(id = ADD_COMMENT_BLOCK_BY_SHORTCUT_IN_EDITOR)
    public void addCommentBlockByShortcutInEditor(@Input("comment") String comment) {
        scheduleOperator.getScheduleEditPage().sendCommentBlockShortcut();
        addCommentIntoCommentsBlock(comment);
    }

    private void addCommentIntoCommentsBlock(String comment) {
        if (Strings.isNullOrEmpty(comment)) {
            return;
        }

        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToCommentBlockValue();
        scheduleEditPage.sendKeysToEditor(comment);
    }

    @TestStep(id = MOVE_CURSOR_TO_THE_END_OF_LINE_IN_EDITOR)
    public void moveCursorToTheEndOfLineInEditor() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().moveCursorToTheEndOfLine();
    }

    @TestStep(id = START_NEW_LINE_IN_EDITOR)
    public void startNewLineInEditor() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().pressEnterInEditor();
    }

    @TestStep(id = ADD_PARALLEL_ATTRIBUTE_BY_ICON_IN_EDITOR)
    public void addParallelAttributeByIconInEditor(@Input("parallel-value") String value) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleEditPage.addParallelAttribute();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.sendKeysToEditor(value);
    }

    @TestStep(id = ADD_PARALLEL_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR)
    public void addParallelAttributeByShortcutInEditor(@Input("parallel-value") String value) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.sendKeysToEditor(" ");
        scheduleEditPage.sendAutocompleteShortcut();
        scheduleEditPage.pressEnterInEditor();
        scheduleEditPage.sendKeysToEditor("=\"" + value);
    }

    @TestStep(id = ADD_ITEM_GROUP_BY_ICON_IN_EDITOR)
    public void addItemGroupByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addItemGroupTagsBlock();
    }

    @TestStep(id = ADD_ITEM_BY_ICON_IN_EDITOR)
    public void addItemByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addItemTagsBlock();
    }

    @TestStep(id = ADD_MANUAL_ITEM_BY_ICON_IN_EDITOR)
    public void addManualItemByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addManualItemTagsBlock();
    }

    @TestStep(id = ADD_TEST_CAMPAIGNS_BY_ICON_IN_EDITOR)
    public void addTestCampaignsByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addTestCampaignsTagsBlock();
    }

    @TestStep(id = ADD_TEST_CAMPAIGN_BY_ICON_IN_EDITOR)
    public void addTestCampaignByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addTestCampaignTagsBlock();
    }

    @TestStep(id = ADD_PROPERTY_BY_ICON_IN_EDITOR)
    public void addPropertyByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addPropertyTagsBlock();
    }

    @TestStep(id = ADD_ENVIRONMENT_BY_ICON_IN_EDITOR)
    public void addEnvironmentByIconInEditor() {
        scheduleOperator.getScheduleEditPage().addEnvironmentTagsBlock();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_ITEM_GROUP_TO_ADD_ATTRIBUTE)
    public void moveCursorInsideItemGroupToAddAttribute() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().moveCursorInsideItemGroupToAddAttribute();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_DOUBLE_NAMED_TAG_TO_ADD_VALUE)
    public void moveCursorInsideDoubleNamedTagToAddValue() {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToTheEndOfLine();
        scheduleEditPage.moveCursorInsideDoubleNamedTagToAddValue();
    }

    @TestStep(id = MOVE_CURSOR_OUT_OF_ENVIRONMENT_TAGS)
    public void moveCursorOutOfEnvironmentTags() {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToTheEndOfLine();
        scheduleEditPage.moveCursorOutOfEnvironmentTags();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_ITEM_TO_ADD_VALUE)
    public void moveCursorInsideItemToAddValue() {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToTheEndOfLine();
        scheduleEditPage.moveCursorInsideTagsToAddValue();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_MANUAL_ITEM_TO_ADD_VALUE)
    public void moveCursorInsideManualItemToAddValue() {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToTheEndOfLine();
        scheduleEditPage.moveCursorInsideTagsToAddValue();
    }

    @TestStep(id = MOVE_CURSOR_TO_ADD_NEW_ITEM)
    public void moveCursorToAddNewItem() {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.moveCursorToTheLine(9);
        scheduleEditPage.moveCursorToTheEndOfLine();
        scheduleEditPage.pressEnterInEditor();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_SINGLE_TAG_TO_ADD_ATTRIBUTE)
    public void setMoveCursorInsideSingleTagToAddAttribute() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().moveCursorInsideTagToAddAttribute();
    }

    @TestStep(id = MOVE_CURSOR_INSIDE_TEST_CAMPAIGN_OR_PROPERTY_TAG_TO_ADD_ATTRIBUTE)
    public void setMoveCursorInsidePropertyTagToAddAttribute() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().moveCursorInsideTestCampaignOrPropertyTagToAddAttribute();
    }

    @TestStep(id = MOVE_CURSOR_BETWEEN_PROPERTY_TAGS_TO_ADD_VALUE)
    public void setMoveCursorBetweenPropertyTagsToAddValue() {
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleOperator.getScheduleEditPage().moveCursorInsideTagsToAddValue();
    }

    @TestStep(id = ADD_STOP_ON_FAIL_ATTRIBUTE_BY_ICON_IN_EDITOR)
    public void addStopOnFailAttributeByIconInEditor(@Input("stop-on-fail-value") String value) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleEditPage.addStopOnFailAttribute();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.sendKeysToEditor(value);
    }

    @TestStep(id = ADD_PROPERTY_VALUE_IN_EDITOR)
    public void addPropertyValueInEditor(@Input("value") String value) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleEditPage.sendKeysToEditor(value);
    }

    @TestStep(id = ADD_PROPERTY_ATTRIBUTES_BY_ICON_IN_EDITOR)
    public void addPropertyAttributesByIconInEditor(@Input("key") String key, @Input("type") String type) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleEditPage.addPropertyAttributes();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.sendKeysToEditor(key);
        scheduleEditPage.moveCursorInsideTypeToAddAttribute();
        scheduleEditPage.sendKeysToEditor(type);
    }

    @TestStep(id = ADD_TIMEOUT_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR)
    public void addTimeoutAttributeByShortcutInEditor(@Input("timeout-value") String value) {
        ScheduleEditPage scheduleEditPage = scheduleOperator.getScheduleEditPage();
        scheduleOperator.moveEditorTextareaToVisibleArea();
        scheduleEditPage.sendKeysToEditor(" ");
        scheduleEditPage.sendAutocompleteShortcut();
        scheduleEditPage.pressDownArrowInEditor();
        scheduleEditPage.pressEnterInEditor();
        scheduleEditPage.sendKeysToEditor("=\"" + value);
    }

    @TestStep(id = OPEN_REVIEW_COMMENTS)
    public void openReviewComments() {
        scheduleOperator.openReviewComments();
    }

    @TestStep(id = CLOSE_REVIEW_COMMENTS)
    public void closeReviewComments() {
        scheduleOperator.closeReviewComments();
    }

    @TestStep(id = WRITE_REVIEW_COMMENT)
    public void writeReviewComment(@Input("comment") String comment) {
        scheduleOperator.getReviewCommentsPopup().setCommentText(comment);
    }

    @TestStep(id = SEND_REVIEW_COMMENT_WITH_DOUBLE_CLICK)
    public void sendReviewCommentWithDoubleClick() {
        ReviewCommentsPopup reviewCommentsPopup = scheduleOperator.getReviewCommentsPopup();
        reviewCommentsPopup.addCommentAction();
    }

    @TestStep(id = SEND_AND_SAVE_SCHEDULE)
    public void sendAndSaveSchedule() {
        scheduleOperator.saveAndSendScheduleForApproval();
    }

    @TestStep(id = REMOVE_INVALID_APPROVERS)
    public void removeInvalidApprovers(@Input("index") int index) {
        scheduleOperator.deleteInvalidReviewer(index);
    }

    @TestStep(id = ADD_REVIEWERS)
    public void addReviewers(@Input("reviewers") List<String> reviewers) {
        scheduleOperator.enterApproverDetails(reviewers);
    }

    @TestStep(id = CHANGE_SCHEDULE_VERSION_DROPDOWN)
    public void changeScheduleVersionDropDown(@Input("version") String version) {
        scheduleOperator.selectScheduleVersion(version);
    }

    @TestStep(id = SHOW_APPROVAL_FORM)
    public void showApprovalForm() {
        scheduleOperator.showApprovalForm();
    }

    @TestStep(id = ADD_SCHEDULE_DETAILS)
    public void addScheduleDetails(
            @Input("name") String name,
            @Input("type") String type,
            @Input("team") String team) {

        scheduleOperator.setScheduleFormNameText(name);
        scheduleOperator.setScheduleFormTypeDropdown(type);
        scheduleOperator.setScheduleTeamNameDropdown(team);
    }

    @TestStep(id = CLICK_INCLUDE_SCHEDULE_BUTTON)
    public void includeSchedule() {
        scheduleOperator.clickIncludeScheduleButton();
    }

    @TestStep(id = SELECT_SCHEDULE_TO_INCLUDE)
    public void selectScheduleToInclude(@Input("scheduleName") String scheduleName) {
        scheduleOperator.selectScheduleToInclude(scheduleName);
        scheduleOperator.confirmIncludeSchedule();
    }

    @TestStep(id = SELECT_TAB_BY_NAME)
    public void selectTabByName(@Input("name") String name) {
        scheduleOperator.selectTabByName(name);
    }

    @TestStep(id = CANCEL_EDIT_SCHEDULE)
    public void cancelEditSchedule() {
        scheduleOperator.cancelEditSchedule();
    }

    @TestStep(id = GO_TO_HELP)
    public void goToHelp() {
        scheduleOperator.help();
    }

    @TestStep(id = CLICK_DOCS_LINK_BY_NAME)
    public void clickDocumentationLinkByName(@Input("name") String name) {
        scheduleOperator.clickDocumentationLinkByName(name);
    }

    // **************************************************************** //
    // **************************************************************** //
    // Verify Steps

    @TestStep(id = VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED)
    public void verifyAddTagsBlockIsDisplayed() {
        scheduleOperator.waitUntilAddTagsBlockIsDisplayed();
        assertTrue(isAddTagsIconsBlockDisplayed());
        assertFalse(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddItemGroupValuesIconsBlockDisplayed());
        assertFalse(isAddPropertyAttributesIconBlockDisplayed());
        assertFalse(isAddTestCampaignAttributesIconBlockDisplayed());
    }

    @TestStep(id = VERIFY_ADD_ITEM_GROUP_ATTRIBUTES_BLOCK_IS_DISPLAYED)
    public void verifyAddItemGroupAttributesBlockIsDisplayed() {
        scheduleOperator.waitUntilItemGroupBlockIsDisplayed();
        assertFalse(isAddTagsIconsBlockDisplayed());
        assertFalse(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddPropertyAttributesIconBlockDisplayed());
        assertTrue(isAddItemGroupValuesIconsBlockDisplayed());
        assertFalse(isAddTestCampaignAttributesIconBlockDisplayed());
    }

    @TestStep(id = VERIFY_ADD_ITEM_ATTRIBUTES_BLOCK_IS_DISPLAYED)
    public void verifyAddItemAttributesBlockIsDisplayed() {
        scheduleOperator.waitUntilItemAttributesBlockIsDisplayed();
        assertFalse(isAddTagsIconsBlockDisplayed());
        assertTrue(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddItemGroupValuesIconsBlockDisplayed());
        assertFalse(isAddPropertyAttributesIconBlockDisplayed());
        assertFalse(isAddTestCampaignAttributesIconBlockDisplayed());
    }

    @TestStep(id = VERIFY_ADD_PROPERTY_ATTRIBUTES_BLOCK_IS_DISPLAYED)
    public void verifyAddPropertyAttributesBlockIsDisplayed() {
        scheduleOperator.waitUntilPropertyAttributesBlockIsDisplayed();
        assertFalse(isAddTagsIconsBlockDisplayed());
        assertFalse(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddItemGroupValuesIconsBlockDisplayed());
        assertTrue(isAddPropertyAttributesIconBlockDisplayed());
        assertFalse(isAddTestCampaignAttributesIconBlockDisplayed());
    }

    @TestStep(id = VERIFY_ADD_TEST_CAMPAIGN_ATTRIBUTES_BLOCK_IS_DISPLAYED)
    public void verifyAddTestCampaignAttributesBlockIsDisplayed() {
        scheduleOperator.waitUntilTestCampaignAttributesBlockIsDisplayed();
        assertFalse(isAddTagsIconsBlockDisplayed());
        assertFalse(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddItemGroupValuesIconsBlockDisplayed());
        assertFalse(isAddPropertyAttributesIconBlockDisplayed());
        assertTrue(isAddTestCampaignAttributesIconBlockDisplayed());
    }

    @TestStep(id = VERIFY_ICONS_BLOCKS_ARE_HIDDEN)
    public void verifyIconsBlocksAreHidden() {
        scheduleOperator.waitUntilIconBlocksAreHidden();
        assertFalse(isAddTagsIconsBlockDisplayed());
        assertFalse(isAddItemAttributesIconsBlockDisplayed());
        assertFalse(isAddItemGroupValuesIconsBlockDisplayed());
    }

    @TestStep(id = VERIFY_COMMENTS_BLOCK_IS_ADDED)
    public void verifyCommentsBlockIsAdded(@Input("comment") String comment) {
        String searchString = "<!--" + comment + "-->";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_ITEM_GROUP_TAGS_ARE_ADDED)
    public void verifyItemGroupTagsAreAdded() {
        String searchString = "<item-group></item-group>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_ITEM_TAGS_ARE_ADDED)
    public void verifyItemTagsAreAdded() {
        String searchString = "<item></item>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_MANUAL_ITEM_TAGS_ARE_ADDED)
    public void verifyManualItemTagsAreAdded() {
        String searchString = "<manual-item></manual-item>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_TEST_CAMPAIGNS_TAGS_ARE_ADDED)
    public void verifyTestCampaignsTagsAreAdded() {
        String searchString = "<test-campaigns></test-campaigns>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_TEST_CAMPAIGN_TAG_IS_ADDED)
    public void verifyTestCampaignsTagIsAdded() {
        String searchString = "<test-campaign/>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_PROPERTY_TAGS_ARE_ADDED)
    public void verifyPropertyTagsAreAdded() {
        String searchString = "<property></property>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_ENVIRONMENT_TAGS_ARE_ADDED)
    public void verifyEnvironmentTagsAreAdded() {
        String searchString = "<env-properties></env-properties>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_PARALLEL_ATTRIBUTE_IS_ADDED)
    public void verifyParallelAttributeIsAdded(@Input("parallel-value") String value) {
        String searchString = "<item-group parallel=\"" + value + "\"></item-group>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_STOP_ON_FAIL_ATTRIBUTE_IS_ADDED)
    public void verifyStopOnFailAttributeIsAdded(@Input("stop-on-fail-value") String value) {
        String searchString = "<item stop-on-fail=\"" + value + "\"></item>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_PROPERTY_VALUE_ADDED)
    public void verifyPropertyValueIsAdded(@Input("value") String value) {
        String searchString = "<property>" + value + "</property>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_PROPERTY_ATTRIBUTES_ARE_ADDED)
    public void verifyPropertyAttributesAreAdded(@Input("key") String key, @Input("type") String type) {
        String searchString = "<property type=\"" + type + "\" key=\"" + key + "\">8</property>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_TIMEOUT_ATTRIBUTE_IS_ADDED)
    public void verifyTimeoutAttributeIsAdded(@Input("timeout-value") String value) {
        String searchString = "<item timeout-in-seconds=\"" + value + "\"></item>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @TestStep(id = VERIFY_REVIEW_COMMENTS_POPUP)
    public void verifyReviewCommentsPopup() {
        ReviewCommentsPopup reviewCommentsPopup = scheduleOperator.getReviewCommentsPopup();
        assertTrue(reviewCommentsPopup.isCurrentView());
        assertFalse(reviewCommentsPopup.isAddCommentButtonEnabled());
        assertEquals(reviewCommentsPopup.getCommentBoxText(), "");
        assertEquals(reviewCommentsPopup.getEmptyCommentsMessage(), "No comments found.");
    }

    @TestStep(id = VERIFY_REVIEW_COMMENTS_POPUP_IS_CLOSED)
    public void verifyReviewCommentsPopupIsClosed() {
        scheduleOperator.waitUntilCommentsPopupIsHidden();
        assertFalse(scheduleOperator.getReviewCommentsPopup().isCurrentView());
    }

    @TestStep(id = VERIFY_REVIEW_COMMENT)
    public void verifyReviewComment(@Input("comment") String comment) {
        ReviewCommentsPopup reviewCommentsPopup = scheduleOperator.getReviewCommentsPopup();
        assertEquals(reviewCommentsPopup.getCommentBoxText(), comment);
        assertTrue(reviewCommentsPopup.isAddCommentButtonEnabled());
    }

    @TestStep(id = VERIFY_REVIEW_COMMENT_IS_ADDED)
    public void verifyReviewCommentIsAdded(@Input("comment") String comment) {
        ReviewCommentsPopup reviewCommentsPopup = scheduleOperator.getReviewCommentsPopup();
        assertEquals(reviewCommentsPopup.getCommentBoxText(), "");
        assertFalse(reviewCommentsPopup.isAddCommentButtonEnabled());
        assertEquals(reviewCommentsPopup.getCommentsCount(), 1);

        Host host = scheduleOperator.getHost();
        int commentIndex = 0;
        assertEquals(reviewCommentsPopup.getCommentTextByIndex(commentIndex), comment);
        assertEquals(reviewCommentsPopup.getCommentAuthorByIndex(commentIndex), host.getUser());
    }

    @TestStep(id = VERIFY_REVIEWERS)
    public void verifyReviewers(@Input("size") int expectedSize) {
        assertEquals(scheduleOperator.getNumberOfReviewersEntered(), expectedSize);
    }

    @TestStep(id = VERIFY_NEW_SCHEDULE_IS_CREATED)
    public void verifyNewScheduleIsCreated(@Input("name") String scheduleName, @Input("xml") String xml) {
        assertEquals(scheduleOperator.getScheduleXml(), xml);
        assertEquals(scheduleOperator.getScheduleName(), scheduleName + ".xml");
    }

    @TestStep(id = VERIFY_SCHEDULES_DISPLAYED)
    public void verifyAllSchedulesDisplayed(@Input("count") int count) {
        assertEquals(scheduleOperator.getNumberOfSchedulesDisplayed(), count);
    }

    @TestStep(id = VERIFY_SCHEDULE_EDIT_PAGE_DISPLAYED)
    public void verifyScheduleEditPageDisplayed(@Input("name") String scheduleName, @Input("xml") String xml) {
        assertEquals(scheduleOperator.getScheduleFormXml(), xml);
        assertEquals(scheduleOperator.getScheduleFormNameText(), scheduleName);
    }

    @TestStep(id = VERIFY_TESTWARE_LIST_FOR_HIGHLIGHT)
    public void verifyTestwareListForHighlight(
            @Input("includedTestwareList") String[] includedTestwareList,
            @Input("testwareListWithSuitesDistinctions") String[] testwareListWithSuitesDistinctions) {

        for (String testwareName : includedTestwareList) {
            assertTrue(scheduleOperator.isTestwareIncluded(testwareName));
        }
        for (String testwareName : testwareListWithSuitesDistinctions) {
            assertTrue(scheduleOperator.hasTestwareSuitesDistinctions(testwareName));
        }
    }

    @TestStep(id = VERIFY_SCHEDULE_TEAM)
    public void verifyScheduleTeam(@Input("team") String team) {
        assertEquals(scheduleOperator.getNumberOfUniqueTeams(), 1);
        assertEquals(scheduleOperator.getFilteredTeamNameFromTable(), team);
    }

    @TestStep(id = VERIFY_TESTWARE_IS_SELECTED)
    public void verifyTestwareIsSelected(@Input("name") String testwareName) {
        assertTrue(scheduleOperator.isTestwareSelectedForScheduleEdit(testwareName));
    }

    @TestStep(id = VERIFY_SUITES_POPUP_ITEMS)
    public void verifySuitesPopupItems() {
        assertTrue(scheduleOperator.isSuitesPopupOpened());
        assertTrue(scheduleOperator.areSuitesCheckedInPopup());
    }

    @TestStep(id = VERIFY_ADDED_SUITES_HAVE_ICONS)
    public void verifyAddedSuitesHaveIcons(@Input("suitesWithIcons") String[] suitesWithIcons) {
        for (String suiteName : suitesWithIcons) {
            assertTrue(scheduleOperator.isSuiteAdded(suiteName));
        }
    }

    @TestStep(id = VERIFY_SELECTED_SUITES_ARE_ADDED)
    public void verifySelectedSuitesAreAdded(@Input("suitesNames") String[] suitesNames) {
        for (String suiteName : suitesNames) {
            assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(suiteName));
        }
    }

    @TestStep(id = VERIFY_EDIT_SCHEDULE_BUTTON_IS_DISPLAYED)
    public void verifyEditScheduleButtonIsDisplayed(@Input("scheduleName") String scheduleName) {
        assertTrue(scheduleOperator.isEditButtonDisplayed(scheduleName + ".xml"));
    }

    @TestStep(id = VERIFY_EDIT_SCHEDULE_BUTTON_IS_NOT_DISPLAYED)
    public void verifyEditScheduleButtonIsNotDisplayed(@Input("scheduleName") String scheduleName) {
        assertFalse(scheduleOperator.isEditButtonDisplayed(scheduleName + ".xml"));
    }

    @TestStep(id = VERIFY_REVIEW_ICON_IS_DISPLAYED)
    public void verifyReviewIconIsDisplayed() {
        assertTrue(scheduleOperator.isReviewIconDisplayed());
    }

    @TestStep(id = VERIFY_REVIEW_ICON_IS_NOT_DISPLAYED)
    public void verifyReviewIconIsNotDisplayed() {
        assertFalse(scheduleOperator.isReviewIconDisplayed());
    }

    @TestStep(id = VERIFY_APPROVE_REJECT_BUTTONS_DISABLED)
    public void verifyApproveRejectButtonsDisabled() {
        assertFalse(scheduleOperator.approveAndRejectButtonsEnabled());
    }

    @TestStep(id = VERIFY_SCHEDULE_VERSION)
    public void verifyScheduleVersion(@Input("version") String version) {
        assertTrue(scheduleOperator.checkIfLatestScheduleVersionContainsString(version));
    }

    @TestStep(id = VERIFY_UNVALIDATED_WARNING_IS_DISPLAYED)
    public void verifyUnvalidatedWarningDisplayed() {
        assertTrue(scheduleOperator.checkIfUnvalidatedWarningIsDisplayed());
    }

    @TestStep(id = VERIFY_SCHEDULE_LABEL_IS_UNVALIDATED)
    public void verifyUnvalidatedScheduleLabelDisplayed() {
        assertTrue(scheduleOperator.checkIfUnvalidatedLabelIsDisplayed());
    }

    @TestStep(id = VERIFY_SCHEDULE_LABEL_IS_VALIDATED)
    public void verifyValidatedScheduleLabelDisplayed() {
        assertTrue(scheduleOperator.checkIfValidatedLabelIsDisplayed());
    }

    @TestStep(id = VERIFY_INCLUDED_SCHEDULE_TAB_IS_DISPLAYED)
    public void verifyIncludedScheduleTabIsDisplayed(@Input("scheduleName") String scheduleName) {
        assertTrue(scheduleOperator.isScheduleTabDisplayed(scheduleName));
    }

    @TestStep(id = VERIFY_DROP_SELECTOR_DISPLAYED)
    public void verifyDropSelectorDisplayed() {
        assertTrue(scheduleOperator.isDropSelectorDisplayed());
    }

    @TestStep(id = VERIFY_DROP_SELECTOR_HIDDEN)
    public void verifyDropSelectorHidden() {
        assertFalse(scheduleOperator.isDropSelectorDisplayed());
    }

    @TestStep(id = VERIFY_KGB_SCHEDULES_ARE_IN_LIST)
    public void assertKgbSchedulesAreInlist(@Input("scheduleNames") List<String> scheduleNames) {
        scheduleNames.forEach(name -> assertTrue(scheduleOperator.isScheduleInList(name + ".xml")));
    }

    @TestStep(id = VERIFY_KGB_MODE_IS_ENABLED_IN_FORM)
    public void verifyKgbModeIsEnabledInForm() {
        assertTrue(scheduleOperator.isKgbModeEnabledInForm());
    }

    @TestStep(id = VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
    public void verifyCorrectHeadingIsDisplayed(@Input("name") String name) {
        assertTrue(scheduleOperator.isCorrectHeadingDisplayed(name));
    }

    private boolean isAddTagsIconsBlockDisplayed() {
        return scheduleOperator.getScheduleEditPage().isAddTagsIconsBlockDisplayed();
    }

    private boolean isAddItemAttributesIconsBlockDisplayed() {
        return scheduleOperator.getScheduleEditPage().isAddItemAttributesIconsBlockDisplayed();
    }

    private boolean isAddItemGroupValuesIconsBlockDisplayed() {
        return scheduleOperator.getScheduleEditPage().isAddItemGroupValuesIconsBlockDisplayed();
    }

    private boolean isAddPropertyAttributesIconBlockDisplayed() {
        return scheduleOperator.getScheduleEditPage().isAddPropertyAttributesIconBlockDisplayed();
    }

    private boolean isAddTestCampaignAttributesIconBlockDisplayed() {
        return scheduleOperator.getScheduleEditPage().isAddTestCampaignAttributesIconBlockDisplayed();
    }
}
