package com.ericsson.cifwk.taf.scenario;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.operators.ScheduleTestSteps;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ericsson.cifwk.taf.ScenarioConstants.*;
import static com.ericsson.cifwk.taf.operators.ScheduleTestSteps.*;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

public class TestStepFlows {

    @Inject
    TestContext context;

    @Inject
    private ScheduleTestSteps scheduleTestSteps;

    public TestStepFlow loginFlow() {
        return flow("Login Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGIN))
                .build();
    }

    public TestStepFlow logoutFlow() {
        return flow("Logout Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGOUT))
                .build();
    }

    public TestStepFlow sendReviewCommentFlow() {
        return flow("Send Review Comment flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SEND_REVIEW_COMMENT_WITH_DOUBLE_CLICK))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_COMMENT_IS_ADDED)
                        .withParameter("comment", context.getAttribute("reviewComment")))
                .build();
    }

    public TestStepFlow writeReviewCommentFlow() {
        context.setAttribute("reviewComment", "Comment " + UUID.randomUUID().toString());
        return flow("Write Review Comment flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, WRITE_REVIEW_COMMENT)
                        .withParameter("comment", context.getAttribute("reviewComment")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_COMMENT)
                        .withParameter("comment", context.getAttribute("reviewComment")))
                .build();
    }

    public TestStepFlow openReviewCommentsFlow() {
        return flow("Open Review Comments flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_REVIEW_COMMENTS))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_COMMENTS_POPUP))
                .build();
    }

    public TestStepFlow attemptSendReviewCommentFlow() {
        return flow("Attempt Send Review Comments flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SEND_REVIEW_COMMENT_WITH_DOUBLE_CLICK))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_COMMENT_IS_ADDED)
                        .withParameter("comment", context.getAttribute("reviewComment")))
                .build();
    }

    public TestStepFlow closeReviewCommentsFlow() {
        return flow("Close Review Comments flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CLOSE_REVIEW_COMMENTS))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_COMMENTS_POPUP_IS_CLOSED))
                .build();
    }

    public TestStepFlow addTimeoutAttributeForItemByShortcutFlow() {
        return flow("Move Cursor Inside Item Tag and Add timeout Attribute By Shortcut flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_SINGLE_TAG_TO_ADD_ATTRIBUTE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_ITEM_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_TIMEOUT_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR)
                        .withParameter("timeout-value", "true"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TIMEOUT_ATTRIBUTE_IS_ADDED)
                        .withParameter("timeout-value", "true"))
                .build();
    }

    public TestStepFlow addStopOnFailAttributeForItemByIconFlow() {
        return flow("Move Cursor Inside Item Tag and Add stop-on-fail Attribute By Icon flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_SINGLE_TAG_TO_ADD_ATTRIBUTE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_ITEM_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_STOP_ON_FAIL_ATTRIBUTE_BY_ICON_IN_EDITOR)
                        .withParameter("stop-on-fail-value", "true"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_STOP_ON_FAIL_ATTRIBUTE_IS_ADDED)
                        .withParameter("stop-on-fail-value", "true"))
                .build();
    }

    public TestStepFlow addPropertyValueForPropertyByIconFlow() {
        return flow("Move cursor between Property Tags and Add value flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_BETWEEN_PROPERTY_TAGS_TO_ADD_VALUE))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_PROPERTY_VALUE_IN_EDITOR)
                        .withParameter("value", "8"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_PROPERTY_VALUE_ADDED)
                        .withParameter("value", "8"))
                .build();
    }

    public TestStepFlow addKeyTypeAttributesForPropertyByIconFlow() {
        return flow("Move cursor Inside Property Tag and Add type and key Attributes By Icon flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_TEST_CAMPAIGN_OR_PROPERTY_TAG_TO_ADD_ATTRIBUTE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_PROPERTY_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_PROPERTY_ATTRIBUTES_BY_ICON_IN_EDITOR)
                        .withParameter("type", "java")
                        .withParameter("key", "version"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_PROPERTY_ATTRIBUTES_ARE_ADDED)
                        .withParameter("type", "java")
                        .withParameter("key", "version"))
                .build();
    }

    public TestStepFlow addIdAttributeForTestCampaignFlow() {
        return flow("Move cursor Inside Test Campaign Tag and Add id Attribute By Icon flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_TEST_CAMPAIGN_OR_PROPERTY_TAG_TO_ADD_ATTRIBUTE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TEST_CAMPAIGN_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow addItemByIconInEditorFlow() {
        return flow("Add Item Tags By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_ITEM_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ITEM_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow addManualItemByIconInEditorFlow() {
        return flow("Add Manual Item Tags By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_MANUAL_ITEM_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_MANUAL_ITEM_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow addTestCampaignsByIconInEditorFlow() {
        return flow("Add TestCampaigns Tags By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_TEST_CAMPAIGNS_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TEST_CAMPAIGNS_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow addTestCampaignByIconInEditorFlow() {
        return flow("Add TestCampaign Tag By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_TEST_CAMPAIGN_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TEST_CAMPAIGN_TAG_IS_ADDED))
                .build();
    }

    public TestStepFlow moveCursorInsideItemToAddValueFlow() {
        return flow("Move Cursor Inside Item To Add Value flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_ITEM_TO_ADD_VALUE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow addEnvironmentByIconInEditorFlow() {
        return flow("Add Env-Property Tags By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_ENVIRONMENT_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ENVIRONMENT_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow addPropertyByIconInEditorFlow() {
        return flow("Add Property Tags By Icon In Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_PROPERTY_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_PROPERTY_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow moveCursorInsideDoubleNamedTagToAddValueFlow() {
        return flow("Move Cursor Inside Item Group,Environment or Manual Item To Add Value flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_DOUBLE_NAMED_TAG_TO_ADD_VALUE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow moveCursorOutsideOfEnvironmentTags() {
        return flow("Move Cursor Out of Environment Properties Tag flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_OUT_OF_ENVIRONMENT_TAGS))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow addParallelAttributeForItemGroupByShortcutFlow() {
        return flow("Move Cursor Inside item-group tag and Add Parallel Attribute by Shortcut flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_ITEM_GROUP_TO_ADD_ATTRIBUTE)
                        .withParameter("parallel-value", "true"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_ITEM_GROUP_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_PARALLEL_ATTRIBUTE_BY_SHORTCUT_IN_EDITOR)
                        .withParameter("parallel-value", "true"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_PARALLEL_ATTRIBUTE_IS_ADDED)
                        .withParameter("parallel-value", "true"))
                .build();
    }

    public TestStepFlow addParallelAttributeForItemGroupByIconFlow() {
        return flow("Move Cursor Inside item-group tag and Add Parallel Attribute by Icon flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_INSIDE_ITEM_GROUP_TO_ADD_ATTRIBUTE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_ITEM_GROUP_ATTRIBUTES_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_PARALLEL_ATTRIBUTE_BY_ICON_IN_EDITOR)
                        .withParameter("parallel-value", "true"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_PARALLEL_ATTRIBUTE_IS_ADDED)
                        .withParameter("parallel-value", "true"))
                .build();
    }

    public TestStepFlow addItemGroupByIconInEditorFlow() {
        return flow("Add Item Group By Icon in Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_ITEM_GROUP_BY_ICON_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ITEM_GROUP_TAGS_ARE_ADDED))
                .build();
    }

    public TestStepFlow startNewLineInEditorFlow() {
        return flow("Start New Line in Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, START_NEW_LINE_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow goToEndOfLineAndStartNewLineInEditorFlow() {
        return flow("Go To The End of Line and Start New Line in Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_TO_THE_END_OF_LINE_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, START_NEW_LINE_IN_EDITOR))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow addCommentBlockWithTextByIconFlow() {
        return flow("Add Comment Block With Text By Icon flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_COMMENT_BLOCK_BY_ICON_IN_EDITOR)
                        .withParameter("comment", "comment"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_COMMENTS_BLOCK_IS_ADDED)
                        .withParameter("comment", "comment"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ICONS_BLOCKS_ARE_HIDDEN))
                .build();
    }

    public TestStepFlow addCommentBlockWithTextByShortcutFlow() {
        return flow("Add Comment Block With Text By Shortcut flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_COMMENT_BLOCK_BY_SHORTCUT_IN_EDITOR)
                        .withParameter("comment", "comment"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_COMMENTS_BLOCK_IS_ADDED)
                        .withParameter("comment", "comment"))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ICONS_BLOCKS_ARE_HIDDEN))
                .build();
    }

    public TestStepFlow removeDefaultEditorContentFlow() {
        return flow("Remove Default Editor Content flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, REMOVE_DEFAULT_EDITOR_CONTENT))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADD_TAGS_BLOCK_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow navigateAndRemoveScheduleByNameFlow() {
        return flow("Create Schedule For Testing flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, NAVIGATE_AND_REMOVE_SCHEDULE_BY_NAME)
                        .withParameter("name", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow removeScheduleByNameFlow() {
        return flow("Remove Schedule By Name without navigating to page Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, REMOVE_SCHEDULE_BY_NAME)
                        .withParameter("name", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow moveToScheduleListFlow() {
        return flow("Navigate to Schedule List Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_TO_SCHEDULE_LIST_VIEW)
                        .withParameter("name", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow createScheduleForTestingFlow() {
        String scheduleForTesting = "scheduleForTesting_" + UUID.randomUUID().toString();
        context.setAttribute("scheduleName", scheduleForTesting);
        String xml = getScheduleXml("schedule-valid");
        context.setAttribute("scheduleXml", xml);

        return flow("Create Schedule For Testing flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CREATE_SCHEDULE_FOR_TESTING)
                        .withParameter("name", scheduleForTesting)
                        .withParameter("xml", xml)
                        .withParameter("type", SCHEDULE_TYPE)
                        .withParameter("team", SCHEDULE_TEAM))
                .build();
    }

    public TestStepFlow createInvalidScheduleForTestingFlow() {
        String scheduleForTesting = "scheduleForTesting_" + UUID.randomUUID().toString();
        context.setAttribute("scheduleName", scheduleForTesting);
        String xml = getScheduleXml("schedule-invalid-suite");
        context.setAttribute("scheduleXml", xml);

        return flow("Create Schedule For Testing flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CREATE_SCHEDULE_FOR_TESTING)
                        .withParameter("name", scheduleForTesting)
                        .withParameter("xml", xml)
                        .withParameter("type", SCHEDULE_TYPE)
                        .withParameter("team", SCHEDULE_TEAM))
                .build();
    }

    public TestStepFlow confirmSaveScheduleFlow() {
        return flow("Confirm Save Schedule flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SAVE_SCHEDULE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_NEW_SCHEDULE_IS_CREATED)
                        .withParameter("name", context.getAttribute("scheduleName"))
                        .withParameter("xml", context.getAttribute("scheduleXml")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULE_LABEL_IS_VALIDATED))
                .build();
    }

    public TestStepFlow saveScheduleFlow() {
        return flow("Confirm Save Schedule flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SAVE_SCHEDULE))
                .build();
    }

    public TestStepFlow confirmSaveInvalidScheduleFlow() {
        return flow("Confirm Save Invalid Schedule flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_UNVALIDATED_WARNING_IS_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SAVE_SCHEDULE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_NEW_SCHEDULE_IS_CREATED)
                        .withParameter("name", context.getAttribute("scheduleName"))
                        .withParameter("xml", context.getAttribute("scheduleXml")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULE_LABEL_IS_UNVALIDATED))
                .build();
    }

    public TestStepFlow confirmEditedScheduleSaveFlow() {
        final int version = 2;

        return flow("Confirm Edited Schedule is Saved flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SAVE_SCHEDULE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULE_VERSION)
                        .withParameter("version", version))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_ICON_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow moveToScheduleCreationFlow() {
        return flow("Move to Schedule Creation flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGIN))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP_WITH_TESTWARE))
                .addTestStep(annotatedMethod(scheduleTestSteps, GO_TO_CREATE_SCHEDULE))
                .build();
    }

    public TestStepFlow includeScheduleFlow() {
        String scheduleWithIncludeTags = "scheduleForTesting_" + UUID.randomUUID().toString();
        String testwareName = "ERICTAFgenericidentitymgmtservice_CXP9031924";
        String nameOfScheduleToInclude = "Schedule To Include.xml";

        return flow("Include Schedule Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_SCHEDULE_DETAILS)
                        .withParameter("name", scheduleWithIncludeTags)
                        .withParameter("type", SCHEDULE_TYPE)
                        .withParameter("team", SCHEDULE_TEAM))
                .addTestStep(annotatedMethod(scheduleTestSteps, REMOVE_DEFAULT_EDITOR_CONTENT))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_TESTWARE_TO_ADD)
                        .withParameter("name", testwareName))
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_SUITES_POPUP))
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SUITES_SELECTION))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_INCLUDE_SCHEDULE_BUTTON))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP_WITH_TESTWARE))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_SCHEDULE_TO_INCLUDE)
                        .withParameter("scheduleName", nameOfScheduleToInclude))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_INCLUDED_SCHEDULE_TAB_IS_DISPLAYED)
                        .withParameter("scheduleName", nameOfScheduleToInclude))
                .build();
    }

    public TestStepFlow openForEditScheduleWithHighlightedTestwareFlow() {
        context.setAttribute("scheduleName", SCHEDULE_NAME_FOR_TESTWARE_CHANGES);
        String xml = getScheduleXml("schedule-for-testware-changes");
        context.setAttribute("scheduleXml", xml);
        String[] highlightedTestwareList = {"ERICTAFgenericidentitymgmtservice_CXP9031924"};

        return flow("Open For Edit Schedule With Highlighted Testware flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_EDIT_SCHEDULE_FROM_TABLE)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULE_EDIT_PAGE_DISPLAYED)
                        .withParameter("name", context.getAttribute("scheduleName"))
                        .withParameter("xml", context.getAttribute("scheduleXml")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TESTWARE_LIST_FOR_HIGHLIGHT)
                        .withParameter("includedTestwareList", highlightedTestwareList)
                        .withParameter("testwareListWithSuitesDistinctions", highlightedTestwareList))
                .build();
    }

    public TestStepFlow openScheduleToEditFlow() {
        return flow("Open Edit Schedule Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_EDIT_SCHEDULE_FROM_TABLE)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow moveCursorToAddNewItemInTheEditorFlow() {
        return flow("Move Cursor To Add New Item In The Editor flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, MOVE_CURSOR_TO_ADD_NEW_ITEM))
                .build();
    }

    public TestStepFlow selectHighlightedTestwareFlow() {
        context.setAttribute("testwareName", "ERICTAFgenericidentitymgmtservice_CXP9031924");

        return flow("Select Highlighted Testware flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_TESTWARE_TO_ADD)
                        .withParameter("name", context.getAttribute("testwareName")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TESTWARE_IS_SELECTED)
                        .withParameter("name", context.getAttribute("testwareName")))
                .build();
    }

    public TestStepFlow selectBnsiServerTestwareFlow() {
        context.setAttribute("testwareName", "ERICTAFbnsiserver_CXP9031831");

        return flow("Select BnsiServer Testware flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_TESTWARE_TO_ADD)
                        .withParameter("name", context.getAttribute("testwareName")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TESTWARE_IS_SELECTED)
                        .withParameter("name", context.getAttribute("testwareName")))
                .build();
    }

    public TestStepFlow openSuitesPopupFlow() {
        context.setAttribute("suitesWithIcons", new String[]{"RoleManagement.xml", "TargetGroupManagement.xml"});

        return flow("Open Suites Popup flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_SUITES_POPUP))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SUITES_POPUP_ITEMS))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_ADDED_SUITES_HAVE_ICONS)
                        .withParameter("suitesWithIcons", context.getAttribute("suitesWithIcons")))
                .build();
    }

    public TestStepFlow deselectAddedSuitesFlow() {
        return flow("Deselect Added Suites flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, DESELECT_SUITES_BY_NAMES)
                        .withParameter("suitesNames", context.getAttribute("suitesWithIcons")))
                .build();
    }

    public TestStepFlow confirmSuitesSelectionFlow() {
        context.setAttribute("suitesWithoutIcons", new String[]{
                "TargetGroupPerformanceFull.xml",
                "TargetGroupPerformance.xml"
        });

        return flow("Confirm Suites Selection flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CONFIRM_SUITES_SELECTION))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SELECTED_SUITES_ARE_ADDED)
                        .withParameter("suitesNames", context.getAttribute("suitesWithoutIcons")))
                .build();
    }

    public TestStepFlow loginWithNewUserFlow() {
        return flow("Logout then Log in with a different User Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGOUT))
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGIN_WITH_DEFINED_USER)
                        .withParameter("name", NEW_USER_LOGIN_DETAILS)
                        .withParameter("password", NEW_USER_LOGIN_DETAILS))
                .build();
    }

    public TestStepFlow refreshTestwareListFlow() {
        String[] highlightedTestwareList = {"ERICTAFgenericidentitymgmtservice_CXP9031924"};

        return flow("Refresh Testware List flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, REFRESH_TESTWARE_LIST))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_TESTWARE_LIST_FOR_HIGHLIGHT)
                        .withParameter("includedTestwareList", highlightedTestwareList)
                        .withParameter("testwareListWithSuitesDistinctions", new String[]{}))
                .build();
    }

    public TestStepFlow displaySchedulesEditButtonDisplayedFlow() {
        return flow("Display Schedules for Current Drop flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP_WITH_TESTWARE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_EDIT_SCHEDULE_BUTTON_IS_DISPLAYED)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow displaySchedulesWithTestwareFlow() {
        return flow("Display Schedules for Current Drop flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP_WITH_TESTWARE))
                .build();
    }

    public TestStepFlow displaySchedulesWithTestwareForUnauthorisedUserFlow() {
        return flow("Display Schedules for Current Drop flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP_WITH_TESTWARE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_EDIT_SCHEDULE_BUTTON_IS_NOT_DISPLAYED)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow displayApproveFormFlow() {
        return flow("Display Approve Form flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SHOW_APPROVAL_FORM))
                .build();
    }

    public TestStepFlow changeScheduleViewVersionFlow() {
        final int version = 1;
        return flow("Change version of Schedule on view page flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CHANGE_SCHEDULE_VERSION_DROPDOWN)
                        .withParameter("version", version))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_ICON_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow changeScheduleViewLatestVersionFlow() {
        final int version = 2;
        return flow("Change to latest version of Schedule on view page flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, CHANGE_SCHEDULE_VERSION_DROPDOWN)
                        .withParameter("version", version))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_APPROVE_REJECT_BUTTONS_DISABLED))
                .build();
    }

    public TestStepFlow displaySchedulesFlow() {
        final int count = 3;

        return flow("Display Schedules for Current Drop Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, LOGIN))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_DROP)
                        .withParameter("drop", DROP))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULES_DISPLAYED)
                        .withParameter("count", count))
                .build();
    }

    public TestStepFlow filterTableByTeamFlow() {
        final int count = 2;
        final String team = "CI-TAF";
        context.setAttribute("team", team);

        return flow("Filter Schedules By Team Name")
                .addTestStep(annotatedMethod(scheduleTestSteps, FILTER_SCHEDULES_BY_TEAM)
                        .withParameter("team", team))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULES_DISPLAYED)
                        .withParameter("count", count))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULE_TEAM)
                        .withParameter("team", context.getAttribute("team")))
                .build();
    }

    public TestStepFlow showSchedulesForAllTeamsFlow() {
        final int count = 3;
        final String team = "All Teams";

        return flow("Display schedules for all Teams Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, FILTER_SCHEDULES_BY_TEAM)
                        .withParameter("team", team))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_SCHEDULES_DISPLAYED)
                        .withParameter("count", count))
                .build();
    }

    private String getScheduleXml(String name) {
        return getResource("schedules/" + name + ".xml");
    }

    private String getResource(String resourceName) {
        try {
            // NOTE: if stream is null when debugging, make sure Maven has copied the resource from /src to /target
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            return IOUtils.toString(inputStream).replaceAll("\r\n", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TestStepFlow addReviewersFlow() {
        final List<String> reviewers = Arrays.asList(REVIEWER1_EMAIL, REVIEWER2_SIGNUM, INVALID_USER);

        return flow("Add Reviewers to Schedule flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, ADD_REVIEWERS)
                        .withParameter("reviewers", reviewers))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEWERS)
                        .withParameter("size", 3))
                .build();
    }

    public TestStepFlow sendAndSaveScheduleFlow() {
        return flow("Send and Save Schedule Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SEND_AND_SAVE_SCHEDULE))
                .build();
    }

    public TestStepFlow openSendAndSaveDialogFlow() {
        return flow("Open Send and Save Dialog Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_SEND_AND_SAVE_DIALOG))
                .build();
    }

    public TestStepFlow editScheduleNameFlow() {
        String updatedScheduleName = "updatedScheduleName" + UUID.randomUUID().toString();
        context.setAttribute("scheduleName", updatedScheduleName);

        return flow("Edit Schedule Name Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, EDIT_SCHEDULE_NAME)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .build();
    }

    public TestStepFlow editScheduleTeamNameFlow() {
        String updatedScheduleTeamName = SCHEDULE_TEAM_UPDATED;
        context.setAttribute("scheduleTeam", updatedScheduleTeamName);

        return flow("Edit Schedule Name Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, EDIT_SCHEDULE_TEAM_NAME)
                        .withParameter("scheduleTeam", context.getAttribute("scheduleTeam")))
                .build();
    }

    public TestStepFlow editScheduleXmlFlow() {
        String updatedScheduleXml = getScheduleXml("schedule-valid");

        return flow("Edit Schedule Xml Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, EDIT_SCHEDULE_XML)
                        .withParameter("scheduleXml", updatedScheduleXml))
                .build();
    }

    public TestStepFlow removeInvalidReviewersFlow() {
        return flow("Validate Approvers and Remove Invalid Approvers Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, REMOVE_INVALID_APPROVERS)
                        .withParameter("index", 2))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEWERS)
                        .withParameter("size", 2))
                .build();
    }

    public TestStepFlow moveToScheduleViewReviewIconDisplayedFlow() {
        return flow("Click View Icon for Schedule Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, VIEW_SCHEDULE_FROM_TABLE)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_ICON_IS_DISPLAYED))
                .build();
    }

    public TestStepFlow moveToScheduleViewReviewIconNotDisplayedFlow() {
        return flow("Click View Icon for Schedule Flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, VIEW_SCHEDULE_FROM_TABLE)
                        .withParameter("scheduleName", context.getAttribute("scheduleName")))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_REVIEW_ICON_IS_NOT_DISPLAYED))
                .build();
    }

    public TestStepFlow verifyKgbFunctionalityFlow() {
        String kgbTabName = "KGB+N";
        String tafKgbScheduleName = "KGB Schedule TAF";
        String doozersKgbScheduleName = "KGB Schedule DOOZERS";
        List<String> kgbScheduleNames = Lists.newArrayList(tafKgbScheduleName, doozersKgbScheduleName);

        return flow("Verify KGB+N functionality flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_TAB_BY_NAME)
                        .withParameter("name", kgbTabName))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_DROP_SELECTOR_HIDDEN))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_KGB_SCHEDULES_ARE_IN_LIST)
                        .withParameter("scheduleNames", kgbScheduleNames))
                .addTestStep(annotatedMethod(scheduleTestSteps, OPEN_EDIT_SCHEDULE_FROM_TABLE)
                        .withParameter("scheduleName", tafKgbScheduleName))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_KGB_MODE_IS_ENABLED_IN_FORM))
                .addTestStep(annotatedMethod(scheduleTestSteps, CANCEL_EDIT_SCHEDULE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_DROP_SELECTOR_DISPLAYED))
                .addTestStep(annotatedMethod(scheduleTestSteps, SELECT_TAB_BY_NAME)
                        .withParameter("name", kgbTabName))
                .addTestStep(annotatedMethod(scheduleTestSteps, GO_TO_CREATE_SCHEDULE))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_DROP_SELECTOR_HIDDEN))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_KGB_MODE_IS_ENABLED_IN_FORM))
                .build();
    }

    public TestStepFlow navigateDocumentationFlow() {
        String overview = "Overview";
        String scheduleList = "Schedule List";
        String scheduleView = "View a Schedule";
        String scheduleCreate = "Schedule Creation / Editing";
        String scheduleReview = "Review a Schedule";
        String changelog = "Changelog";

        return flow("Navigate documentation flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, GO_TO_HELP))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", overview))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
                        .withParameter("name", overview))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", scheduleList))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
                        .withParameter("name", scheduleList))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", scheduleView))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
                        .withParameter("name", scheduleView))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", scheduleCreate))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
                        .withParameter("name", scheduleCreate))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", scheduleReview))
                .addTestStep(annotatedMethod(scheduleTestSteps, CLICK_DOCS_LINK_BY_NAME)
                        .withParameter("name", changelog))
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_CORRECT_DOCUMENTATION_HEADING_IS_DISPLAYED)
                        .withParameter("name", changelog))
                .build();
    }

    // Verify Flows

    public TestStepFlow verifyScheduleIsCreatedFlow() {
        return flow("Verify Schedule Is Created flow")
                .addTestStep(annotatedMethod(scheduleTestSteps, VERIFY_NEW_SCHEDULE_IS_CREATED)
                        .withParameter("name", context.getAttribute("scheduleName"))
                        .withParameter("xml", context.getAttribute("scheduleXml")))
                .build();
    }
}
