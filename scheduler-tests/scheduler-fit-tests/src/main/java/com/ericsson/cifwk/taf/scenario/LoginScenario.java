package com.ericsson.cifwk.taf.scenario;

import com.ericsson.cifwk.taf.HostResolver;
import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.operators.DropSelection;
import com.ericsson.cifwk.taf.operators.ScheduleOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class LoginScenario extends TafTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginScenario.class);

    private static final DropSelection DEFAULT_DROP = new DropSelection("ENM", "3.0");
    private static final DropSelection CUSTOM_DROP = new DropSelection("ENM", "1.0.enm.early");

    @Inject
    private ScheduleOperator scheduleOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        scheduleOperator.init(HostResolver.resolve());
    }

    @Test
    @TestId(id = "TAF_Scheduler_001")
    public void login() {
        assertFalse(scheduleOperator.isLoggedIn());

        scheduleOperator.login();

        scheduleOperator.waitForScheduleListShown();
        assertTrue(scheduleOperator.isLoggedIn());

        scheduleOperator.logout();
        assertFalse(scheduleOperator.isLoggedIn());
    }

    @Test
    public void verifyInvalidUserLoginIsDenied() {
        assertFalse(scheduleOperator.isLoggedIn());

        scheduleOperator.login("INVALID_USER", "INVALID_PASSWORD");
        assertFalse(scheduleOperator.isLoggedIn());
    }

    @Test(enabled = false) // TODO: ejhnhng find out why this isn't working
    @TestId(id = "TAF_Scheduler_014")
    public void verifyLoginPopupAfterSessionTimeout() {
        // login
        scheduleOperator.login();

        // Open second tab
        LOGGER.info("Create a new tab");
        String activeTabDescriptor = scheduleOperator.getCurrentTabDescriptor();
        scheduleOperator.createNewTab();

        // Logout on second tab
        LOGGER.info("Logout from the new tab");
        scheduleOperator.logout();

        // Activate first tab
        LOGGER.info("Switch to first tab");
        scheduleOperator.switchTab(activeTabDescriptor);
        scheduleOperator.waitForScheduleListShown();

        // Change DROP for ISO selection
        String dropName = CUSTOM_DROP.getDrop();
        LOGGER.info("Select a drop {}", dropName);
        scheduleOperator.selectDrop(dropName);
        assertTrue(scheduleOperator.isLoginPopupShown());
        LOGGER.info("Login popup is shown: {}", scheduleOperator.isLoginPopupShown());

        // login again
        LOGGER.info("Login to application from login popup");
        scheduleOperator.login();
        assertFalse(scheduleOperator.isLoginPopupShown());
        LOGGER.info("Login popup is shown: {}", scheduleOperator.isLoginPopupShown());
        assertTrue(scheduleOperator.isScheduleListPageOpened());
        assertEquals(DEFAULT_DROP, scheduleOperator.getDropSelection());

        // Again change DROP for ISO selection
        LOGGER.info("Select a drop {}", dropName);
        scheduleOperator.selectDrop(dropName);
        assertFalse(scheduleOperator.isLoginPopupShown());
        assertEquals(CUSTOM_DROP, scheduleOperator.getDropSelection());
    }

}
