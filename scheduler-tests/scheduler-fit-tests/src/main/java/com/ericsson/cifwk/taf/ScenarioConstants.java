package com.ericsson.cifwk.taf;

import com.ericsson.cifwk.taf.operators.DropSelection;

public interface ScenarioConstants {

    String SCHEDULE_NAME = "Test_Schedule_";
    String SCHEDULE_TYPE = "MTE-V";
    String SCHEDULE_TEAM = "CI-TAF";
    String SCHEDULE_TEAM_UPDATED = "TOR-KAOS";
    String SCHEDULE_NAME_FOR_TESTWARE_CHANGES = "Schedule For Testware changes";
    DropSelection DROP = new DropSelection("ENM", "1.0.enm.early");
    DropSelection DROP_WITH_TESTWARE = new DropSelection("ENM", "15.14");
    String REVIEWER1_EMAIL = "tafuser1@ericsson.com";
    String REVIEWER2_SIGNUM = "taf2";
    String INVALID_USER = "invalidUser";
    String NEW_USER_LOGIN_DETAILS = "taf2";
}
