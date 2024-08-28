package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.ScheduleExecution;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class ScheduleExecutionRepositoryITest {

    @Autowired
    private ScheduleExecutionRepository executionRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private IsoRepository isoRepository;

    @Autowired
    private TestwareIsoRepository testwareIsoRepository;

    @Test
    public void shouldRetrieveExecution_ByScheduleIsoAndTestwareIso() {
        Schedule schedule = scheduleRepository.findOne(1L);
        ISO iso = isoRepository.findOne(1L);
        TestwareIso testwareIso = testwareIsoRepository.findOne(1L);

        ScheduleExecution execution = executionRepository.findByScheduleIsoAndTestwareIso(schedule, iso, testwareIso);
        assertThat(execution.getId(), equalTo(1L));

        TestwareIso secondTestwareIso = testwareIsoRepository.findOne(2L);
        ScheduleExecution secondExecution = executionRepository.findByScheduleIsoAndTestwareIso(schedule, iso, secondTestwareIso);
        assertThat(secondExecution.getId(), equalTo(2L));
    }
}
