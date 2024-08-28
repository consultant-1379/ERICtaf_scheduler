package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.repository.IsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleExecutionRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleExecutionService;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.ScheduleExecution;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class ScheduleExecutionServiceITest {

    @Autowired
    private ScheduleExecutionService executionService;

    @Autowired
    private ScheduleExecutionRepository executionRepository;

    @Autowired
    private IsoRepository isoRepository;

    @Autowired
    private TestwareIsoRepository testwareIsoRepository;

    @Test
    public void shouldCreateScheduleExecution() {
        List<ScheduleExecution> executions = executionRepository.findAll();
        assertThat(executions.size(), equalTo(2));

        List<ISO> isos = isoRepository.findAll();
        assertThat(isos.size(), equalTo(6));

        List<TestwareIso> testwareIsos = testwareIsoRepository.findAll();
        assertThat(testwareIsos.size(), equalTo(3));

        final Long scheduleId = 14L;
        final String productIsoVersion = "1.12.68";
        final String testwareIsoVersion = "1.12.35";

        ScheduleExecution newExecution = executionService.createExecution(scheduleId, productIsoVersion, testwareIsoVersion).get();
        assertThat(newExecution.getId(), equalTo(3L));

        // assert that new iso was created
        isos = isoRepository.findAll();
        assertThat(isos.size(), equalTo(7));

        // assert that new testware iso was created
        testwareIsos = testwareIsoRepository.findAll();
        assertThat(testwareIsos.size(), equalTo(4));

        assertThat(newExecution.getIso().getId(), equalTo(7L));

        assertThat(newExecution.getTestwareIso().getId(), equalTo(4L));
    }
}
