package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleService;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class ScheduleServiceITest {

    private static final long SCHEDULE_ID = 1L;

    @Autowired
    private ScheduleService scheduleService;

    @Test
    @Transactional
    public void shouldDeleteAllVersions() {
        List<ScheduleInfo> schedules = scheduleService.getScheduleVersions(SCHEDULE_ID);
        assertThat(schedules.size(), equalTo(3));
        scheduleService.deleteAllVersions(SCHEDULE_ID);
        schedules = scheduleService.getScheduleVersions(SCHEDULE_ID);
        assertThat(schedules.size(), equalTo(0));
    }
}

