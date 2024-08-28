package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.collect.Lists;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.contains;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Created by eniakel on 04/03/2016.
 */
@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class SchedulePromotionServiceITest {
    @Autowired
    DropRepository dropRepository;

    @Autowired
    SchedulePromotionService schedulePromotionService;

    @Autowired
    ScheduleRepository scheduleRepository;


    @Test
    @Transactional
    public void shouldPromoteSchedulesWhenNewDropPresent() {
        List<Drop> currentDrops = dropRepository.findAll();
        Product product = currentDrops.get(0).getProduct();
        DefaultArtifactVersion latestDropVersion = getLatestDrop(currentDrops);
        Drop latestDrop = dropRepository.findByProductAndDropNames(product.getName(), latestDropVersion.toString());
        List<Schedule> schedulesInLatestDrop = scheduleRepository.findLatestVersionsByDrop(latestDrop);

        int nextMinorVersion = 1 + latestDropVersion.getMinorVersion();
        String newDropName = latestDropVersion.getMajorVersion() + "." + nextMinorVersion;
        Drop newDrop = new Drop(product, newDropName);
        newDrop = dropRepository.save(newDrop);

        schedulePromotionService.promoteSchedulesByDrop(product.getName(), Lists.newArrayList(newDrop));

        List<Schedule> promotedSchedules = scheduleRepository.findLatestVersionsByDrop(newDrop);
        assertEquals(schedulesInLatestDrop.size(), promotedSchedules.size());
        assertEquals(schedulesInLatestDrop.get(0).getName(), promotedSchedules.get(0).getName());
        assertThat(promotedSchedules.get(0).getVersion(), is(1));
        assertEquals(schedulesInLatestDrop.get(1).getName(), promotedSchedules.get(1).getName());
        assertThat(promotedSchedules.get(1).getVersion(), is(1));
        assertEquals(schedulesInLatestDrop.get(2).getName(), promotedSchedules.get(2).getName());
        assertThat(promotedSchedules.get(2).getVersion(), is(1));

        assertThat(promotedSchedules.get(2).getComments().size(), is(2));
        assertEquals(promotedSchedules.get(2).getComments().get(0).getMessage(), "TestComment1");
        assertEquals(promotedSchedules.get(2).getComments().get(1).getMessage(), "TestComment2");

        assertThat(promotedSchedules.get(2).getReviewers().size(), is(2));
    }

    private DefaultArtifactVersion getLatestDrop(List<Drop> drops) {
        List<DefaultArtifactVersion> versions = drops.stream()
                .map(d -> new DefaultArtifactVersion(d.getName()))
                .collect(toList());
        return versions.stream()
                .sorted((v1, v2) -> v2.compareTo(v1))
                .collect(toList())
                .get(0);
    }
}
