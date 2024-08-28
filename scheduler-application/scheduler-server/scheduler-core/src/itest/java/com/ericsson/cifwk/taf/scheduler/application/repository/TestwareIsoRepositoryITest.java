package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ericsson.cifwk.taf.scheduler.common.AssertionHelper.validateHasProperEntities;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class TestwareIsoRepositoryITest {

    @Autowired
    TestwareIsoRepository testwareIsoRepository;

    @Autowired
    IsoRepository isoRepository;

    @Autowired
    DropRepository dropRepository;

    @Test
    public void shouldFindByNameAndVersion() {
        TestwareIso testwareIso = testwareIsoRepository.findByNameAndVersion("CXP9027746", "0.0.1");
        assertThat(testwareIso.getName(), equalTo("CXP9027746"));
        assertThat(testwareIso.getVersion(), equalTo("0.0.1"));
        assertThat(testwareIso.getTestwares().size(), equalTo(3));
        validateHasProperEntities(testwareIso.getTestwares(), containsInAnyOrder(1L, 2L, 3L));
    }

    @Test
    public void shouldFindTestwareIsosByProductIso() {
        ISO iso = isoRepository.findOne(1L);
        List<TestwareIso> testwareIsos = testwareIsoRepository.findByProductIso(iso);
        assertThat(testwareIsos.size(), equalTo(2));

        assertThat(testwareIsos.get(0).getName(), equalTo("CXP9027746"));
        assertThat(testwareIsos.get(0).getVersion(), equalTo("0.0.1"));
        assertThat(testwareIsos.get(0).getTestwares().size(), equalTo(3));
        validateHasProperEntities(testwareIsos.get(0).getTestwares(), containsInAnyOrder(1L, 2L, 3L));

        assertThat(testwareIsos.get(1).getVersion(), equalTo("0.0.2"));
        assertThat(testwareIsos.get(1).getTestwares().size(), equalTo(2));
        validateHasProperEntities(testwareIsos.get(1).getTestwares(), containsInAnyOrder(1L, 2L));
    }

    @Test
    public void shouldFindLatestByProductAndDrop() {
        final String product = "ENM";
        final String drop = "1.0.enm.early";
        TestwareIso lastestTestwareIso = testwareIsoRepository.findLatestByProductAndDrop(product, drop);
        assertThat(lastestTestwareIso.getName(), equalTo("CXP9027746"));
        assertThat(lastestTestwareIso.getVersion(), equalTo("0.0.2"));
    }

    @Transactional
    @Test
    public void shouldCreateNewTestwareIso() {
        ISO iso = isoRepository.findOne(1L);
        TestwareIso newTestwareIso = new TestwareIso("CXP9027746", "0.0.3", iso);
        TestwareIso savedTestwareIso = testwareIsoRepository.save(newTestwareIso);

        assertThat(savedTestwareIso.getName(), equalTo("CXP9027746"));
        assertThat(savedTestwareIso.getVersion(), equalTo("0.0.3"));
        assertThat(savedTestwareIso.getIso(), equalTo(iso));

        List<TestwareIso> testwareIsos = testwareIsoRepository.findByProductIso(iso);
        assertThat(testwareIsos.size(), equalTo(3));
        validateHasProperEntities(testwareIsos, containsInAnyOrder(1L, 2L, 4L));
    }
}
