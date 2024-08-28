package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiTestware;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@RunWith(MockitoJUnitRunner.class)
public class TestwareUtilsTest {

    List<CiArtifact> artifacts;

    List<CiTestware> ciTestware;

    TestwareUtils testwareUtils;

    @Before
    public void setUp() {
        testwareUtils = new TestwareUtils();
        artifacts = Lists.newArrayList(createCiArtifact());
        ciTestware = Lists.newArrayList(createCiTestware());
    }

    @Test
    public void shouldMapCiArtifactsToDtos() {
        Set<TestwareInfo> testware = testwareUtils.mapCiArtifactsToDtos(artifacts);
        List<TestwareInfo> testwareList = Lists.newArrayList(testware);
        assertThat(testwareList, hasSize(1));
        assertEquals(testwareList.get(0).getGroupId(), artifacts.get(0).getGroup());
        assertEquals(testwareList.get(0).getArtifactId(), artifacts.get(0).getName());
        assertEquals(testwareList.get(0).getVersion(), artifacts.get(0).getVersion());
        assertEquals(testwareList.get(0).getCxpNumber(), artifacts.get(0).getNumber());
    }

    @Test
    public void shouldMapCiTestwareToDtos() {
        Set<TestwareInfo> testware = testwareUtils.mapCiTestwareToDtos(ciTestware);
        List<TestwareInfo> testwareList = Lists.newArrayList(testware);
        assertThat(testwareList, hasSize(1));
        assertEquals(testwareList.get(0).getGroupId(), ciTestware.get(0).getGroupId());
        assertEquals(testwareList.get(0).getArtifactId(), ciTestware.get(0).getArtifactId());
        assertEquals(testwareList.get(0).getVersion(), ciTestware.get(0).getVersion());
    }

    private CiArtifact createCiArtifact() {
        CiArtifact ciArtifact = new CiArtifact();
        ciArtifact.setGroup("testGroup");
        ciArtifact.setName("testName");
        ciArtifact.setVersion("1.0.1");
        ciArtifact.setNumber("TestCXPNumber");
        return ciArtifact;
    }

    private CiTestware createCiTestware() {
        CiTestware ciTestware = new CiTestware();
        ciTestware.setArtifactId("artifactId");
        ciTestware.setGroupId("groupId");
        ciTestware.setVersion("1.1");
        return ciTestware;
    }
}
