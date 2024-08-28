package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static com.ericsson.cifwk.taf.scheduler.common.AssertionHelper.validateHasProperEntities;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class DropRepositoryITest {

    @Autowired
    DropRepository dropRepository;

    @Test
    public void shouldFindAll() {
        assertFalse(dropRepository.findAll().isEmpty());
    }

    @Test
    public void findByProductName() {
        List<Drop> enm = dropRepository.findByProductName("ENM");
        assertFalse(enm.isEmpty());
        assertEquals("ENM", enm.iterator().next().getProduct().getName());
    }

    @Test
    public void findByProductAndDropNames() {
        Drop drop = dropRepository.findByProductAndDropNames("ENM", "1.0.enm.early");
        assertEquals("ENM", drop.getProduct().getName());
        assertEquals("1.0.enm.early", drop.getName());
    }

    @Test
    @Transactional
    public void shouldReturnDropAndMappedIsos() {
        Drop drop = dropRepository.findOne(1L);

        validateHasProperEntities(drop.getIsos(), containsInAnyOrder(1L, 3L));
        assertThat(drop.getName(), is("1.0.enm.early"));
        assertThat(drop.getProduct().getName(), is("ENM"));

        drop = dropRepository.findOne(3L);

        validateHasProperEntities(drop.getIsos(), containsInAnyOrder(2L, 3L, 4L));
        assertThat(drop.getName(), is("1.0.oss"));
        assertThat(drop.getProduct().getName(), is("OSS"));
    }

}
