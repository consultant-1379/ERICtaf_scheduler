package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class IsoRepositoryITest {

    @Autowired
    IsoRepository isoRepository;

    @Autowired
    DropRepository dropRepository;

    @Test
    public void testFindByNameAndVersion() {
        ISO iso = isoRepository.findByNameAndVersion("CXP7812ISO", "2.0.2");
        assertThat(iso.getName(), is("CXP7812ISO"));
        assertThat(iso.getVersion(), is("2.0.2"));
        assertThat(iso.getId(), is(2L));
    }

    @Test
     public void shouldReturnAllIsosFromDataMigration() {
        List<ISO> isos = isoRepository.findAll();
        assertThat(isos.size(), is(6));
    }

    @Test
    public void shouldReturnISObyProductDropAndName() {
        ISO iso = isoRepository.findByProductDropAndName("ENM", "1.0.enm.early", "CXP1234ISO");
        assertThat(iso, notNullValue());
        assertThat(iso.getId(), is(1L));
    }

    @Test
    @Transactional
    public void shouldCreateNewIso() {
        Drop drop = dropRepository.findOne(1L);

        ISO iso = new ISO();
        iso.setName("new");
        iso.setVersion("1.0");
        iso.addDrop(drop);

        ISO save = isoRepository.save(iso);
        assertThat(save.getId(), is(7L));

        drop = dropRepository.findOne(1L);
        validateHasProperEntities(drop.getIsos(), containsInAnyOrder(1L, 3L, 7L));
    }

    @Test
    @Transactional
    public void shouldReturnIsoWithCorrectDrops() {
        ISO iso = isoRepository.findOne(1L);
        validateHasProperEntities(iso.getDrops(), containsInAnyOrder(1L, 2L));


        iso = isoRepository.findOne(3L);
        validateHasProperEntities(iso.getDrops(), containsInAnyOrder(1L, 2L, 3L));
    }

    @Test
    @Transactional
    public void shouldReturnCorrectIsosByDrop() {
        Drop drop = dropRepository.findOne(1L);
        List<ISO> isosInDrop = isoRepository.findByDrop(drop);

        validateHasProperEntities(isosInDrop, containsInAnyOrder(1L, 3L));
    }


}
