package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.api.dto.ValidationResults;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleValidationService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.schema.SchemaService;
import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleValidationResult;
import com.ericsson.cifwk.taf.scheduler.api.dto.TypeInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleControllerTest {

    @InjectMocks
    ScheduleController scheduleController;

    @Mock
    ScheduleService scheduleService;

    @Mock
    SchemaService schemaService;

    @Mock
    ScheduleInfo scheduleInfo;

    @Mock
    ScheduleInfo scheduleInfo2;

    @Mock
    Schedule schedule;

    @Mock
    ScheduleValidationService scheduleValidationService;

    @Mock
    ValidationResults validationResults;

    @Mock
    ValidationResults validationResults2;

    @Mock
    ScheduleValidationResult scheduleValidationResult;

    @Mock
    ScheduleValidationResult scheduleValidationResult2;

    @Mock
    UserInfo userInfo;

    @Mock
    User user;

    private static final String COMMENT_MESSAGE = "Text";

    @Before
    public void setUp() throws Exception {
        when(userInfo.getUserId()).thenReturn("taf1");
        when(userInfo.getName()).thenReturn("TafUser1");
        when(userInfo.getEmail()).thenReturn("tafuser1@ericsson.com");

        when(user.getExternalId()).thenReturn("taf1");
        when(user.getName()).thenReturn("TafUser1");
        when(user.getEmail()).thenReturn("tafuser1@ericsson.com");
        when(schedule.getReviewers()).thenReturn(getUsers());

        when(scheduleInfo.getXmlContent()).thenReturn("xml");
        when(scheduleInfo.getName()).thenReturn("name");
        when(scheduleInfo.getType()).thenReturn(new TypeInfo(1, "KGB+N"));
        when(scheduleInfo.getReviewers()).thenReturn(getUserInfoList());

        when(scheduleInfo2.getXmlContent()).thenReturn("xml2");
        when(scheduleInfo2.getName()).thenReturn("name2");
        when(scheduleInfo2.getType()).thenReturn(new TypeInfo(2, "Physical"));

        when(schemaService.getScheduleSchemaAsString()).thenReturn("schema");

        when(scheduleValidationService.validate(scheduleInfo)).thenReturn(scheduleValidationResult);
        when(scheduleValidationService.validate(Lists.newArrayList(scheduleInfo))).thenReturn(validationResults);
        when(validationResults.getResults()).thenReturn(Lists.newArrayList(scheduleValidationResult));

        when(scheduleValidationService.validate(Lists.newArrayList(scheduleInfo, scheduleInfo2)))
                .thenReturn(validationResults2);
        when(validationResults2.getResults()).thenReturn(Lists.newArrayList(scheduleValidationResult, scheduleValidationResult2));
    }

    @Test
    public void testCreate_shouldReturnBadRequestIfNotCreated() throws Exception {
        when(scheduleService.create(any(ScheduleInfo.class))).thenReturn(null);

        ResponseEntity<ScheduleInfo> response = scheduleController.create(new ScheduleInfo());
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldReturnHttpStatusCreatedWhenEntityIsCreated() {
        when(scheduleService.create(scheduleInfo)).thenReturn(scheduleInfo);


        ResponseEntity<ScheduleInfo> response = scheduleController.create(scheduleInfo);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldReturnLatestVersionOfSchedule() {
        when(scheduleService.getSchedule(1L)).thenReturn(Optional.of(scheduleInfo));

        ResponseEntity<ScheduleInfo> response = scheduleController.getLatestVersionOfSchedule(1L);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(scheduleInfo));
    }

    @Test
    public void shouldReturnNotFoundIfScheduleNotExists() {
        when(scheduleService.getSchedule(1L)).thenReturn(Optional.empty());

        ResponseEntity<ScheduleInfo> response = scheduleController.getLatestVersionOfSchedule(1L);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), nullValue());
    }

    @Test
    public void shouldDeleteAllVersion() {
        ScheduleInfo dto = new ScheduleInfo();
        dto.setId(1L);
        scheduleController.deleteSchedule(dto);

        verify(scheduleService, times(1)).deleteAllVersions(1L);
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToUpdateNotExistingSchedule() {
        when(scheduleService.updateScheduleContent(eq(1L), any())).thenReturn(Optional.empty());

        ResponseEntity<ScheduleInfo> response = scheduleController.update(1L, scheduleInfo);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), nullValue());
    }

    @Test
    public void shouldReturnOKWhenUpdateSuccess() {
        when(scheduleService.updateScheduleContent(1L, scheduleInfo)).thenReturn(Optional.of(scheduleInfo2));

        ResponseEntity<ScheduleInfo> response = scheduleController.update(1L, scheduleInfo);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(scheduleInfo2));
    }

    @Test
    public void shouldReturnAllVersion() {
        when(scheduleService.getScheduleVersions(1L)).thenReturn(Lists.newArrayList(scheduleInfo, scheduleInfo2));

        List<ScheduleInfo> response = scheduleController.getScheduleVersions(1L);

        assertThat(response, hasItems(scheduleInfo, scheduleInfo2));
    }

    @Test
    public void shouldReturnScheduleSchema() throws IOException {
        ResponseEntity<String> scheduleSchema = scheduleController.getScheduleSchema();

        assertThat(scheduleSchema.getBody(), is("schema"));
    }

    @Test
    public void shouldReturnBadRequestWhenSchedulesInvalid() throws IOException {
        when(validationResults.areValid()).thenReturn(false);
        ResponseEntity<List<ScheduleValidationResult>> response = scheduleController.validateSchedules(new ScheduleInfo[]{scheduleInfo});

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        List<ScheduleValidationResult> results = response.getBody();
        assertThat(results.size(), equalTo(1));
        assertThat(results.get(0), is(scheduleValidationResult));

        response = scheduleController.validateSchedules(new ScheduleInfo[]{scheduleInfo, scheduleInfo2});
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        results = response.getBody();
        assertThat(results.size(), equalTo(2));
        assertThat(results.get(0), is(scheduleValidationResult));
        assertThat(results.get(1), is(scheduleValidationResult2));
    }

    @Test
    public void shouldReturnOKWhenScheduleValid() throws IOException {
        when(validationResults.areValid()).thenReturn(true);
        ResponseEntity<List<ScheduleValidationResult>> response = scheduleController.validateSchedules(new ScheduleInfo[]{scheduleInfo});

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnReviewers() {
        when(scheduleService.getReviewers(1L, 1)).thenReturn(Sets.newHashSet(userInfo));

        Set<UserInfo> reviewers = scheduleController.getReviewers(1L, 1);
        assertThat(reviewers, hasItem(userInfo));
    }

    @Test
    public void shouldReturnOKWhenReviewerAdded() {
        when(scheduleService.addReviewer(1L, 1, userInfo)).thenReturn(Optional.of(userInfo));

        ResponseEntity<UserInfo> response = scheduleController.addReviewer(1L, 1, userInfo);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnOKWhenReviewerRemoved() {
        when(scheduleService.deleteReviewer(1L, 1, "user1")).thenReturn(Optional.of(userInfo));

        ResponseEntity<UserInfo> response = scheduleController.deleteReviewer(1L, 1, "user1");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void addReviewer_shouldReturnNotFoundWhenScheduleNotExists() {
        when(scheduleService.addReviewer(1L, 1, userInfo)).thenReturn(Optional.empty());

        ResponseEntity<UserInfo> response = scheduleController.addReviewer(1L, 1, userInfo);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldReturnCreatedWhenCommentAdded() {
        when(scheduleService.addComment(1L, 1, COMMENT_MESSAGE)).thenReturn(Optional.of(new CommentInfo()));

        ResponseEntity<CommentInfo> response = scheduleController.addComment(1L, 1, COMMENT_MESSAGE);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void addComment_shouldReturnNotFoundWhenScheduleNotExists() {
        when(scheduleService.addComment(1L, 1, COMMENT_MESSAGE)).thenReturn(Optional.empty());

        ResponseEntity<CommentInfo> response = scheduleController.addComment(1L, 1, COMMENT_MESSAGE);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    private Set<User> getUsers() {
        Set<User> users = Sets.newHashSet(user);
        return users;
    }

    private Set<UserInfo> getUserInfoList() {
        Set<UserInfo> userInfoList = Sets.newHashSet(userInfo);
        return userInfoList;
    }
}
