package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.constant.KgbConstant;
import com.ericsson.cifwk.taf.scheduler.application.security.SecurityMock;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class ScheduleRepositoryITest {

    private static final String TEAM_CI_TAF = "CI-TAF";
    private static final String TEAM_TOR_DOOZERS = "TOR-Doozers";
    private static final boolean SCHEDULE_VALIDATED = true;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    DropRepository dropRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldReturnAllEntitiesInDataMigration() {
        List<Schedule> all = scheduleRepository.findAll();
        assertThat(all.size(), is(17));
    }

    @Test
    public void shouldReturnLastScheduleVersionsForDrop() {
        Drop drop = dropRepository.findByProductAndDropNames("ENM", "1.0.enm.early");
        List<Schedule> byDrop = scheduleRepository.findLatestVersionsByDrop(drop);
        assertThat(byDrop.size(), is(3));

        assertThat(byDrop.stream().filter(s -> s.getName().equals("First Schedule V3")).count(), is(1L));
        assertThat(byDrop.stream().filter(s -> s.getName().equals("A Schedule to edit")).count(), is(1L));
        assertThat(byDrop.stream().filter(s -> s.getName().equals("Another Schedule")).count(), is(1L));
    }

    @Test
    public void shouldReturnAllApprovedSchedulesInDrop() {
        Drop drop = dropRepository.findByProductAndDropNames("ENM", "1.0.enm.early");
        List<Schedule> approved = scheduleRepository.findApprovedSchedulesByDrop(drop, ApprovalStatus.APPROVED);
        assertThat(approved.size(), equalTo(0));

        final long scheduleId = 1L;

        // just approve schedules
        for (int i = 1; i < 4; i++) {
            Schedule schedule = scheduleRepository.findVersion(scheduleId, i);
            schedule.approve("Approved");
            scheduleRepository.save(schedule);
        }

        approved = scheduleRepository.findApprovedSchedulesByDrop(drop, ApprovalStatus.APPROVED);
        assertThat(approved.size(), equalTo(3));
        assertThat(approved.stream().filter(s -> s.getName().equals("First Schedule V3")).count(), is(1L));
        assertThat(approved.stream().filter(s -> s.getName().equals("First Schedule V2")).count(), is(1L));
        assertThat(approved.stream().filter(s -> s.getName().equals("First Schedule")).count(), is(1L));
    }

    @Test
    public void shouldReturnAllApprovedSchedulesInDrop_OfAParticularType() {
        Drop drop = dropRepository.findByProductAndDropNames("ENM", "1.0.enm.early");
        final Integer TYPE = 3;
        List<Schedule> approved = scheduleRepository.findApprovedSchedulesByDropAndType(drop, TYPE, ApprovalStatus.APPROVED);
        assertThat(approved.size(), equalTo(0));

        final long scheduleId = 8L;
        Schedule schedule = scheduleRepository.findLastVersionByOriginalScheduleId(scheduleId);
        schedule.approve("Approved");
        scheduleRepository.save(schedule);

        approved = scheduleRepository.findApprovedSchedulesByDropAndType(drop, TYPE, ApprovalStatus.APPROVED);
        assertThat(approved.size(), equalTo(1));
        assertThat(approved.stream().filter(s -> s.getName().equals("A Schedule to edit")).count(), is(1L));
    }

    @Test
    public void shouldReturnLastScheduleVersion() {
        Schedule firstSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(1L);
        assertThat(firstSchedule.getVersion(), is(3));
        assertThat(firstSchedule.getId(), is(3L));

        Schedule secondSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(4L);
        assertThat(secondSchedule.getVersion(), is(2));
        assertThat(secondSchedule.getId(), is(5L));
        assertThat(secondSchedule.getOriginalId(), is(4L));
    }

    @Test
    public void shouldReturnListOfAllTeams() {
        List<String> teams = scheduleRepository.findAllTeams();
        assertThat(teams.size(), is(4));
    }

    @Test
    public void shouldReturnLatestSchedulesByCiBuildType() {
        List<Schedule> schedules = scheduleRepository.findByType(2);
        assertThat(schedules.size(), is(3));
    }

    @Test
    public void shouldReturnAllScheduleVersions() {
        List<Schedule> schedules = scheduleRepository.findAllVersions(4L);
        assertThat(schedules.size(), is(2));
    }

    @Test
    public void shouldReturnSpecifiedScheduleVersion() {
        Schedule thirdScheduleV1 = scheduleRepository.findVersion(4L, 1);
        assertThat(thirdScheduleV1.getVersion(), is(1));

        Schedule thirdScheduleV2 = scheduleRepository.findVersion(4L, 2);
        assertThat(thirdScheduleV2.getVersion(), is(2));
    }

    @Test
    public void shouldReturnNullAsScheduleVersionNotExists() {
        Schedule thirdScheduleV1 = scheduleRepository.findVersion(4L, 99);
        assertThat(thirdScheduleV1, nullValue());
    }

    @Test
    public void shouldCreateNewSchedule() {
        final String SCHEDULE_NAME = "New Schedule";
        Drop drop = dropRepository.findByProductAndDropNames("OSS", "1.0.oss");

        List<Schedule> byDrop = scheduleRepository.findLatestVersionsByDrop(drop);
        assertThat(byDrop.size(), equalTo(1));
        Optional<Schedule> noNewSchedule = byDrop.stream()
                .filter(s -> s.getName().equals(SCHEDULE_NAME))
                .findAny();
        assertFalse(noNewSchedule.isPresent());

        SecurityMock.mockPrincipal("enikoal");
        scheduleRepository.save(new Schedule(SCHEDULE_NAME, 1, "<xml/>", drop,
                ApprovalStatus.UNAPPROVED.name(), TEAM_CI_TAF, SCHEDULE_VALIDATED));

        byDrop = scheduleRepository.findLatestVersionsByDrop(drop);
        assertThat(byDrop.size(), is(2));
        Schedule newSchedule = byDrop.stream()
                .filter(s -> s.getName().equals(SCHEDULE_NAME))
                .findAny()
                .get();

        assertThat(newSchedule.getName(), is("New Schedule"));
        assertThat(newSchedule.getXml(), is("<xml/>"));
        assertThat(newSchedule.getType(), is(1));
    }

    @Test
    public void shouldCreateNewVersion() {
        Schedule firstSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(1L);

        Schedule newVersion = firstSchedule.createNewVersion();
        newVersion.setXml("newXml");
        newVersion.setName("newTitle");
        newVersion.setType(3);

        scheduleRepository.save(firstSchedule);
        scheduleRepository.save(newVersion);

        Schedule newVersionOfFirstSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(1L);
        assertThat(newVersionOfFirstSchedule.getVersion(), is(4));
        assertThat(newVersionOfFirstSchedule.getId(), is(18L));
        assertThat(newVersionOfFirstSchedule.getXml(), is("newXml"));
        assertThat(newVersionOfFirstSchedule.getName(), is("newTitle"));
        assertThat(newVersionOfFirstSchedule.getType(), is(3));
        assertThat(newVersionOfFirstSchedule.getTeam(), is(TEAM_CI_TAF));

        assertThat(newVersionOfFirstSchedule.getCreatedBy(), is(firstSchedule.getCreatedBy()));
        long originalCreateTime = firstSchedule.getCreated().getTime() / 1000;
        long originalCreateTimeInNewVersion = newVersionOfFirstSchedule.getCreated().getTime() / 1000;
        assertThat(originalCreateTimeInNewVersion, is(originalCreateTime));
    }

    @Test
    public void shouldReturnLatestSchedulesByName() {
        final String SCHEDULE_NAME = "Third Schedule V2";

        Schedule schedule = scheduleRepository.findLatestScheduleByName("2.X.enm", SCHEDULE_NAME);
        assertEquals(schedule.getName(), SCHEDULE_NAME);
        assertThat(schedule.getVersion(), is(3));
        assertThat(schedule.getXml(), Matchers.containsString("<name>Schedule Items Latest Version</name>"));
        assertThat(schedule.getType(), is(3));
        assertThat(schedule.getTeam(), is("TOR-KAOS"));
    }

    @Test
    public void shouldReturnScheduleByNameAndVersion() {
        final String SCHEDULE_NAME = "Third Schedule V2";

        Schedule schedule = scheduleRepository.findScheduleByNameAndVersion("2.X.enm", SCHEDULE_NAME, 2);
        assertEquals(schedule.getName(), SCHEDULE_NAME);
        assertThat(schedule.getVersion(), is(2));
        assertThat(schedule.getXml(), Matchers.containsString("<name>Schedule Items</name>"));
        assertThat(schedule.getType(), is(3));
        assertThat(schedule.getTeam(), is("TOR-KAOS"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldFailToCreateSameVersionSchedule() {
        Schedule secondSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(4L);

        Schedule newVersion1 = secondSchedule.createNewVersion();
        Schedule newVersion2 = secondSchedule.createNewVersion();

        scheduleRepository.save(secondSchedule);
        scheduleRepository.save(newVersion1);
        scheduleRepository.save(newVersion2);
    }

    @Test
    @Transactional
    public void delete() {

        // creating first version
        Drop drop = dropRepository.findByProductAndDropNames("ENM", "1.0.enm.early");
        Schedule firstSchedule = new Schedule("Name", 1, "<xml/>", drop,
                ApprovalStatus.UNAPPROVED.name(), TEAM_CI_TAF, SCHEDULE_VALIDATED);
        firstSchedule = scheduleRepository.save(firstSchedule);

        // creating next version
        Schedule secondSchedule = firstSchedule.createNewVersion();
        scheduleRepository.save(firstSchedule);
        scheduleRepository.save(secondSchedule);

        // precondition
        Long originalId = firstSchedule.getId();
        assertThat(scheduleRepository.findAllVersions(originalId), hasSize(2));

        // deletion
        scheduleRepository.deleteDependentVersions(originalId);
        scheduleRepository.deleteOriginalVersion(originalId);
        assertTrue(scheduleRepository.findAllVersions(originalId).isEmpty());
    }

    @Test
    @Transactional
    public void shouldNotReturnSchedulesMarkedAsDeleted() {
        List<Schedule> schedules = scheduleRepository.findAllVersions(1L);
        assertThat(schedules.size(), is(3));

        Schedule schedule = scheduleRepository.findLastVersionByOriginalScheduleId(1L);
        schedule.delete();
        scheduleRepository.save(schedule);

        schedules = scheduleRepository.findAllVersions(1L);
        assertThat(schedules.size(), is(2));

        Optional<Schedule> scheduleMarkedAsDeleted = schedules.stream()
                .filter(s -> s.getName().equals("First Schedule V3"))
                .findAny();
        assertFalse(scheduleMarkedAsDeleted.isPresent());
    }

    @Test
    @Transactional
    public void shouldAssignScheduleReviewers() {
        Schedule schedule = scheduleRepository.findVersion(1L, 3);
        assertThat(schedule.getReviewers(), is(empty()));

        Set<User> reviewers = new HashSet<>(2);
        reviewers.add(userRepository.findOne(1L));
        reviewers.add(userRepository.findOne(2L));
        schedule.setReviewers(reviewers);
        schedule = scheduleRepository.save(schedule);

        assertThat(schedule.getReviewers().size(), is(2));

        schedule.setReviewers(Collections.<User>emptySet());
        schedule = scheduleRepository.save(schedule);

        assertThat(schedule.getReviewers(), is(empty()));
    }

    @Test
    public void shouldFindApproved_schedulesByIds() {
        List<Schedule> schedules =
                scheduleRepository.findApprovedSchedulesByIds(Lists.newArrayList(1L, 2L, 12L, 13L, 14L));
        assertThat(schedules.size(), equalTo(3)); //1L and 2L aren't approved
        List<Long> approvedIds = Lists.newArrayList(12L, 13L, 14L); // Schedule for Trigger Plugin
        List<Long> returnedScheduleIds = schedules.stream().map(s -> s.getId()).collect(Collectors.toList());
        assertTrue(returnedScheduleIds.containsAll(approvedIds));
    }

    @Test
    public void shouldFindKgbSchedulesByTeam() {
        Drop kgbDrop = getKgbDrop();
        List<Schedule> ciTafKgbSchedules = scheduleRepository.findApprovedSchedulesByDropAndTeam(kgbDrop, TEAM_CI_TAF);
        assertThat(ciTafKgbSchedules.size(), equalTo(1));
        assertThat(ciTafKgbSchedules.get(0).getName(), equalTo("KGB Schedule TAF"));
        assertThat(ciTafKgbSchedules.get(0).getVersion(), equalTo(1));

        List<Schedule> doozersKgbSchedules = scheduleRepository.findApprovedSchedulesByDropAndTeam(kgbDrop, TEAM_TOR_DOOZERS);
        assertThat(doozersKgbSchedules.size(), equalTo(1));
        assertThat(doozersKgbSchedules.get(0).getName(), equalTo("KGB Schedule DOOZERS"));
        assertThat(doozersKgbSchedules.get(0).getVersion(), equalTo(1));
    }

    @Test
    public void shouldFindAllKgbSchedules() {
        Drop kgbDrop = getKgbDrop();
        List<Schedule> kgbSchedules = scheduleRepository.findApprovedSchedulesByDrop(kgbDrop);
        assertThat(kgbSchedules.size(), equalTo(2));
        assertThat(kgbSchedules.get(0).getName(), equalTo("KGB Schedule TAF"));
        assertThat(kgbSchedules.get(0).getVersion(), equalTo(1));
        assertThat(kgbSchedules.get(1).getName(), equalTo("KGB Schedule DOOZERS"));
        assertThat(kgbSchedules.get(1).getVersion(), equalTo(1));
    }

    @Test
    public void shouldFindSchedulesByDropAndTeam() {
        Drop kgbDrop = getKgbDrop();
        List<Schedule> tafSchedules = scheduleRepository.findSchedulesByDropAndTeam(kgbDrop, TEAM_CI_TAF);
        assertThat(tafSchedules.size(), equalTo(1));
        assertThat(tafSchedules.get(0).getId(), equalTo(16L));

        List<Schedule> doozersSchedules = scheduleRepository.findSchedulesByDropAndTeam(kgbDrop, TEAM_TOR_DOOZERS);
        assertThat(doozersSchedules.size(), equalTo(1));
        assertThat(doozersSchedules.get(0).getId(), equalTo(17L));
    }

    private Drop getKgbDrop() {
        return dropRepository.findByProductAndDropNames(KgbConstant.PRODUCT_NAME.value(), KgbConstant.DROP_NAME.value());
    }
}
