package com.ericsson.cifwk.taf.scenario;

import com.ericsson.cifwk.taf.HostResolver;
import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.operators.DropSelection;
import com.ericsson.cifwk.taf.operators.ScheduleOperator;
import com.ericsson.cifwk.taf.ui.UI;
import com.google.common.base.Joiner;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static com.ericsson.cifwk.taf.ScenarioConstants.*;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertEquals;

public class ScheduleScenario extends TafTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleScenario.class);

    private static final String SCHEDULE_TO_EDIT = "A Schedule to edit.xml";
    private static final String SCHEDULE_VERSION_1 = "First Schedule.xml";
    private static final String SCHEDULE_VERSION_2 = "First Schedule V2.xml";
    private static final String SCHEDULE_VERSION_3 = "First Schedule V3.xml";
    private static final String TESTWARE_NAME = "ERICTAFlogviewer_CXP9031658";
    private static final String SCHEDULE_APPROVAL_MSG = "I am TAF Scheduler user and I approve this schedule.";
    private static final String SCHEDULE_REJECTION_MSG = "I am TAF Scheduler user and I reject this schedule.";
    private static final String TAF = "taf";
    private static final String TAF2 = "taf2";

    private static final String APPROVE = "approve";
    private static final String REJECT = "reject";
    private static final String QUERY_PARAM_PRODUCT = "product";
    private static final String QUERY_PARAM_DROP = "drop";

    public static final int PAGE_UPDATE_WAIT_TIME = 3000;

    @Inject
    private ScheduleOperator scheduleOperator;

    @Inject
    private TestStepFlows testStepFlows;

    @BeforeMethod
    public void setUp() throws Exception {
        scheduleOperator.init(HostResolver.resolve());
    }

    @Test
    @TestId(id = "TAF_Scheduler_004")
    public void createSchedule() throws InterruptedException {
        // login
        scheduleOperator.login();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        // remember current iso
        DropSelection selectedIso = scheduleOperator.getDropSelection();

        // go to schedule editor
        scheduleOperator.goToCreateSchedule();
        assertEquals(scheduleOperator.getDropSelection(), selectedIso);

        // make page refresh and check product, drop and iso select boxes.
        scheduleOperator.refreshPage();
        assertEquals(DROP_WITH_TESTWARE, scheduleOperator.getDropSelection());

        // iso selectors are disabled
        assertFalse(scheduleOperator.getDropSelector().isEnabled());

        // editing schedule
        String xml = getScheduleXml("schedule-valid");
        String name = SCHEDULE_NAME + UUID.randomUUID();
        enterScheduleDetails(name, SCHEDULE_TYPE, xml, SCHEDULE_TEAM, scheduleOperator);
        scheduleOperator.saveSchedule();
        scheduleOperator.confirmSaveSchedule();

        // redirected to schedule details
        assertTrue(scheduleOperator.isScheduleViewPageShown());
        assertEquals(name + ".xml", scheduleOperator.getScheduleName());
        assertEquals(SCHEDULE_TYPE, scheduleOperator.getScheduleType());
        assertEquals(SCHEDULE_TEAM, scheduleOperator.getScheduleTeam());
        assertThat(scheduleOperator.getScheduleXml(), equalTo(xml));

        scheduleOperator.waitForToastsToClear();
        scheduleOperator.logout();
    }

    @Test
    @TestId(id = "TAF_Scheduler_005")
    public void editSchedule() throws InterruptedException {
        // login
        scheduleOperator.login();

        // precondition - create schedule to edit
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);
        scheduleOperator.goToCreateSchedule();

        // make page refresh and check product, drop and iso select boxes.
        scheduleOperator.refreshPage();
        assertEquals(DROP_WITH_TESTWARE, scheduleOperator.getDropSelection());

        enterScheduleDetailsAndVerify(SCHEDULE_TO_EDIT, SCHEDULE_TYPE, getScheduleXml("schedule-valid"),
                SCHEDULE_TEAM, scheduleOperator);

        // save new schedule
        scheduleOperator.saveSchedule();
        scheduleOperator.confirmSaveSchedule();
        scheduleOperator.waitForScheduleViewPageShown();

        // go to schedules list view
        scheduleOperator.goToSchedulesList();
        assertTrue(scheduleOperator.isScheduleListShown());

        // change current iso
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        scheduleOperator.goToEditScheduleByXmlName(SCHEDULE_TO_EDIT + ".xml");

        // make page refresh and check product, drop and iso select boxes.
        scheduleOperator.refreshPage();
        assertEquals(DROP_WITH_TESTWARE, scheduleOperator.getDropSelection());

        String name = UUID.randomUUID().toString();
        LOG.info("Setting schedule title to {} ", name);
        String xml = getScheduleXml("schedule-valid");
        enterScheduleDetailsAndVerify(name, xml, scheduleOperator);
        scheduleOperator.saveSchedule();
        scheduleOperator.confirmSaveSchedule();

        assertThat(scheduleOperator.getScheduleXml(), equalTo(xml));
        assertThat(scheduleOperator.getScheduleName(), is(name + ".xml"));

        // delete edited schedule
        scheduleOperator.goToSchedulesList();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);
        scheduleOperator.deleteScheduleByXmlName(name + ".xml");

        scheduleOperator.waitForToastsToClear();
        scheduleOperator.logout();
    }

    @Test
    @TestId(id = "TAF_Scheduler_007")
    public void deleteSchedule() {
        // login
        scheduleOperator.login();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        // create schedule
        final String scheduleToDelete = "deleteSchedule_" + UUID.randomUUID().toString();
        final String scheduleToDeleteXml = scheduleToDelete + ".xml";
        LOG.info("Schedule name set to " + scheduleToDelete);

        scheduleOperator.goToCreateSchedule();
        enterScheduleDetailsAndVerify(scheduleToDelete, SCHEDULE_TYPE, getScheduleXml("schedule-valid"),
                                        SCHEDULE_TEAM, scheduleOperator);
        scheduleOperator.saveSchedule();
        scheduleOperator.confirmSaveSchedule();

        // find created
        scheduleOperator.goToSchedulesList();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        // delete
        scheduleOperator.deleteScheduleByXmlName(scheduleToDeleteXml);
        assertFalse(scheduleOperator.isScheduleInList(scheduleToDeleteXml));

        // logout
        scheduleOperator.waitForToastsToClear();
        scheduleOperator.logout();
    }

    @Test
    @TestId(id = "TAF_Scheduler_008")
    public void validateSchedule_Suites() {
        validateSchedule("schedule-invalid-suite",
                String.format("Suites %s don't exist in Testware", Lists.newArrayList("InvalidSuite.xml")));
    }

    @Test
    @TestId(id = "TAF_Scheduler_009")
    public void validateSchedule_Schema() {
        validateSchedule("schedule-duplicate-item",
                "Duplicate unique value [duplicate] declared for identity constraint of element \"schedule\".");
    }

    @Test
    @TestId(id = "TAF_Scheduler_010")
    public void selectScheduleVersions() {
        scheduleOperator.login();

        scheduleOperator.selectDrop(DROP);
        scheduleOperator.viewSchedule(SCHEDULE_VERSION_3);

        assertThat(scheduleOperator.getScheduleName(), equalTo(SCHEDULE_VERSION_3));
        assertThat(scheduleOperator.getScheduleXml(), containsString("<name>ERICTAFeniq_cdb_setup_CXP9027959 3</name>"));
        assertThat(scheduleOperator.getScheduleType(), equalTo(SCHEDULE_TYPE));
        assertThat(scheduleOperator.getScheduleTeam(), equalTo(SCHEDULE_TEAM));

        scheduleOperator.selectScheduleVersion("2");
        assertThat(scheduleOperator.getScheduleName(), equalTo(SCHEDULE_VERSION_2));
        assertThat(scheduleOperator.getScheduleXml(), containsString("<name>ERICTAFeniq_cdb_setup_CXP9027959 2</name>"));
        assertThat(scheduleOperator.getScheduleType(), equalTo(SCHEDULE_TYPE));
        assertThat(scheduleOperator.getScheduleTeam(), equalTo(SCHEDULE_TEAM));

        scheduleOperator.selectScheduleVersion("1");
        assertThat(scheduleOperator.getScheduleName(), equalTo(SCHEDULE_VERSION_1));
        assertThat(scheduleOperator.getScheduleXml(), containsString("<name>ERICTAFeniq_cdb_setup_CXP9027959 1</name>"));
        assertThat(scheduleOperator.getScheduleType(), equalTo(SCHEDULE_TYPE));
        assertThat(scheduleOperator.getScheduleTeam(), equalTo(SCHEDULE_TEAM));

        scheduleOperator.logout();
    }

    @Test
    @TestId(id = "TAF_Scheduler_011")
    public void selectTestwareSuitesForSchedule() {
        // login
        scheduleOperator.login();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        // go to schedule editor
        scheduleOperator.goToCreateSchedule();
        assertTrue(scheduleOperator.isCreateSchedulePageOpened());

        // Enter schedule title
        final String scheduleToCreate = "scheduleWithTestwareSuites_" + UUID.randomUUID().toString();
        scheduleOperator.setScheduleFormNameText(scheduleToCreate);

        // Modify schedule xml content
        scheduleOperator.removeDefaultItemContentFromEditor();

        // Select testware
        scheduleOperator.selectTestwareForScheduleEdit(TESTWARE_NAME);
        assertTrue(scheduleOperator.isTestwareSelectedForScheduleEdit(TESTWARE_NAME));

        // Open popup with suites
        scheduleOperator.openSuitesPopupForScheduleEdit();

        assertTrue(scheduleOperator.isSuitesPopupOpened());
        assertTrue(scheduleOperator.areSuitesCheckedInPopup());

        // Preselect suites from popup and add to editor
        scheduleOperator.deselectSuitesInPopup();
        assertFalse(scheduleOperator.isConfirmButtonEnabledInPopup());
        scheduleOperator.checkSuitesByOrderNrInPopup(0);
        assertTrue(scheduleOperator.isConfirmButtonEnabledInPopup());
        List<String> suitesList = scheduleOperator.getCheckedSuitesNamesFromPopup();

        scheduleOperator.confirmSuitesSelectionInPopup();

        // Check, that suites are added to editor
        String suitesString = Joiner.on(", ").join(suitesList);
        String searchString = "<suites>" + suitesString + "</suites>";
        assertTrue(scheduleOperator.checkIfScheduleEditorContainsString(searchString));
    }

    @Test
    @TestId(id = "TAF_Scheduler_012")
    public void approveAndRejectSchedule() {
        scheduleOperator.login();

        scheduleOperator.selectDrop(DROP);
        scheduleOperator.viewSchedule(SCHEDULE_VERSION_3);

        assertFalse(scheduleOperator.isScheduleApproved());
        scheduleOperator.reviewSchedule(SCHEDULE_APPROVAL_MSG, APPROVE);
        assertTrue(scheduleOperator.isScheduleApproved());
        assertTrue(scheduleOperator.isApproverCorrect(TAF));
        assertTrue(scheduleOperator.isApprovalMsgCorrect(SCHEDULE_APPROVAL_MSG));

        scheduleOperator.logout();

        scheduleOperator.login(TAF2, TAF2);
        scheduleOperator.selectDrop(DROP);
        scheduleOperator.viewSchedule(SCHEDULE_VERSION_3);

        scheduleOperator.revokePreviousStatus();
        assertFalse(scheduleOperator.isScheduleApproved());
        scheduleOperator.reviewSchedule(SCHEDULE_REJECTION_MSG, REJECT);
        assertFalse(scheduleOperator.isScheduleApproved());
        assertTrue(scheduleOperator.isApproverCorrect(TAF2));
        assertTrue(scheduleOperator.isApprovalMsgCorrect(SCHEDULE_REJECTION_MSG));

        scheduleOperator.revokePreviousStatus();

        scheduleOperator.logout();
    }

    //TODO - switching between Maintrack and KGB+N tabs.  Cannot change url without resorting to hacks (view is reloaded)
    @Test(enabled = false)
    @TestId(id = "TAF_Scheduler_015")
    public void selectDropByDifferentWays() {
        // login
        scheduleOperator.login();

        // go to schedules list view
        UI.pause(PAGE_UPDATE_WAIT_TIME); // Need to wait for page update and link apply

        String activeTabUrl = scheduleOperator.getActiveTabUrl();
        assertTrue(activeTabUrl.contains(QUERY_PARAM_PRODUCT));
        assertTrue(activeTabUrl.contains(QUERY_PARAM_DROP));

        // change Drop by SelectBoxes
        scheduleOperator.selectDrop(DROP);
        activeTabUrl = scheduleOperator.getActiveTabUrl();
        assertTrue(activeTabUrl.contains(QUERY_PARAM_PRODUCT + "=" + DROP.getProduct()));
        assertTrue(activeTabUrl.contains(QUERY_PARAM_DROP + "=" + DROP.getDrop()));

        // change active tab with defined URL
        DropSelection iso = new DropSelection("CI", "1.1.ci");
        scheduleOperator.setActiveTabHash(
                "/schedules?" +
                        QUERY_PARAM_PRODUCT + "=" + iso.getProduct() + "&" +
                        QUERY_PARAM_DROP + "=" + iso.getDrop()
        );
        UI.pause(PAGE_UPDATE_WAIT_TIME); // Need to wait for page update and link apply

        assertTrue(iso.equals(scheduleOperator.getDropSelection()));
    }

    @Test
    @TestId(id = "TAF_Scheduler_016", title = "Schedule Edit With Helpers Scenario")
    public void scheduleEditWithHelpersScenario() {
        TestScenario scenario = scenario("Schedule Edit With Helpers Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.removeDefaultEditorContentFlow())
                .addFlow(testStepFlows.addCommentBlockWithTextByIconFlow())
                .addFlow(testStepFlows.goToEndOfLineAndStartNewLineInEditorFlow())
                .addFlow(testStepFlows.addItemGroupByIconInEditorFlow())
                .addFlow(testStepFlows.addParallelAttributeForItemGroupByIconFlow())
                .addFlow(testStepFlows.moveCursorInsideDoubleNamedTagToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addItemByIconInEditorFlow())
                .addFlow(testStepFlows.addStopOnFailAttributeForItemByIconFlow())
                .addFlow(testStepFlows.moveCursorInsideItemToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addEnvironmentByIconInEditorFlow())
                .addFlow(testStepFlows.moveCursorInsideDoubleNamedTagToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addPropertyByIconInEditorFlow())
                .addFlow(testStepFlows.addPropertyValueForPropertyByIconFlow())
                .addFlow(testStepFlows.addKeyTypeAttributesForPropertyByIconFlow())
                .addFlow(testStepFlows.moveCursorOutsideOfEnvironmentTags())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addManualItemByIconInEditorFlow())
                .addFlow(testStepFlows.moveCursorInsideDoubleNamedTagToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addTestCampaignsByIconInEditorFlow())
                .addFlow(testStepFlows.moveCursorInsideDoubleNamedTagToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addTestCampaignByIconInEditorFlow())
                .addFlow(testStepFlows.addIdAttributeForTestCampaignFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_017", title = "Schedule Edit With Autocomplete Scenario")
    public void scheduleEditWithAutocompleteScenario() {
        TestScenario scenario = scenario("Schedule Edit With Autocomplete Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.removeDefaultEditorContentFlow())
                .addFlow(testStepFlows.addCommentBlockWithTextByShortcutFlow())
                .addFlow(testStepFlows.goToEndOfLineAndStartNewLineInEditorFlow())
                .addFlow(testStepFlows.addItemGroupByIconInEditorFlow())
                .addFlow(testStepFlows.addParallelAttributeForItemGroupByShortcutFlow())
                .addFlow(testStepFlows.moveCursorInsideDoubleNamedTagToAddValueFlow())
                .addFlow(testStepFlows.startNewLineInEditorFlow())
                .addFlow(testStepFlows.addItemByIconInEditorFlow())
                .addFlow(testStepFlows.addTimeoutAttributeForItemByShortcutFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_020", title = "Schedule View With Review Comments")
    public void createNewComment() {
        TestScenario scenario = scenario("Schedule View With Review Comments")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createScheduleForTestingFlow())
                .addFlow(testStepFlows.confirmSaveScheduleFlow())
                .addFlow(testStepFlows.openReviewCommentsFlow())
                .addFlow(testStepFlows.writeReviewCommentFlow())
                .addFlow(testStepFlows.sendReviewCommentFlow())
                .addFlow(testStepFlows.closeReviewCommentsFlow())
                .addFlow(testStepFlows.navigateAndRemoveScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_021", title = "Send Schedule for Approval")
    public void sendScheduleForApproval() {
        TestScenario scenario = scenario("Send Schedule For Approval Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createScheduleForTestingFlow())
                .addFlow(testStepFlows.addReviewersFlow())
                .addFlow(testStepFlows.sendAndSaveScheduleFlow())
                .addFlow(testStepFlows.removeInvalidReviewersFlow())
                .addFlow(testStepFlows.sendAndSaveScheduleFlow())
                .addFlow(testStepFlows.verifyScheduleIsCreatedFlow())
                .addFlow(testStepFlows.navigateAndRemoveScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_022", title = "Highlight testware packages and suites in the Schedule Edit")
    public void testwarePackagesAndSuitesHighlighting() {
        TestScenario scenario = scenario("Highlight testware packages and suites Scenario")
                .addFlow(testStepFlows.loginFlow())
                .addFlow(testStepFlows.displaySchedulesWithTestwareFlow())
                .addFlow(testStepFlows.openForEditScheduleWithHighlightedTestwareFlow())
                .addFlow(testStepFlows.moveCursorToAddNewItemInTheEditorFlow())
                .addFlow(testStepFlows.selectHighlightedTestwareFlow())
                .addFlow(testStepFlows.openSuitesPopupFlow())
                .addFlow(testStepFlows.deselectAddedSuitesFlow())
                .addFlow(testStepFlows.confirmSuitesSelectionFlow())
                .addFlow(testStepFlows.selectBnsiServerTestwareFlow())
                .addFlow(testStepFlows.refreshTestwareListFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_023", title = "Filter Schedules By Team")
    public void filterSchedulesByTeam() {
        TestScenario scenario = scenario("Filter Schedules By Team Scenario")
                .addFlow(testStepFlows.displaySchedulesFlow())
                .addFlow(testStepFlows.filterTableByTeamFlow())
                .addFlow(testStepFlows.showSchedulesForAllTeamsFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_024", title = "Edit Schedules with users with team authority")
    public void editScheduleForMultipleUsersSameTeam() {
        TestScenario scenario = scenario("Edit Schedules with users with team authority Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createScheduleForTestingFlow())
                .addFlow(testStepFlows.confirmSaveScheduleFlow())
                .addFlow(testStepFlows.moveToScheduleListFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.openScheduleToEditFlow())
                .addFlow(testStepFlows.editScheduleNameFlow())
                .addFlow(testStepFlows.openSendAndSaveDialogFlow())
                .addFlow(testStepFlows.confirmEditedScheduleSaveFlow())
                .addFlow(testStepFlows.loginWithNewUserFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.removeScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_025", title = "Attempt to Edit Schedules with user with no team authority")
    public void editScheduleForMultipleUsersDifferentTeams() {
        TestScenario scenario = scenario("Attempt to Edit Schedules with user with no team authority Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createScheduleForTestingFlow())
                .addFlow(testStepFlows.confirmSaveScheduleFlow())
                .addFlow(testStepFlows.loginWithNewUserFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.openScheduleToEditFlow())
                .addFlow(testStepFlows.editScheduleTeamNameFlow())
                .addFlow(testStepFlows.openSendAndSaveDialogFlow())
                .addFlow(testStepFlows.confirmEditedScheduleSaveFlow())
                .addFlow(testStepFlows.logoutFlow())
                .addFlow(testStepFlows.loginFlow())
                .addFlow(testStepFlows.displaySchedulesWithTestwareForUnauthorisedUserFlow())
                .addFlow(testStepFlows.loginWithNewUserFlow())
                .addFlow(testStepFlows.removeScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_026", title = "Review schedules based on teams that a user has review permissions for")
    public void reviewScheduleByUserTeamPermissions() {
        TestScenario scenario = scenario("Review schedules based on teams that a user has review permissions for Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createScheduleForTestingFlow())
                .addFlow(testStepFlows.confirmSaveScheduleFlow())
                .addFlow(testStepFlows.loginWithNewUserFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.moveToScheduleViewReviewIconDisplayedFlow())
                .addFlow(testStepFlows.moveToScheduleListFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.openScheduleToEditFlow())
                .addFlow(testStepFlows.editScheduleTeamNameFlow())
                .addFlow(testStepFlows.openSendAndSaveDialogFlow())
                .addFlow(testStepFlows.confirmEditedScheduleSaveFlow())
                .addFlow(testStepFlows.logoutFlow())
                .addFlow(testStepFlows.loginFlow())
                .addFlow(testStepFlows.displaySchedulesWithTestwareForUnauthorisedUserFlow())
                .addFlow(testStepFlows.moveToScheduleViewReviewIconNotDisplayedFlow())
                .addFlow(testStepFlows.changeScheduleViewVersionFlow())
                .addFlow(testStepFlows.displayApproveFormFlow())
                .addFlow(testStepFlows.changeScheduleViewLatestVersionFlow())
                .addFlow(testStepFlows.loginWithNewUserFlow())
                .addFlow(testStepFlows.removeScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_027", title = "Create and edit an unvalidated schedule")
    public void createEditUnvalidatedSchedule() {
        TestScenario scenario = scenario("Create and edit an unvalidated schedule Scenario")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.createInvalidScheduleForTestingFlow())
                .addFlow(testStepFlows.confirmSaveInvalidScheduleFlow())
                .addFlow(testStepFlows.moveToScheduleListFlow())
                .addFlow(testStepFlows.displaySchedulesEditButtonDisplayedFlow())
                .addFlow(testStepFlows.openScheduleToEditFlow())
                .addFlow(testStepFlows.editScheduleXmlFlow())
                .addFlow(testStepFlows.openSendAndSaveDialogFlow())
                .addFlow(testStepFlows.confirmEditedScheduleSaveFlow())
                .addFlow(testStepFlows.navigateAndRemoveScheduleByNameFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_028", title = "Create schedule with <include> tags that includes another schedule")
    public void createScheduleWithIncludeTags() {
        TestScenario scenario = scenario("Create schedule with <include> tags that includes another schedule")
                .addFlow(testStepFlows.moveToScheduleCreationFlow())
                .addFlow(testStepFlows.includeScheduleFlow())
                .addFlow(testStepFlows.openSendAndSaveDialogFlow())
                .addFlow(testStepFlows.saveScheduleFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_029", title = "Display KGB+N schedules and navigate to create KGB+N schedule")
    public void displayKgbSchedules_andVerifyKgbModeIsEnabledInForm() {
        TestScenario scenario = scenario("Verify KGB+N functionality")
                .addFlow(testStepFlows.loginFlow())
                .addFlow(testStepFlows.verifyKgbFunctionalityFlow())
                .addFlow(testStepFlows.logoutFlow())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scheduler_030", title = "Navigate documentation pages")
    public void navigateDocumentation() {
        TestScenario scenario = scenario("Navigate documentation pages")
                .addFlow(testStepFlows.loginFlow())
                .addFlow(testStepFlows.navigateDocumentationFlow())
                .build();
        runner().build().start(scenario);
    }

    private void validateSchedule(String scheduleName, String expectedError) {
        // login
        scheduleOperator.login();
        scheduleOperator.selectDrop(DROP_WITH_TESTWARE);

        // create schedule
        scheduleOperator.goToCreateSchedule();
        String xml = getScheduleXml(scheduleName);
        enterScheduleDetailsAndVerify(scheduleName, SCHEDULE_TYPE, xml, SCHEDULE_TEAM, scheduleOperator);

        scheduleOperator.validateSchedule();
        scheduleOperator.waitUntilToastShown();

        // check that validation errors are marked
        assertTrue(scheduleOperator.hasScheduleEditPageErrorMarkers());
        assertTrue(scheduleOperator.isScheduleEditPageWarningDisplayed());

        // TODO: VIL: Verify validation error messages.
        // TODO: Moving mouse over warning icon currently is not working on Selenium Grid due to "Could not load
        //       native events component" error.

        // logout
        scheduleOperator.waitForToastsToClear();
        scheduleOperator.logout();
    }

    // TODO: VIL: direct link to create page (/#/schedules/create) + passing product/iso/drop via URL
    private void enterScheduleDetailsAndVerify(String name, String type, String xml, String teamName, ScheduleOperator scheduleOperator) {
        enterScheduleDetails(name, type, xml, teamName, scheduleOperator);
        verifyScheduleInput(name, type, xml, teamName, scheduleOperator);
    }

    private void enterScheduleDetails(String name, String type, String xml, String teamName, ScheduleOperator scheduleOperator) {
        scheduleOperator.setScheduleFormNameText(name);
        scheduleOperator.setScheduleFormTypeDropdown(type);
        scheduleOperator.setScheduleTeamNameDropdown(teamName);
        scheduleOperator.setScheduleFormXml(xml);
    }

    private void verifyScheduleInput(String expectedName, String expectedType, String expectedXml, String expectedTeam, ScheduleOperator scheduleOperator) {
        assertThat(scheduleOperator.getScheduleFormNameText(), is(expectedName));
        assertThat(scheduleOperator.getScheduleFormType(), is(expectedType));
        assertThat(scheduleOperator.getScheduleFormTeam(), is(expectedTeam));
        assertThat(scheduleOperator.getScheduleFormXml(), equalTo(expectedXml));
    }

    // for edit - type may not be changed when editing
    private void enterScheduleDetailsAndVerify(String name, String xml, ScheduleOperator scheduleOperator) {
        enterScheduleDetails(name, xml, scheduleOperator);
        verifyScheduleInput(name, xml, scheduleOperator);
    }

    // for edit - type may not be changed when editing
    private void enterScheduleDetails(String name, String xml, ScheduleOperator scheduleOperator) {
        scheduleOperator.setScheduleFormNameText(name);
        scheduleOperator.setScheduleFormXml(xml);
    }

    // for edit - type may not be changed when editing
    private void verifyScheduleInput(String expectedName, String expectedXml, ScheduleOperator scheduleOperator) {
        assertThat(scheduleOperator.getScheduleFormNameText(), is(expectedName));
        assertThat(scheduleOperator.getScheduleFormXml(), equalTo(expectedXml));
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

}
