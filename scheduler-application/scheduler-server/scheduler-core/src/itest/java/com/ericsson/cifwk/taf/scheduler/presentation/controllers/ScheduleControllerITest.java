package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleValidationResult;
import com.ericsson.cifwk.taf.scheduler.api.dto.SimpleScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TypeInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.application.security.SecurityMock;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class ScheduleControllerITest {

    private static final Long SECOND_SCHEDULE = 2L;

    private static final Long ENM_EARLY_MTE_SCHEDULE_1 = 1L;

    private static final Long ENM_EARLY_MTE_SCHEDULE_2 = 8L;

    private static final Long ENM_EARLY_RFA_SCHEDULE = 10L;

    private static final String APPROVER = "emihvol";

    private static final String SCHEDULE_APPROVAL_MSG = "This schedule is approved";

    private static final String REVIEWER1_ID = "egergle";
    private static final String REVIEWER2_EMAIL = "tafuser2@ericsson.com";
    private static final String REVIEWER3_EMAIL = "vladimirs.iljins@ericsson.com";
    private static final String INVALID_EMAIL = "invalid_email_address";

    private static final String COMMENT_TEXT_1 = "First comment text";
    private static final String COMMENT_TEXT_2 = "Second comment text";
    private static final String COMMENT_TEXT_3 = "Third comment text";
    private static final String TEAM_CI_TAF = "CI-TAF";
    private static final String TEAM_TOR_DOOZERS = "TOR-Doozers";
    private static final String TEAM_TOR_KAOS = "TOR-KAOS";

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    Filter springSecurityFilterChain;

    MockMvc mockMvc;

    ConfiguredGson gson;

    ObjectMapper om;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        gson = new ConfiguredGson();
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SecurityMock.mockPrincipal("emihvol");
    }

    @Test
    public void create() throws Exception {

        // preconditions
        ScheduleInfo schedule = createSchedule();
        assertNull(schedule.getId());
        assertNull(schedule.getDrop().getId());

        // execution
        String json = gson.toJson(schedule);
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        // assertions
        ScheduleInfo created = readSchedule(mvcResult);
        assertNotNull(created.getId());
        assertNotNull(created.getDrop().getId());
        assertEquals(created.getType().getName(), "Physical-E");
        assertThat(created.getVersion(), is(1));
        assertThat(created.getTeam(), is(TEAM_CI_TAF));
        assertThat(created.isValid(), is(true));
    }

    @Test
    public void create_shouldReturnBadRequestIfDropNotFound() throws Exception {
        // preconditions
        ScheduleInfo schedule = createSchedule();
        schedule.getDrop().setProductName("ENM");
        schedule.getDrop().setName("no.drop.here");

        // execution
        String json = gson.toJson(schedule);
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_shouldReturnBadRequestIfTeamNotSet() throws Exception {
        // preconditions
        ScheduleInfo schedule = createSchedule();
        schedule.setTeam("");

        // execution
        String json = gson.toJson(schedule);
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_shouldReturnBadRequestIfTeamNotInAuthorities() throws Exception {
        // preconditions
        ScheduleInfo schedule = createSchedule();
        schedule.setTeam("ANOTHER_TEAM");

        // execution
        String json = gson.toJson(schedule);
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_shouldReturnBadRequestIf_scheduleNameIsNotUniqueInDrop() throws Exception {
        ScheduleInfo schedule = createSchedule();
        schedule.setName("duplicate_test");

        String json = gson.toJson(schedule);
        MvcResult result = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleInfo created = readSchedule(result);

        // creation should fail as a schedule with name 'duplicate_test' already exists
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // delete schedule 'duplicate_test'
        mockMvc.perform(put("/api/schedules/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(created)));

        // should be able to create another schedule with name 'duplicate_test' as previous was deleted
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    public void create_shouldReturnBadRequestIf_KgbScheduleNameIsNotUniqueForTeam() throws Exception {
        ScheduleInfo schedule = createSchedule();
        schedule.setTeam("CI-TAF");
        schedule.setDrop(null); // KGB+N schedules have no associated drop
        schedule.setName("duplicate_test");

        String json = gson.toJson(schedule);
        MvcResult result = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleInfo created = readSchedule(result);

        // creation should fail as a schedule with name 'duplicate_test' already exists for team CI-TAF
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // delete schedule 'duplicate_test'
        mockMvc.perform(put("/api/schedules/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(created)));

        // should be able to create another schedule with name 'duplicate_test' as previous was deleted
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    public void update() throws Exception {

        // persisting original schedule version 1
        ScheduleInfo schedule = createSchedule();
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isCreated())
                .andReturn();
        ScheduleInfo original = readSchedule(mvcResult);

        // changing logged in user
        SecurityMock.mockPrincipal("new_user");

        // updating schedule
        schedule.setXmlContent("<updated_xml/>");
        schedule.setName("updatedName");
        schedule.setType(new TypeInfo(6));
        schedule.setTeam(TEAM_TOR_KAOS);

        mvcResult = mockMvc.perform(put("/api/schedules/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isOk())
                .andReturn();
        ScheduleInfo updated = readSchedule(mvcResult);

        // assertions
        assertEquals(original.getId(), updated.getId());
        assertEquals("<updated_xml/>", updated.getXmlContent());
        assertEquals("updatedName", updated.getName());
        assertEquals("Physical-Entry-Loop", updated.getType().getName());
        assertEquals(2, updated.getVersion().intValue());
        assertEquals(TEAM_TOR_KAOS, updated.getTeam());


        assertEquals(original.getCreatedBy(), updated.getCreatedBy());
        assertNotEquals(original.getUpdated(), updated.getUpdated());
        assertNotEquals(original.getUpdatedBy(), updated.getUpdatedBy());

        // checking idempotency
        mvcResult = mockMvc.perform(put("/api/schedules/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updated)))
                .andExpect(status().isOk())
                .andReturn();
        updated = readSchedule(mvcResult);

        assertEquals(original.getId(), updated.getId());
        assertEquals(2, updated.getVersion().intValue());
    }

    @Test
    public void update_shouldReturnBadRequestIfTeamNotInAuthorities() throws Exception {

        // persisting original schedule version 1
        ScheduleInfo schedule = createSchedule();
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isCreated())
                .andReturn();
        ScheduleInfo created = readSchedule(mvcResult);

        // updating schedule - setting team for which user has no role
        created.setXmlContent("<updated_xml/>");
        created.setName("updatedName");
        created.setType(new TypeInfo(6));
        created.setTeam(TEAM_TOR_DOOZERS);

        mockMvc.perform(put("/api/schedules/{id}", created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(created)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // updating schedule - setting team for which user has a role
        created.setTeam(TEAM_TOR_KAOS);

        mockMvc.perform(put("/api/schedules/{id}", created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(created)))
                .andExpect(status().isOk())
                .andReturn();
        ScheduleInfo updated = readSchedule(mvcResult);

        // updating schedule - setting another team for which user has a role
        updated.setTeam(TEAM_CI_TAF);

        mockMvc.perform(put("/api/schedules/{id}", updated.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updated)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void update_shouldReturnBadRequest_whenTryingToEditAnotherTeamsSchedule() throws Exception {
        final long DOOZERS_SCHEDULE_ID = 4;
        final int DOOZERS_SCHEDULE_VERSION = 1;

        MvcResult mvcResult = mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions/{version}", DOOZERS_SCHEDULE_ID, DOOZERS_SCHEDULE_VERSION))
                .andExpect(status().isOk())
                .andReturn();
        ScheduleInfo doozersSchedule = readSchedule(mvcResult);

        // updating schedule with one of current user's roles none of which correspond to the persisted schedule's team
        doozersSchedule.setTeam(TEAM_CI_TAF);

        // try to persist changes
        mockMvc.perform(put("/api/schedules/{id}", doozersSchedule.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(doozersSchedule)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void update_scheduleVersions_canHaveSameName() throws Exception {
        // preconditions
        ScheduleInfo schedule = createSchedule();

        // create schedule
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleInfo version1 = readSchedule(mvcResult);

        // update and show that different schedule versions can have the same name
        version1.setXmlContent("<updated_xml/>");
        mvcResult = mockMvc.perform(put("/api/schedules/{id}", version1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(version1)))
                .andExpect(status().isOk())
                .andReturn();
        // the originally created schedule is now up to version 2
        ScheduleInfo version2 = readSchedule(mvcResult);

        // now create a schedule with a different name
        ScheduleInfo testSchedule = createSchedule("test-schedule", null);
        mvcResult = mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testSchedule)))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleInfo testScheduleVersion1 = readSchedule(mvcResult);

        // update
        testScheduleVersion1.setXmlContent("<updated_xml/>");
        mvcResult = mockMvc.perform(put("/api/schedules/{id}", testScheduleVersion1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testScheduleVersion1)))
                .andExpect(status().isOk())
                .andReturn();

        ScheduleInfo testScheduleVersion2 = readSchedule(mvcResult);

        // now set this schedule name equal to the original schedule name, i.e. 'sample-schedule'
        // both schedules now have the same name and version
        // update should fail however because the originalId of both schedules is different
        testScheduleVersion2.setName("sample-schedule");
        mockMvc.perform(put("/api/schedules/{id}", testScheduleVersion2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testScheduleVersion1)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void delete_shouldReturnBadRequest_whenTryingToDeleteAnotherTeamsSchedule() throws Exception {
        final long DOOZERS_SCHEDULE_ID = 4;
        final int DOOZERS_SCHEDULE_VERSION = 1;

        MvcResult mvcResult = mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions/{version}", DOOZERS_SCHEDULE_ID, DOOZERS_SCHEDULE_VERSION))
                .andExpect(status().isOk())
                .andReturn();
        ScheduleInfo doozersSchedule = readSchedule(mvcResult);

        // try to delete schedule that doesn't belong to us
        mockMvc.perform(put("/api/schedules/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(doozersSchedule)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getLatestVersionOfSchedule() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        ScheduleInfo schedule = readSchedule(mvcResult);
        assertNotNull(schedule.getId());
        assertNotNull(schedule.getName());
        assertNotNull(schedule.getType());
        assertNotNull(schedule.getVersion());
        assertNotNull(schedule.getXmlContent());
        assertNotNull(schedule.getTeam());
    }

    @Test
    public void getLatestVersionOfSchedule_nonExistingScheduleId() throws Exception {
        mockMvc.perform(get("/api/schedules/{id}", "42"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getLatestVersionOfSchedule_invalidScheduleId() throws Exception {
        mockMvc.perform(get("/api/schedules/{id}", "invalid"))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    public void getSchedulesByDrop() throws Exception {
        final Long DROP_ID = 1L;
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules")
                .param("dropId", DROP_ID.toString()))
                .andExpect(status().isOk())
                .andReturn();

        List<ScheduleInfo> scheduleList = readSchedules(mvcResult);

        // assertions
        assertThat(scheduleList.size(), is(3));

        for (ScheduleInfo schedule : scheduleList) {
            assertEquals(schedule.getDrop().getId(), DROP_ID);
        }

        assertNotNull(scheduleList.get(0).getId());
        assertNotNull(scheduleList.get(0).getDrop());
        assertEquals(scheduleList.get(0).getName(), "First Schedule V3");
        assertEquals(scheduleList.get(0).getType().getName(), "MTE-V");
        assertEquals(scheduleList.get(0).getTeam(), TEAM_CI_TAF);

        //check that previous version numbers of this schedule are returned
        assertThat(scheduleList.get(0).getVersionList().size(), is(3));
        assertThat(scheduleList.get(0).getVersionList(), hasItems(1, 2, 3));

        assertNotNull(scheduleList.get(1).getId());
        assertNotNull(scheduleList.get(1).getDrop());
        assertEquals(scheduleList.get(1).getName(), "A Schedule to edit");
        assertEquals(scheduleList.get(1).getTeam(), TEAM_TOR_DOOZERS);
        assertEquals(scheduleList.get(1).getType().getName(), "MTE-V");
        assertThat(scheduleList.get(1).getVersion(), is(1));
        assertThat(scheduleList.get(1).getVersionList(), hasItem(1));

        assertNotNull(scheduleList.get(2).getId());
        assertNotNull(scheduleList.get(2).getDrop());
        assertEquals(scheduleList.get(2).getName(), "Another Schedule");
        assertEquals(scheduleList.get(2).getType().getName(), "RFA-P");
        assertEquals(scheduleList.get(2).getTeam(), TEAM_CI_TAF);
        assertThat(scheduleList.get(2).getVersion(), is(1));
        assertThat(scheduleList.get(2).getVersionList(), hasItem(1));
    }

    @Test
    public void getScheduleVersions() throws Exception {
        mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions", SECOND_SCHEDULE))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getApprovedScheduleSummaries() throws Exception {
        final String PRODUCT_NAME = "ENM";
        final String DROP_NAME = "1.0.enm.early";

        // show there are no approved schedules in drop
        List<SimpleScheduleInfo> scheduleSummaryList = getSummariesByProductAndDrop(PRODUCT_NAME, DROP_NAME);
        assertThat(scheduleSummaryList.size(), equalTo(0));

        // change approval status of all versions of schedule
        for (int i = 1; i < 4; i++) {
            ScheduleInfo schedule = getSchedule(ENM_EARLY_MTE_SCHEDULE_1, i);
            approveSchedule(ENM_EARLY_MTE_SCHEDULE_1, schedule);
        }

        // now get approved schedules by drop
        scheduleSummaryList = getSummariesByProductAndDrop(PRODUCT_NAME, DROP_NAME);
        assertThat(scheduleSummaryList.size(), equalTo(3));

        SimpleScheduleInfo summary1 = getSummaryByName("First Schedule", scheduleSummaryList);
        assertThat(summary1.getUrl(), containsString("/api/schedules/1/versions/1"));
        assertThat(summary1.getUrl(), containsString("localhost"));

        SimpleScheduleInfo summary2 = getSummaryByName("First Schedule V2", scheduleSummaryList);
        assertThat(summary2.getUrl(), containsString("/api/schedules/1/versions/2"));
        assertThat(summary2.getUrl(), containsString("localhost"));

        SimpleScheduleInfo summary3 = getSummaryByName("First Schedule V3", scheduleSummaryList);
        assertThat(summary3.getUrl(), containsString("/api/schedules/1/versions/3"));
        assertThat(summary3.getUrl(), containsString("localhost"));
    }

    @Test
    public void getApprovedScheduleSummariesByType() throws Exception {
        final String PRODUCT_NAME = "ENM";
        final String DROP_NAME = "1.0.enm.early";
        final String TYPE_OF_INTEREST = "MTE-V";

        // show there are no approved schedules of type MTE-V in drop
        List<SimpleScheduleInfo> scheduleSummaryList = getSummariesByProductDropAndType(PRODUCT_NAME, DROP_NAME, TYPE_OF_INTEREST);
        assertThat(scheduleSummaryList.size(), equalTo(0));

        // approve a schedule of type MTE-V
        ScheduleInfo schedule = getSchedule(ENM_EARLY_MTE_SCHEDULE_1, 3);
        approveSchedule(ENM_EARLY_MTE_SCHEDULE_1, schedule);

        // approve a schedule of type RFA-P
        schedule = getSchedule(ENM_EARLY_RFA_SCHEDULE, 1);
        approveSchedule(ENM_EARLY_RFA_SCHEDULE, schedule);

        // now get approved schedules by drop and type MTE-V
        scheduleSummaryList = getSummariesByProductDropAndType(PRODUCT_NAME, DROP_NAME, TYPE_OF_INTEREST);

        // assert that only schedule type MTE-V is returned
        assertThat(scheduleSummaryList.size(), equalTo(1));
        SimpleScheduleInfo summary = getSummaryByName("First Schedule V3", scheduleSummaryList);
        assertThat(summary.getType().getName(), equalTo(TYPE_OF_INTEREST));
    }

    @Test
    public void getScheduleSummariesByIds() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/schedules/summaries").param("scheduleIds", "1", "12", "13"))
                .andExpect(status().isOk())
                .andReturn();

        List<SimpleScheduleInfo> schedules = readScheduleSummaries(result);

        assertThat(schedules.size(), equalTo(2)); // only 12 and 13 are approved
        assertThat(schedules.get(0).getCreatedBy(), equalTo("ekirshe"));
        assertThat(schedules.get(1).getCreatedBy(), equalTo("ekirshe"));

        assertThat(schedules.get(0).getId(), equalTo(12L));
        assertThat(schedules.get(1).getId(), equalTo(13L));

        assertThat(schedules.get(0).getVersion(), equalTo(1));
        assertThat(schedules.get(1).getVersion(), equalTo(2));
    }

    private ScheduleInfo getSchedule(Long originalScheduleId, Integer version) throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions/{version}", originalScheduleId, version))
                .andExpect(status().isOk())
                .andReturn();

        return readSchedule(mvcResult);
    }

    private void approveSchedule(Long originalScheduleId, ScheduleInfo schedule) throws Exception {
        schedule.setApprovalStatus(ApprovalStatus.APPROVED.name());
        schedule.setApprovalMsg(SCHEDULE_APPROVAL_MSG);

        mockMvc.perform(put("/api/schedules/{id}/approval", originalScheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private SimpleScheduleInfo getSummaryByName(String name, List<SimpleScheduleInfo> simpleScheduleInfos) {
        return simpleScheduleInfos.stream().filter(s -> s.getName().equals(name)).findFirst().get();
    }

    private List<SimpleScheduleInfo> getSummariesByProductAndDrop(String product, String drop) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/approved")
                .param("product", product)
                .param("drop", drop))
                .andExpect(status().isOk())
                .andReturn();

        return readScheduleSummaries(mvcResult);
    }

    private List<SimpleScheduleInfo> getSummariesByProductDropAndType(String product, String drop, String type) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/approved")
                .param("product", product)
                .param("drop", drop)
                .param("type", type))
                .andExpect(status().isOk())
                .andReturn();

        return readScheduleSummaries(mvcResult);
    }

    private ScheduleInfo readSchedule(MvcResult mvcResult) throws IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), ScheduleInfo.class);
    }

    private List<ScheduleInfo> readSchedules(MvcResult mvcResult) throws IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ScheduleInfo>>() {
        });
    }

    private List<SimpleScheduleInfo> readScheduleSummaries(MvcResult mvcResult) throws IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<SimpleScheduleInfo>>() {
        });
    }

    private List<String> readTeams(MvcResult mvcResult) throws IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });
    }

    private ScheduleInfo createSchedule() {
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setName("sample-schedule");
        schedule.setType(new TypeInfo(5));
        schedule.setDrop(new DropInfo("ENM", "1.0.enm.early"));
        schedule.setXmlContent("<xml/>");
        schedule.setReviewers(createListOfReviewers());
        schedule.setTeam(TEAM_CI_TAF);
        schedule.setValid(true);
        return schedule;
    }

    private ScheduleInfo createSchedule(String name, Integer version) {
        ScheduleInfo schedule = createSchedule();
        schedule.setName(name);
        schedule.setVersion(version);
        return schedule;
    }

    @Test
    public void getScheduleSchema() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/schema"))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertNotNull(content);
        assertThat(content, containsString("XMLSchema"));
        assertThat(content, anyOf(endsWith("schema>\n"), endsWith("schema>\r\n"), endsWith("schema>")));
    }

    @Test
    public void validate_shouldReturnErrorsIfInvalidXml() throws Exception {
        List<ScheduleInfo> schedulesToValidate = Lists.newArrayList();

        ScheduleInfo schedule1 = createSchedule();
        schedule1.setXmlContent("<invalid/>");
        schedulesToValidate.add(schedule1);

        ScheduleInfo schedule2 = createSchedule();
        schedule2.setName("another-schedule");
        schedule2.setXmlContent("<notCorrect/>");
        schedulesToValidate.add(schedule2);

        String json = gson.toJson(schedulesToValidate);
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andReturn();

        ScheduleValidationResult[] validationResults = om.readValue(mvcResult.getResponse().getContentAsString(),
                ScheduleValidationResult[].class);

        assertThat(validationResults.length, equalTo(2));
        assertThat(validationResults[0].getSchedule().getName(), equalTo("sample-schedule"));
        assertFalse(validationResults[0].isValid());
        assertThat(validationResults[0].getSchemaErrors(), not(empty()));
        assertThat(validationResults[1].getSchedule().getName(), equalTo("another-schedule"));
        assertFalse(validationResults[1].isValid());
        assertThat(validationResults[1].getSchemaErrors(), not(empty()));
    }

    @Test
    public void getScheduleVersion() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions/{version}", ENM_EARLY_MTE_SCHEDULE_1, 3))
                .andExpect(status().isOk())
                .andReturn();

        ScheduleInfo schedule = readSchedule(result);
        assertNotNull(schedule.getId());
        assertThat(schedule.getVersion(), equalTo(3));
        assertThat(schedule.getName(), equalTo("First Schedule V3"));
        assertThat(schedule.getType().getName(), equalTo("MTE-V"));
        assertNotNull(schedule.getXmlContent());
        assertThat(schedule.getXmlContent(), Matchers.containsString("<name>ERICTAFeniq_cdb_setup_CXP9027959 3</name>"));
    }

    @Test
    public void getScheduleXML() throws Exception {
        final String SCHEDULE_NAME = "Third Schedule V2";
        final String DROP_NAME = "2.X.enm";

        // get schedule xml where no version is specified (latest version should be returned)
        MvcResult result = mockMvc
                .perform(get("/api/schedules/{name}/content/", SCHEDULE_NAME)
                        .param("drop", DROP_NAME))
                .andExpect(status().isOk())
                .andReturn();

        String scheduleXML = result.getResponse().getContentAsString();
        assertThat(scheduleXML, Matchers.containsString("<name>Schedule Items Latest Version</name>"));

        // get schedule xml where version is specified
        result = mockMvc
                .perform(get("/api/schedules/{name}/content/", SCHEDULE_NAME)
                        .param("version", "2")
                        .param("drop", DROP_NAME))
                .andExpect(status().isOk())
                .andReturn();

        scheduleXML = result.getResponse().getContentAsString();
        assertThat(scheduleXML, Matchers.containsString("<name>Schedule Items</name>"));
    }

    @Test
    public void changeScheduleApprovalStatus() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/api/schedules/{scheduleId}/versions/{version}", ENM_EARLY_MTE_SCHEDULE_1, 3))
                .andExpect(status().isOk())
                .andReturn();

        ScheduleInfo schedule = readSchedule(mvcResult);

        schedule.setApprovalStatus(ApprovalStatus.APPROVED.name());
        schedule.setApprovalMsg(SCHEDULE_APPROVAL_MSG);

        mvcResult = mockMvc.perform(put("/api/schedules/{id}/approval", ENM_EARLY_MTE_SCHEDULE_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(schedule)))
                .andExpect(status().isCreated())
                .andReturn();

        schedule = readSchedule(mvcResult);
        assertThat(schedule.getApprovalStatus(), equalTo(ApprovalStatus.APPROVED.name()));
        assertThat(schedule.getApprovedBy(), equalTo(APPROVER));
        assertThat(schedule.getApprovalMsg(), equalTo(SCHEDULE_APPROVAL_MSG));

        mvcResult = mockMvc
                .perform(delete("/api/schedules/{scheduleId}/versions/{version}/approval", ENM_EARLY_MTE_SCHEDULE_1, 3))
                .andExpect(status().isOk())
                .andReturn();

        schedule = readSchedule(mvcResult);
        assertThat(schedule.getVersion(), equalTo(3));
        assertThat(schedule.getApprovalStatus(), equalTo(ApprovalStatus.UNAPPROVED.name()));
        assertNull(schedule.getApprovedBy());
        assertNull(schedule.getApprovalMsg());
    }

    @Test
    public void addReviewer() throws Exception {
        List<UserInfo> reviewers = Arrays.asList(
                new UserInfo(REVIEWER1_ID, null, null),
                new UserInfo(null, null, REVIEWER2_EMAIL),
                new UserInfo(null, null, REVIEWER3_EMAIL)
        );

        for (UserInfo reviewer : reviewers) {
            mockMvc.perform(post("/api/schedules/{scheduleId}/versions/{version}/reviewers",
                    ENM_EARLY_MTE_SCHEDULE_1, 3)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gson.toJson(reviewer)))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        List<UserInfo> added = getReviewers();
        assertThat(added, hasSize(reviewers.size()));
        assertThat(added, Matchers.hasItem(hasProperty("userId", is(REVIEWER1_ID))));
        assertThat(added, Matchers.hasItem(hasProperty("email", is(REVIEWER2_EMAIL))));
        assertThat(added, Matchers.hasItem(hasProperty("email", is(REVIEWER3_EMAIL))));

        for (UserInfo reviewer : reviewers) {
            String userId = StringUtils.defaultIfEmpty(reviewer.getUserId(), reviewer.getEmail());
            mockMvc
                    .perform(delete("/api/schedules/{scheduleId}/versions/{version}/reviewers/{userId}/",
                            ENM_EARLY_MTE_SCHEDULE_1, 3, userId))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        assertThat(getReviewers(), empty());
    }

    @Test
    public void getAllTeamsInSchedules() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/teams"))
                .andExpect(status().isOk())
                .andReturn();

        List<String> teams = readTeams(mvcResult);

        assertThat(teams.size(), equalTo(4));
        assertThat(teams.get(0), equalTo("CI-TAF"));
        assertThat(teams.get(1), equalTo("TOR-Doozers"));
        assertThat(teams.get(2), equalTo("TOR-KAOS"));
        assertThat(teams.get(3), equalTo("ENM-Tribe7"));
    }

    @Test
    public void addReviewer_invalidEmail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules/{scheduleId}/versions/{version}/reviewers",
                ENM_EARLY_MTE_SCHEDULE_1, 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new UserInfo(null, null, INVALID_EMAIL))))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private List<UserInfo> getReviewers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/{scheduleId}/versions/{version}/reviewers",
                ENM_EARLY_MTE_SCHEDULE_1, 3))
                .andExpect(status().isOk())
                .andReturn();
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserInfo>>() {
        });
    }

    @Test
    public void addComment() throws Exception {
        CommentInfo comment = addComment(ENM_EARLY_MTE_SCHEDULE_1, 3, COMMENT_TEXT_1);

        assertThat(comment, notNullValue());
        assertThat(comment.getMessage(), is(COMMENT_TEXT_1));
        assertThat(comment.getScheduleVersion(), is(3));
        assertThat(comment.getCreated(), notNullValue());
        assertThat(comment.getCreatedBy(), notNullValue());

        comment = addComment(ENM_EARLY_MTE_SCHEDULE_1, 3, COMMENT_TEXT_2);

        assertThat(comment, notNullValue());
        assertThat(comment.getMessage(), is(COMMENT_TEXT_2));
    }

    @Test
    public void getComments_shouldReturnCommentsForAllVersions() throws Exception {
        addComment(ENM_EARLY_MTE_SCHEDULE_1, 2, COMMENT_TEXT_1);
        addComment(ENM_EARLY_MTE_SCHEDULE_1, 3, COMMENT_TEXT_2);
        addComment(ENM_EARLY_MTE_SCHEDULE_1, 3, COMMENT_TEXT_3);

        List<CommentInfo> comments = getComments(ENM_EARLY_MTE_SCHEDULE_1);
        assertThat(comments, hasSize(3));
        assertThat(comments.get(0).getMessage(), is(COMMENT_TEXT_3));
        assertThat(comments.get(1).getMessage(), is(COMMENT_TEXT_2));
        assertThat(comments.get(2).getMessage(), is(COMMENT_TEXT_1));
    }

    private CommentInfo addComment(Long scheduleId, int version, String message) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/schedules/{scheduleId}/versions/{version}/comments",
                scheduleId, version)
                .contentType(MediaType.APPLICATION_JSON)
                .content(message))
                .andExpect(status().isCreated())
                .andReturn();

        return readComment(mvcResult);
    }

    private List<CommentInfo> getComments(Long scheduleId) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/{scheduleId}/comments",
                scheduleId))
                .andExpect(status().isOk())
                .andReturn();
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<CommentInfo>>() {
        });
    }

    private CommentInfo readComment(MvcResult mvcResult) throws IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), CommentInfo.class);
    }

    @Test
    public void getScheduleTypes() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/types"))
                .andExpect(status().isOk())
                .andReturn();

        Map<Integer, String> types = readPhases(mvcResult);
        assertThat(types.size(), equalTo(20));
        assertEquals(types.get(1), "KGB+N");
        assertEquals(types.get(5), "Physical-E");
        assertEquals(types.get(10), "RNCDB");
        assertEquals(types.get(15), "Virtual");
        assertEquals(types.get(20), "Virtual2");

    }

    @Test
    public void shouldCreateScheduleExecutions() throws Exception {
        String url = "/api/schedules/executions?scheduleId={scheduleId}&productIsoVersion={productIsoVersion}&testwareIsoVersion={testwareIsoVersion}";
        final Long scheduleId = 14L;
        final String productIsoVersion = "1.12.68";
        final String testwareIsoVersion = "1.12.35";

        mockMvc.perform(post(url, scheduleId, productIsoVersion, testwareIsoVersion))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void getApprovedKgbScheduleSummaries() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/schedules/approved/kgb?team={team}", TEAM_CI_TAF))
                .andExpect(status().isOk())
                .andReturn();

        List<SimpleScheduleInfo> tafKgbSchedules = readScheduleSummaries(mvcResult);
        assertThat(tafKgbSchedules.size(), equalTo(1));
        assertThat(tafKgbSchedules.get(0).getName(), equalTo("KGB Schedule TAF"));
        assertThat(tafKgbSchedules.get(0).getVersion(), equalTo(1));

        mvcResult = mockMvc.perform(get("/api/schedules/approved/kgb?team={team}", TEAM_TOR_DOOZERS))
                .andExpect(status().isOk())
                .andReturn();

        List<SimpleScheduleInfo> doozersKgbSchedules = readScheduleSummaries(mvcResult);
        assertThat(doozersKgbSchedules.size(), equalTo(1));
        assertThat(doozersKgbSchedules.get(0).getName(), equalTo("KGB Schedule DOOZERS"));
        assertThat(doozersKgbSchedules.get(0).getVersion(), equalTo(1));

        // omit optional team parameter - all approved kgb schedules should be returned
        mvcResult = mockMvc.perform(get("/api/schedules/approved/kgb"))
                .andExpect(status().isOk())
                .andReturn();

        List<SimpleScheduleInfo> allKgbSchedules = readScheduleSummaries(mvcResult);
        assertThat(allKgbSchedules.size(), equalTo(2));
        assertThat(allKgbSchedules.get(0).getName(), equalTo("KGB Schedule TAF"));
        assertThat(allKgbSchedules.get(1).getName(), equalTo("KGB Schedule DOOZERS"));
    }

    private Set<UserInfo> createListOfReviewers() {
        UserInfo user = new UserInfo("taf1", "TafUser1", "tafuser1@ericsson.com");
        Set<UserInfo> reviewers = new HashSet<>();
        reviewers.add(user);
        return reviewers;
    }

    private Map<Integer, String> readPhases(MvcResult mvcResult) throws java.io.IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<Integer, String>>() {
        });
    }
}
