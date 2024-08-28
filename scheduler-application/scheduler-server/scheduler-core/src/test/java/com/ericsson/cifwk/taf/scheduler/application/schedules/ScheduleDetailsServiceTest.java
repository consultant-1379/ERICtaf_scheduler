package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareRepository;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.TestwareMapper;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleDetailsServiceTest {

    private static final String GROUP_ID_NOT_FOUND = "com.ericsson.cifwk.not.found";
    private static final String ARTIFACT_ID_NOT_EXIST = "not-exist-testware";

    private static final String GROUP_ID_SINGLE = "com.ericsson.cifwk.single";
    private static final String ARTIFACT_ID_SINGLE = "single-testware";

    private static final String GROUP_ID_GROUP = "com.ericsson.cifwk.group";
    private static final String ARTIFACT_ID_GROUP = "group-testware";

    private static final String GROUP_ID_SUITES = "com.ericsson.cifwk.suites";
    private static final String ARTIFACT_ID_SUITES = "suites-testware";

    private static final String VERSION_1_0_0 = "1.0.0";
    private static final String VERSION_1_0_1 = "1.0.1";
    private static final String VERSION_1_0_2 = "1.0.2";
    private static final String VERSION_2_0_1 = "2.0.1";

    private static final String[] SUITES_VERSION_1_0_0 = {"suite1.xml"};
    private static final String[] SUITES_VERSION_1_0_1 = {"suite1.xml", "suite2.xml", "suite3.xml"};
    private static final String[] SUITES_VERSION_1_0_2 = {"suite1.xml", "suite2.xml"};
    private static final String[] SUITES_VERSION_2_0_1 = {"suite1.xml", "suite2.xml", "suite3.xml", "suite4.xml"};

    @InjectMocks
    private ScheduleDetailsService scheduleDetailsService;

    @Mock
    private TestwareRepository testwareRepository;
    @Mock
    private TestwareMapper testwareMapper;
    @Mock
    private TestRegistryClient testRegistryClient;

    @Mock
    private com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware trTestware1;
    @Mock
    private com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware trTestware2;
    @Mock
    private com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware trTestware3;
    @Mock
    private Testware testware1;
    @Mock
    private Testware testware2;
    @Mock
    private Testware testware3;
    @Mock
    private Gav gav1;
    @Mock
    private Gav gav2;
    @Mock
    private Gav gav3;

    private TestwareInfo testwareInfo1 = new TestwareInfo();
    private TestwareInfo testwareInfo2 = new TestwareInfo();
    private TestwareInfo testwareInfo3 = new TestwareInfo();

    @Before
    public void setUp() {
        testwareInfo1 = new TestwareInfo();
        testwareInfo2 = new TestwareInfo();
        testwareInfo3 = new TestwareInfo();

        when(testwareMapper.map(testware1)).thenReturn(testwareInfo1);
        when(testwareMapper.map(testware2)).thenReturn(testwareInfo2);
        when(testwareMapper.map(testware3)).thenReturn(testwareInfo3);
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withoutItems() throws IOException {
        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-without-items.xml"), 1L);
        assertTrue(testwareInfoList.isEmpty());
        verify(testwareRepository, never()).findByGroupAndArtifactIdAndVersion(anyString(), anyString(), anyString());
        verify(testwareMapper, never()).map(any(Testware.class));
        verify(testRegistryClient, never()).findTestwareDetails(anyListOf(TestwareInfo.class));
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withEmptyComponent() throws IOException {
        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-with-empty-component.xml"), 1L);
        assertTrue(testwareInfoList.isEmpty());
        verify(testwareRepository, never()).findByGroupAndArtifactIdAndVersion(anyString(), anyString(), anyString());
        verify(testwareMapper, never()).map(any(Testware.class));
        verify(testRegistryClient, times(1)).findTestwareDetails(anyListOf(TestwareInfo.class));
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withNotFoundItem() throws IOException {
        when(testwareRepository.findByGroupAndArtifactIdAndVersion(GROUP_ID_NOT_FOUND, ARTIFACT_ID_NOT_EXIST, ""))
                .thenReturn(null);

        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-with-not-found-item.xml"), 1L);
        assertTrue(testwareInfoList.isEmpty());
        verify(testRegistryClient, times(1)).findTestwareDetails(anyListOf(TestwareInfo.class));
        verify(testwareMapper, never()).map(any(Testware.class));

        PageRequest pageable = new PageRequest(0, 1);
        verify(testwareRepository, times(1)).findLatestByGroupAndArtifactId(anyString(), anyString(), eq(1L), eq(pageable));
        verify(testwareRepository, never()).findByGroupAndArtifactIdAndVersion(anyString(), anyString(), anyString());
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withGroupItems() throws IOException {
        String groupId = GROUP_ID_GROUP;
        String artifactId = ARTIFACT_ID_GROUP;

        setupTrTestwareMock(trTestware1, groupId, artifactId, VERSION_1_0_1, SUITES_VERSION_1_0_1);
        setupTestwareMock(testware1, gav1, groupId, artifactId, VERSION_1_0_1);
        setupTestwareInfo(testwareInfo1, groupId, artifactId, VERSION_1_0_1);

        PageRequest pageable = new PageRequest(0, 1);
        when(testwareRepository.findLatestByGroupAndArtifactId(groupId, artifactId, 1L, pageable))
                .thenReturn(Lists.newArrayList(testware1));
        when(testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_1_0_1))
                .thenReturn(testware1);

        List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestwareList =
                Lists.newArrayList(trTestware1);
        when(testRegistryClient.findTestwareDetails(anyListOf(TestwareInfo.class)))
                .thenReturn(trTestwareList);

        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-with-group-items.xml"), 1L);
        assertFalse(testwareInfoList.isEmpty());
        assertEquals(1, testwareInfoList.size());
        verify(testwareRepository, times(2)).findLatestByGroupAndArtifactId(groupId, artifactId, 1L, pageable);
        verify(testwareRepository, times(1)).findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_1_0_1);
        verifyTestwareInfo(testwareInfoList.get(0), groupId, artifactId, VERSION_1_0_1,
                Sets.newHashSet("suite1.xml", "suite2.xml", "suite3.xml"), Sets.newHashSet(SUITES_VERSION_1_0_1));
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withDifferentVersions() throws IOException {
        String groupId = GROUP_ID_SINGLE;
        String artifactId = ARTIFACT_ID_SINGLE;

        setupTrTestwareMock(trTestware1, groupId, artifactId, VERSION_1_0_0, SUITES_VERSION_1_0_0);
        setupTrTestwareMock(trTestware2, groupId, artifactId, VERSION_1_0_1, SUITES_VERSION_1_0_1);
        setupTrTestwareMock(trTestware3, groupId, artifactId, VERSION_1_0_2, SUITES_VERSION_1_0_2);

        setupTestwareMock(testware1, gav1, groupId, artifactId, VERSION_1_0_0);
        setupTestwareMock(testware2, gav1, groupId, artifactId, VERSION_1_0_1);
        setupTestwareMock(testware3, gav1, groupId, artifactId, VERSION_1_0_2);

        setupTestwareInfo(testwareInfo1, groupId, artifactId, VERSION_1_0_0);
        setupTestwareInfo(testwareInfo2, groupId, artifactId, VERSION_1_0_1);
        setupTestwareInfo(testwareInfo3, groupId, artifactId, VERSION_1_0_2);

        when(testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_1_0_0))
                .thenReturn(testware1);
        when(testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_1_0_1))
                .thenReturn(testware2);
        when(testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_1_0_2))
                .thenReturn(testware3);

        List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestwareList =
                Lists.newArrayList(trTestware1, trTestware2, trTestware3);
        when(testRegistryClient.findTestwareDetails(anyListOf(TestwareInfo.class)))
                .thenReturn(trTestwareList);

        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-with-different-versions.xml"), 1L);
        assertFalse(testwareInfoList.isEmpty());
        assertEquals(3, testwareInfoList.size());
        verifyTestwareInfo(testwareInfoList.get(0), groupId, artifactId, VERSION_1_0_0,
                Sets.newHashSet(), Sets.newHashSet(SUITES_VERSION_1_0_0));
        verifyTestwareInfo(testwareInfoList.get(1), groupId, artifactId, VERSION_1_0_1,
                Sets.newHashSet("suite1.xml"), Sets.newHashSet(SUITES_VERSION_1_0_1));
        verifyTestwareInfo(testwareInfoList.get(2), groupId, artifactId, VERSION_1_0_2,
                Sets.newHashSet("suite1.xml", "suite2.xml"), Sets.newHashSet(SUITES_VERSION_1_0_2));
    }

    @Test
    public void getExistingTestwareListFromScheduleXml_withSameSuites() throws IOException {
        String groupId = GROUP_ID_SUITES;
        String artifactId = ARTIFACT_ID_SUITES;

        setupTrTestwareMock(trTestware1, groupId, artifactId, VERSION_2_0_1, SUITES_VERSION_2_0_1);
        setupTestwareMock(testware1, gav1, groupId, artifactId, VERSION_2_0_1);
        setupTestwareInfo(testwareInfo1, groupId, artifactId, VERSION_2_0_1);

        when(testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, VERSION_2_0_1))
                .thenReturn(testware1);

        List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestwareList =
                Lists.newArrayList(trTestware1);
        when(testRegistryClient.findTestwareDetails(anyListOf(TestwareInfo.class)))
                .thenReturn(trTestwareList);

        List<TestwareInfo> testwareInfoList = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(getXmlContent("schedule-with-same-suites.xml"), 1L);
        assertFalse(testwareInfoList.isEmpty());
        assertEquals(1, testwareInfoList.size());
        verifyTestwareInfo(testwareInfoList.get(0), groupId, artifactId, VERSION_2_0_1,
                Sets.newHashSet("suite1.xml", "suite2.xml", "suite3.xml", "suite4.xml"),
                Sets.newHashSet(SUITES_VERSION_2_0_1));
    }

    private void verifyTestwareInfo(
            TestwareInfo testwareInfo,
            String groupId, String artifactId, String version,
            Set<String> selectedSuites, Set<String> existingSuites) {

        assertEquals(groupId, testwareInfo.getGroupId());
        assertEquals(artifactId, testwareInfo.getArtifactId());
        assertEquals(version, testwareInfo.getVersion());
        assertEquals(selectedSuites.size(), testwareInfo.getSelectedSuites().size());
        assertEquals(existingSuites.size(), testwareInfo.getExistingSuites().size());
        assertThat(testwareInfo.getSelectedSuites(), containsInAnyOrder(selectedSuites.toArray()));
        assertThat(testwareInfo.getExistingSuites(), containsInAnyOrder(existingSuites.toArray()));
    }

    private void setupTestwareInfo(TestwareInfo testwareInfo, String groupId, String artifactId, String version) {
        testwareInfo.setGroupId(groupId);
        testwareInfo.setArtifactId(artifactId);
        testwareInfo.setVersion(version);
    }

    private void setupTestwareMock(Testware mockTestware, Gav gav, String groupId, String artifactId, String version) {
        when(mockTestware.getGav()).thenReturn(gav);
        when(mockTestware.getGav().getGroupId()).thenReturn(groupId);
        when(mockTestware.getGav().getArtifactId()).thenReturn(artifactId);
        when(mockTestware.getGav().getVersion()).thenReturn(version);
    }

    private void setupTrTestwareMock(
            com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware mockTrTestware,
            String groupId, String artifactId, String version, String[] suitesNames) {
        when(mockTrTestware.getGroupId()).thenReturn(groupId);
        when(mockTrTestware.getArtifactId()).thenReturn(artifactId);
        when(mockTrTestware.getVersion()).thenReturn(version);
        when(mockTrTestware.getSuites()).thenReturn(suitesNames);
    }

    private String getXmlContent(String scheduleFileName) throws IOException {
        String scheduleResourceName = "schedules/parsing/" + scheduleFileName;
        URL url = Resources.getResource(scheduleResourceName);
        return Resources.toString(url, Charsets.UTF_8);
    }
}
