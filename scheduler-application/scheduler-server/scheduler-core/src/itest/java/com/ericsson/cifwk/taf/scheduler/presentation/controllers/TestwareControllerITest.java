package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.application.security.SecurityMock;
import com.ericsson.cifwk.taf.scheduler.db.FlywayTest;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
@FlywayTest(locations = {"db/migration", "db/dev"}, invokeClean = true)
public class TestwareControllerITest {

    private static final String PRODUCT_NAME = "ENM";
    private static final String DROP_VERSION = "15.14";

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    ObjectMapper om;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SecurityMock.mockPrincipal("eKiajen");
    }

    @Test
    public void getLatestTestwareForDrop() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/testware/latest")
                .param("product", PRODUCT_NAME)
                .param("drop", DROP_VERSION))
                .andExpect(status().isOk())
                .andReturn();

        List<TestwareInfo> testwareList = readTestware(mvcResult);

        assertThat(testwareList.size(), equalTo(97));
    }

    @Test
    public void getLatestTestwareForDropWithScheduleId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/testware/latest")
                .param("product", PRODUCT_NAME)
                .param("drop", DROP_VERSION)
                .param("scheduleId", "11"))
                .andExpect(status().isOk())
                .andReturn();

        List<TestwareInfo> testwareList = readTestware(mvcResult);

        String groupId = "com.ericsson.oss.services.security.identitymgmt";
        String artifactId = "ERICTAFgenericidentitymgmtservice_CXP9031924";

        assertThat(testwareList, hasSize(97));
        List<TestwareInfo> filteredTestwareList = testwareList.stream()
                .filter(t -> groupId.equals(t.getGroupId()) && artifactId.equals(t.getArtifactId()))
                .collect(Collectors.toList());

        String[] selectedItems = {"RoleManagement.xml", "TargetGroupManagement.xml"};
        String[] existingItems = {
                "RoleManagement.xml", "TargetGroupManagement.xml",
                "TargetGroupPerformance.xml", "TargetGroupPerformanceFull.xml"
        };

        assertThat(filteredTestwareList, hasSize(1));
        TestwareInfo testwareInfo = filteredTestwareList.get(0);
        verifyTestwareInfo(testwareInfo, selectedItems, existingItems, true);
    }

    @Test
    public void getTestwareListByScheduleXml() throws Exception {
        String xmlContent = getXmlContent("schedule-with-items.xml");

        // perform request to get latest testware list
        mockMvc.perform(get("/api/testware/latest")
                .param("product", PRODUCT_NAME)
                .param("drop", DROP_VERSION))
                .andExpect(status().isOk())
                .andReturn();

        // perform request for testing
        MvcResult mvcResult = mockMvc.perform(post("/api/testware/analyzeXml")
                .param("product", PRODUCT_NAME)
                .param("drop", DROP_VERSION)
                .content(xmlContent))
                .andExpect(status().isOk())
                .andReturn();

        List<TestwareInfo> testwareList = readTestware(mvcResult);
        assertThat(testwareList, hasSize(3));

        String[] selectedItems0 = {
                "SmrsServiceTest.xml", "SmrsServiceTest1.xml", "SoftwareManagementRepositoryService.xml"
        };
        String[] existingItems0 = {
                "SmrsServiceTest.xml", "SmrsServiceTest1.xml", "SoftwareManagementRepositoryService.xml"
        };
        verifyTestwareInfo(testwareList.get(0), selectedItems0, existingItems0, false);

        String[] selectedItems1 = {"CredentialManagerCLI.xml"};
        String[] existingItems1 = {"CredentialManagerCLI.xml"};
        verifyTestwareInfo(testwareList.get(1), selectedItems1, existingItems1, false);

        String[] selectedItems2 = {"CliScript.xml", "NodeSecurity.xml"};
        String[] existingItems2 = {
                "CliScript.xml", "NodeSecurity.xml", "NodeSecurity_CertIssue.xml", "NodeSecurity_Cred.xml",
                "NodeSecurity_Cred_cba.xml", "NodeSecurity_Iscf.xml", "NodeSecurity_Iscf_Negative.xml",
                "NodeSecurity_Iscf_Positive.xml", "NodeSecurity_Keygen.xml", "NodeSecurity_Perf.xml",
                "NodeSecurity_TrustDistr.xml"
        };
        verifyTestwareInfo(testwareList.get(2), selectedItems2, existingItems2, true);
    }

    private void verifyTestwareInfo(
            TestwareInfo testwareInfo,
            String[] expectedSelectedItems,
            String[] expectedExistingItems,
            boolean isDistinguishedSuites) {

        assertThat(testwareInfo.isIncluded(), is(true));
        assertThat(testwareInfo.isDistinguishedSuites(), is(isDistinguishedSuites));
        assertThat(testwareInfo.getSelectedSuites(), hasSize(expectedSelectedItems.length));
        assertThat(testwareInfo.getSelectedSuites(), hasItems(expectedSelectedItems));
        assertThat(testwareInfo.getExistingSuites(), hasSize(expectedExistingItems.length));
        assertThat(testwareInfo.getExistingSuites(), hasItems(expectedExistingItems));
    }

    private List<TestwareInfo> readTestware(MvcResult mvcResult) throws java.io.IOException {
        return om.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<TestwareInfo>>() {
        });
    }

    private String getXmlContent(String scheduleFileName) throws IOException {
        URL url = Resources.getResource(scheduleFileName);
        return Resources.toString(url, Charsets.UTF_8);
    }

}
