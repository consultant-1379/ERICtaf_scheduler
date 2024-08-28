package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */
@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class TestwareRepositoryITest {

    @Autowired
    TestwareRepository testwareRepository;

    @Test
    @Transactional
    public void shouldFindTestwareByIsoVersionAndName() {
        List<Testware> testwares = testwareRepository.findByIsoVersionAndName("CXP9027746", "0.0.1");
        assertThat(testwares, hasSize(3));
    }

    @Test
    public void shouldFindPackageByArtifactIdAndVersion() {
        Testware t = testwareRepository.findByArtifactIdAndVersion("ERICTAFeniq_cdb_setup_CXP9027959", "1.0.20");

        assertEquals(t.getGav().getGroupId(), "com.ericsson.ci.cloud.testware");
        assertEquals(t.getGav().getArtifactId(), "ERICTAFeniq_cdb_setup_CXP9027959");
        assertEquals(t.getGav().getVersion(), "1.0.20");
        assertEquals(t.getCxpNumber(), "CXPValid1");
    }

    @Test
    public void shouldCreateNewTestware(){
        Testware newTestware = new Testware();
        newTestware.setCxpNumber("CXPjkl");
        Gav gav = createGav("com.ericsson.taf", "ERICtaftestware_0000", "0.12.15");
        newTestware.setGav(gav);

        testwareRepository.save(newTestware);
        Testware savedTestware = testwareRepository.findByArtifactIdAndVersion("ERICtaftestware_0000", "0.12.15");

        assertThat(savedTestware.getId(), is(4L));
        assertThat(savedTestware.getCxpNumber(), is("CXPjkl"));
        assertThat(savedTestware.getGav().getArtifactId(), is("ERICtaftestware_0000"));
        assertThat(savedTestware.getGav().getGroupId(), is("com.ericsson.taf"));
        assertThat(savedTestware.getGav().getVersion(), is("0.12.15"));
        List<Testware> allTestware = testwareRepository.findAll();
        assertThat(allTestware.size(), is(4));
    }

    private Gav createGav(String groupId, String artifactId, String version) {
        Gav gav = new Gav();
        gav.setGroupId(groupId);
        gav.setArtifactId(artifactId);
        gav.setVersion(version);
        return gav;
    }
}
