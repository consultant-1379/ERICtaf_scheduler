package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleValidationResult;
import com.ericsson.cifwk.taf.scheduler.api.dto.SimpleScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ValidationResults;
import com.ericsson.cifwk.taf.scheduler.api.validation.KgbScheduleNameIsUniqueForTeam;
import com.ericsson.cifwk.taf.scheduler.api.validation.ScheduleNameIsUniqueInDrop;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleExecutionService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleValidationService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.schema.SchemaService;
import com.ericsson.cifwk.taf.scheduler.constant.BuildType;
import com.ericsson.cifwk.taf.scheduler.model.ScheduleExecution;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ScheduleValidationService scheduleValidationService;

    @Autowired
    private ScheduleExecutionService executionService;

    @RequestMapping(method = RequestMethod.GET)
    public List<ScheduleInfo> getSchedules(@RequestParam("dropId") Long dropId) {
        return scheduleService.getLatestSchedulesByDrop(dropId);
    }

    @RequestMapping(value = "/kgb", method = RequestMethod.GET)
    public List<ScheduleInfo> getLatestKgbSchedules() {
        return scheduleService.getLatestKgbSchedules();
    }

    @RequestMapping(value = "/approved", method = RequestMethod.GET)
    public List<SimpleScheduleInfo> getApprovedScheduleSummaries(@RequestParam("product") String productName,
                                                                 @RequestParam("drop") String dropName,
                                                                 @RequestParam("type") Optional<String> type) {
        if (type.isPresent()) {
            return scheduleService.getApprovedSchedulesByDropAndType(productName, dropName, type.get());
        } else {
            return scheduleService.getApprovedSchedulesByDrop(productName, dropName);
        }
    }

    @RequestMapping(value = "/approved/kgb", method = RequestMethod.GET)
    public List<SimpleScheduleInfo> getApprovedKgbSummaries(@RequestParam("team") Optional<String> maybeTeam) {
        return scheduleService.getApprovedKgbSummaries(maybeTeam);
    }

    @RequestMapping(value = "/summaries", method = RequestMethod.GET)
    public List<SimpleScheduleInfo> getScheduleSummariesByIds(@RequestParam(value = "scheduleIds") List<Long> scheduleIds) {
        return scheduleService.getScheduleSummariesByIds(scheduleIds);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ScheduleInfo> create(@ScheduleNameIsUniqueInDrop
                                                   @KgbScheduleNameIsUniqueForTeam
                                                   @Valid @RequestBody ScheduleInfo schedule) {

        ScheduleInfo created = scheduleService.create(schedule);
        if (created == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{scheduleId}", method = RequestMethod.GET)
    public ResponseEntity<ScheduleInfo> getLatestVersionOfSchedule(@PathVariable("scheduleId") long scheduleId) {
        Optional<ScheduleInfo> schedule = scheduleService.getSchedule(scheduleId);
        return schedule.isPresent() ? new ResponseEntity<>(schedule.get(), OK) : new ResponseEntity<>(NOT_FOUND);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.PUT)
    // PUT because angular cannot send a request body with DELETE
    public void deleteSchedule(@Valid @RequestBody ScheduleInfo schedule) {
        scheduleService.deleteAllVersions(schedule.getId());
    }

    @RequestMapping(value = "/{scheduleId}", method = RequestMethod.PUT)
    public ResponseEntity<ScheduleInfo> update(@PathVariable("scheduleId") long scheduleId,
                                               @ScheduleNameIsUniqueInDrop
                                               @KgbScheduleNameIsUniqueForTeam
                                               @Valid @RequestBody ScheduleInfo schedule) {

        Optional<ScheduleInfo> updatedSchedule = scheduleService.updateScheduleContent(scheduleId, schedule);
        if (updatedSchedule.isPresent()) {
            return new ResponseEntity<>(updatedSchedule.get(), OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{scheduleId}/versions", method = RequestMethod.GET)
    public List<ScheduleInfo> getScheduleVersions(@PathVariable("scheduleId") long scheduleId) {
        return scheduleService.getScheduleVersions(scheduleId);
    }

    @RequestMapping(value = "/{scheduleId}/versions/{version}", method = RequestMethod.GET)
    public ResponseEntity<ScheduleInfo> getScheduleVersion(@PathVariable("scheduleId") long originalScheduleId,
                                                           @PathVariable("version") int version) {

        Optional<ScheduleInfo> schedule = scheduleService.getScheduleVersion(originalScheduleId, version);
        return schedule.isPresent() ? new ResponseEntity<>(schedule.get(), OK) : new ResponseEntity<>(NOT_FOUND);
    }

    @RequestMapping(value = "/{scheduleId}/approval", method = RequestMethod.PUT)
    public ResponseEntity<ScheduleInfo> approveOrRejectSchedule(@PathVariable("scheduleId") long scheduleId,
                                                                @Valid @RequestBody ScheduleInfo schedule) {

        ScheduleInfo updated = scheduleService.approveOrReject(scheduleId, schedule);
        return new ResponseEntity<>(updated, CREATED);
    }

    @RequestMapping(value = "/{scheduleId}/versions/{version}/approval", method = RequestMethod.DELETE)
    public ResponseEntity<ScheduleInfo> revokeApproval(@PathVariable("scheduleId") long originalScheduleId,
                                                       @PathVariable("version") int version) {

        ScheduleInfo updated = scheduleService.revokeApproval(originalScheduleId, version);
        return new ResponseEntity<>(updated, OK);
    }

    @RequestMapping(value = "/{scheduleId}/versions/{version}/reviewers", method = RequestMethod.POST)
    public ResponseEntity<UserInfo> addReviewer(@PathVariable("scheduleId") long originalScheduleId,
                                                @PathVariable("version") int version,
                                                @Valid @RequestBody UserInfo reviewer) {
        Optional<UserInfo> addedReviewer = scheduleService.addReviewer(originalScheduleId, version, reviewer);
        return addedReviewer.isPresent() ? new ResponseEntity<>(addedReviewer.get(), OK) : new ResponseEntity<>(NOT_FOUND);
    }

    /**
     * Deletes a reviewer from a schedule version.
     *
     * @param userId user external id or email
     */
    @RequestMapping(value = "/{scheduleId}/versions/{version}/reviewers/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<UserInfo> deleteReviewer(@PathVariable("scheduleId") long originalScheduleId,
                                                   @PathVariable("version") int version,
                                                   @PathVariable("userId") String userId) {
        Optional<UserInfo> schedule = scheduleService.deleteReviewer(originalScheduleId, version, userId);
        return schedule.isPresent() ? new ResponseEntity<>(schedule.get(), OK) : new ResponseEntity<>(NOT_FOUND);
    }

    @RequestMapping(value = "/{scheduleId}/versions/{version}/reviewers", method = RequestMethod.GET)
    public Set<UserInfo> getReviewers(@PathVariable("scheduleId") long originalScheduleId,
                                      @PathVariable("version") int version) {
        return scheduleService.getReviewers(originalScheduleId, version);
    }

    @RequestMapping(value = "/{scheduleId}/comments", method = RequestMethod.GET)
    public List<CommentInfo> getComments(@PathVariable("scheduleId") long scheduleId) {
        return scheduleService.getComments(scheduleId);
    }

    @RequestMapping(value = "/{scheduleId}/versions/{version}/comments",
            method = RequestMethod.POST,
            consumes = {"application/json;charset=UTF-8"})
    public ResponseEntity<CommentInfo> addComment(@PathVariable("scheduleId") long scheduleId,
                                                  @PathVariable("version") int version,
                                                  @RequestBody String message) {
        Optional<CommentInfo> comment = scheduleService.addComment(scheduleId, version, message);
        return comment.isPresent() ? new ResponseEntity<>(comment.get(), CREATED) : new ResponseEntity<>(NOT_FOUND);
    }

    @RequestMapping(value = "/schema", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getScheduleSchema() throws IOException {
        String scheduleSchema = schemaService.getScheduleSchemaAsString();
        return new ResponseEntity<>(scheduleSchema, HttpStatus.OK);
    }

    @RequestMapping(value = "/validation", method = RequestMethod.POST)
    public ResponseEntity<List<ScheduleValidationResult>> validateSchedules(@RequestBody ScheduleInfo[] schedules) throws IOException {
        ValidationResults validationResults = scheduleValidationService.validate(Lists.newArrayList(schedules));
        return new ResponseEntity<>(validationResults.getResults(), validationResults.areValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{name}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getScheduleXML(@PathVariable("name") String scheduleName,
                                                 @RequestParam(value = "version", required = false) String version,
                                                 @RequestParam(value = "drop") String dropName) {
        String scheduleXml = scheduleService.getScheduleXml(dropName, scheduleName, version);
        return new ResponseEntity<>(scheduleXml, HttpStatus.OK);
    }

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public Map<Integer, String> getTypes() {
        return BuildType.getTypes();
    }

    @RequestMapping(value = "/teams", method = RequestMethod.GET)
    public List<String> getAllTeams() {
        return scheduleService.getAllTeams();
    }

    @RequestMapping(value = "/executions", method = RequestMethod.POST)
    public ResponseEntity createScheduleExecution(@RequestParam("scheduleId") Long scheduleId,
                                                  @RequestParam("productIsoVersion") String productIsoVersion,
                                                  @RequestParam("testwareIsoVersion") String testwareIsoVersion) {

        Optional<ScheduleExecution> created = executionService.createExecution(scheduleId, productIsoVersion, testwareIsoVersion);
        if (!created.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
