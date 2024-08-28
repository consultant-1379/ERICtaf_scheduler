package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class TestwareServiceITest {

    private static final String PRODUCT = "ENM";

    private static final String DROP = "15.14";

    @Autowired
    TestwareService testwareService;

    @Test
    public void shouldReturnLatestTestwareIsoForDrop() {
        List<TestwareInfo> latestTestware = testwareService.getLatestTestwareForDrop(PRODUCT, DROP, null);
        assertThat(latestTestware.size(), equalTo(97));
    }

    @Test
    public void shouldReturnLatestTestwareIsoWithScheduleParsedData() {
        List<TestwareInfo> latestTestware = testwareService.getLatestTestwareForDrop(PRODUCT, DROP, 11L);
        assertThat(latestTestware.size(), equalTo(97));

        String groupId = "com.ericsson.oss.services.security.identitymgmt";
        String artifactId = "ERICTAFgenericidentitymgmtservice_CXP9031924";

        List<TestwareInfo> filteredTestwareList = latestTestware.stream()
                .filter(t -> groupId.equals(t.getGroupId()) && artifactId.equals(t.getArtifactId()))
                .collect(Collectors.toList());

        String[] selectedItems = {"RoleManagement.xml", "TargetGroupManagement.xml"};
        String[] existingItems = {
                "RoleManagement.xml", "TargetGroupManagement.xml",
                "TargetGroupPerformance.xml", "TargetGroupPerformanceFull.xml"
        };

        assertThat(filteredTestwareList, hasSize(1));
        TestwareInfo testwareInfo = filteredTestwareList.get(0);
        assertThat(testwareInfo.isIncluded(), is(true));
        assertThat(testwareInfo.isDistinguishedSuites(), is(true));
        assertThat(testwareInfo.getSelectedSuites(), hasSize(2));
        assertThat(testwareInfo.getSelectedSuites(), hasItems(selectedItems));
        assertThat(testwareInfo.getExistingSuites(), hasSize(4));
        assertThat(testwareInfo.getExistingSuites(), hasItems(existingItems));
    }

    @Test
    public void shouldGetLatesTestware() {
        List<TestwareInfo> latestTestware = testwareService.getLatestTestware(null);
        assertFalse(latestTestware.isEmpty());
    }

}
