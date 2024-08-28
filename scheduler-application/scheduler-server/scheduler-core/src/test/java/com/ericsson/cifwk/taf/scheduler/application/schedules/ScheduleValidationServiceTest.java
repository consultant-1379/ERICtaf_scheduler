package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.application.repository.IsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.schema.SchemaService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidator;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidatorFactory;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.exceptions.NoScheduleItemsDefinedException;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleValidationResult;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.integration.tms.TmsClient;
import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */

@RunWith(MockitoJUnitRunner.class)
public class ScheduleValidationServiceTest {


    @InjectMocks
    ScheduleValidationService service;

    @Mock
    TestwareIsoRepository testwareIsoRepository;
    @Mock
    TestRegistryClient testRegistryClient;
    @Mock
    TmsClient tmsClient;
    @Mock
    ScheduleInfo scheduleInfo;
    @Mock
    DropInfo drop;
    @Mock
    TestwareIso testwareIso;
    @Mock
    Testware testware1;
    @Mock
    Testware testware2;
    @Mock
    Testware notInRegistry;
    @Mock
    Gav gav1;
    @Mock
    Gav gav2;
    @Mock
    Gav gav3;
    @Mock
    com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware registryTestware1;
    @Mock
    com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware registryTestware2;
    @Mock
    ScheduleValidatorFactory scheduleValidatorFactory;
    @Mock
    ScheduleValidator scheduleValidator;
    @Mock
    SchemaService schemaService;

    @Before
    public void setUp() throws IOException {
        when(scheduleInfo.getDrop()).thenReturn(drop);
        when(drop.getName()).thenReturn("dropName");
        when(drop.getProductName()).thenReturn("productName");

        when(testwareIso.getName()).thenReturn("ENMCXP123");
        when(testwareIso.getVersion()).thenReturn("1.13.1");
        when(testwareIso.getTestwares()).thenReturn(Sets.newHashSet(testware1, testware2));

        when(testware1.getGav()).thenReturn(gav1);
        when(testware1.getGav().getGroupId()).thenReturn("com.ericsson.cifwk.taf.scheduler");
        when(testware1.getGav().getArtifactId()).thenReturn("scheduler-testware");
        when(testware1.getGav().getVersion()).thenReturn("1.1.1");

        when(testware2.getGav()).thenReturn(gav2);
        when(testware2.getGav().getGroupId()).thenReturn("com.ericsson.cifwk.taf.scheduler");
        when(testware2.getGav().getArtifactId()).thenReturn("scheduler-testware-new");
        when(testware2.getGav().getVersion()).thenReturn("1.0");

        when(notInRegistry.getGav()).thenReturn(gav3);
        when(notInRegistry.getGav().getGroupId()).thenReturn("com.ericsson.cifwk.taf.scheduler");
        when(notInRegistry.getGav().getArtifactId()).thenReturn("scheduler-testware-not-from-registry");
        when(notInRegistry.getGav().getVersion()).thenReturn("1.0");

        when(registryTestware1.getGroupId()).thenReturn("com.ericsson.cifwk.taf.scheduler");
        when(registryTestware1.getArtifactId()).thenReturn("scheduler-testware");
        when(registryTestware1.getVersion()).thenReturn("1.1.1");
        when(registryTestware1.getSuites()).thenAnswer(a -> {
            String[] suites = new String[2];
            suites[0] = "correct-suite.xml";
            suites[1] = "correct-suite2.xml";
            return suites;
        });

        when(registryTestware2.getGroupId()).thenReturn("com.ericsson.cifwk.taf.scheduler");
        when(registryTestware2.getArtifactId()).thenReturn("scheduler-testware-new");
        when(registryTestware2.getVersion()).thenReturn("1.0");
        when(registryTestware2.getSuites()).thenAnswer(a -> {
            String[] suites = new String[2];
            suites[0] = "correct-new-suite.xml";
            suites[1] = "correct-new-suite2.xml";
            return suites;
        });
        mockTestRegistryClient();

        when(schemaService.getScheduleSchema()).thenReturn(null);
        when(scheduleValidatorFactory.create(any())).thenReturn(scheduleValidator);

        when(testwareIsoRepository.findLatestByProductAndDrop(anyString(), anyString())).thenReturn(testwareIso);
    }

    @Test
    public void validate_allValid() throws Exception {
        mockXml("schedules/schedule-suite-in-testware.xml");
        when(scheduleValidator.validate(any())).thenReturn(Lists.newArrayList());
        ScheduleValidationResult result = service.validate(scheduleInfo);

        assertThat(result, notNullValue());
        assertTrue(result.isValid());
        assertThat(result.getSchemaErrors(), empty());
        assertThat(result.getSuiteErrors(), empty());
    }

    @Test
    public void validate_hasSchemaErrors() throws Exception {
        mockXml("schedules/schedule-suite-in-testware.xml");
        when(scheduleValidator.validate(any())).thenReturn(Lists.newArrayList(
                new ScheduleErrorInfo(new ErrorRange(), "Test")));
        ScheduleValidationResult result = service.validate(scheduleInfo);

        assertThat(result, notNullValue());
        assertFalse(result.isValid());
        assertThat(result.getSchemaErrors(), not(empty()));
        assertThat(result.getSuiteErrors(), empty());
    }

    @Test
    public void validate_hasSuiteErrors() throws Exception {
        mockXml("schedules/schedule-invalid-testware.xml");
        when(scheduleValidator.validate(any())).thenReturn(Lists.newArrayList());
        ScheduleValidationResult result = service.validate(scheduleInfo);

        assertThat(result, notNullValue());
        assertFalse(result.isValid());
        assertThat(result.getSchemaErrors(), empty());
        assertThat(result.getSuiteErrors(), not(empty()));
    }

    @Test
    public void validateSuites_shouldNotReturnNullValue() throws IOException {
        mockXml("schedules/schedule-simple.xml");
        List<ScheduleErrorInfo> errors = service.validateSuites(scheduleInfo);
        assertThat(errors, Matchers.notNullValue());
    }

    @Test
    public void validateSuites_shouldReturnEmptyListWhenAllSuitesExist() throws IOException {
        mockXml("schedules/schedule-simple.xml");
        List<ScheduleErrorInfo> errors = service.validateSuites(scheduleInfo);
        assertThat(errors, Matchers.notNullValue());
    }

    @Test(expected = NoScheduleItemsDefinedException.class)
    public void validateSuites_shouldThrowExceptionWhenNoScheduleItemsFound() throws IOException {
        mockXml("schedules/schedule-no-items.xml");
        service.validateSuites(scheduleInfo);
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenComponentNotDefined() throws IOException {
        mockXml("schedules/schedule-component-not-defined.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));

        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(5, 11, 8, 12), "<component> should be defined"),
                new ScheduleErrorInfo(new ErrorRange(11, 39, 14, 16), "<component> should be defined")));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenSuiteNotDefined() throws IOException {
        mockXml("schedules/schedule-suite-not-defined.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));

        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(5, 11, 8, 12), "<suites> should be defined"),
                new ScheduleErrorInfo(new ErrorRange(11, 39, 14, 16), "<suites> should be defined")));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenComponentBodyIsEmpty() throws IOException {
        mockXml("schedules/schedule-component-has-empty-body.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));

        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(8, 20, 8, 32), "<component> body should not be empty"),
                new ScheduleErrorInfo(new ErrorRange(15, 24, 15, 36), "<component> body should not be empty")
        ));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenSuiteBodyIsEmpty() throws IOException {
        mockXml("schedules/schedule-suite-has-empty-body.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));

        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(9, 17, 9, 26), "<suites> body should not be empty"),
                new ScheduleErrorInfo(new ErrorRange(16, 21, 16, 30), "<suites> body should not be empty")
        ));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenComponentGavIsNotMapped() throws IOException {
        mockXml("schedules/schedule-component-has-unmapped-gav.xml");
        List<ScheduleErrorInfo> errors = service.validateSuites(scheduleInfo);
        assertThat(errors, hasSize(2));

        assertThat(errors, Matchers.hasItems(
                new ScheduleErrorInfo(new ErrorRange(7, 20, 7, 69), "com.ericsson.cifwk.taf.executor:wrong not found in list of associated Testware for this Iso"),
                new ScheduleErrorInfo(new ErrorRange(14, 24, 14, 89), "com.ericsson.cifwk.taf.executor:te-taf-testware:wrong not found in list of associated Testware for this Iso")
        ));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenSuiteNotExistsInTestware() throws IOException {
        mockXml("schedules/schedule-suite-not-in-testware.xml");

        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));

        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(9, 17, 9, 58), "Suites [wrong-suite.xml, wrong-suite2.xml] don't exist in Testware"),
                new ScheduleErrorInfo(new ErrorRange(16, 21, 16, 49), "Suites [wrong-new-suite.xml] don't exist in Testware")
        ));
    }

    @Test
    public void validateSuites_shouldNotReturnErrorsWhenSuiteExistsForTestwareAndComponentGavIsMapped() throws IOException {
        mockXml("schedules/schedule-suite-in-testware.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(0));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenTestwareNotExistsInRegistry() throws IOException {
        when(testwareIso.getTestwares()).thenReturn(Sets.newHashSet(notInRegistry));
        mockXml("schedules/schedule-component-not-in-registry.xml");
        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(1));
        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(8, 20, 8, 101), "Testware doesn't exist in TAF Test Registry")
        ));
    }

    @Test
    public void validateSuites_shouldReturnErrorsWhenTestwareHasNoSuitesInRegistry() throws IOException {
        mockXml("schedules/schedule-suite-in-testware.xml");
        when(registryTestware1.getSuites()).thenReturn(new String[0]); //change mock to have 0 suites

        List<ScheduleErrorInfo> scheduleErrors = service.validateSuites(scheduleInfo);
        assertThat(scheduleErrors, hasSize(1));
        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(8, 20, 8, 83), "No suites exist for Testware in TAF Test Registry")
        ));
    }

    @Test
    public void validateTestCampaigns_shouldReturnErrorsWhenTestCampaignsNotInTms() throws IOException {
        mockXml("schedules/schedule-testcampaigns-tms.xml");
        when(tmsClient.findAllTestCampaignIds()).thenReturn(new ArrayList<TestCampaign.Item>());

        List<ScheduleErrorInfo> scheduleErrors = service.validateTestCampaigns(scheduleInfo);
        assertThat(scheduleErrors, hasSize(2));
        assertThat(scheduleErrors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(7, 35, 7, 51), "TestCampaign with id: \"1\" does not exist in TMS")
        ));
    }

    @Test
    public void validateTestCampaigns_shouldReturnNoErrorsWhenTestCampaignsInTms() throws IOException {
        mockXml("schedules/schedule-testcampaigns-tms.xml");
        when(tmsClient.findAllTestCampaignIds()).thenReturn(mockTestCampaignItems());

        List<ScheduleErrorInfo> scheduleErrors = service.validateTestCampaigns(scheduleInfo);
        assertThat(scheduleErrors, hasSize(0));
    }

    private void mockTestRegistryClient() {
        when(testRegistryClient.findTestwareDetails(anyList())).thenAnswer(invocation -> {
            List<TestwareInfo> testwareInfos = (List<TestwareInfo>) invocation.getArguments()[0];
            if (testwareInfos.isEmpty()) {
                return Collections.emptyList();
            }
            TestwareInfo testwareInfo = testwareInfos.get(0);
            if (testwareInfo.getArtifactId().equals(registryTestware1.getArtifactId()) &&
                    testwareInfo.getGroupId().equals(registryTestware1.getGroupId()) &&
                    testwareInfo.getVersion().equals(registryTestware1.getVersion())) {
                return Lists.newArrayList(registryTestware1);
            }
            if (testwareInfo.getArtifactId().equals(registryTestware2.getArtifactId()) &&
                    testwareInfo.getGroupId().equals(registryTestware2.getGroupId()) &&
                    testwareInfo.getVersion().equals(registryTestware2.getVersion())) {
                return Lists.newArrayList(registryTestware2);
            }
            return Collections.emptyList();
        });
    }

    private void mockXml(String scheduleResourceName) throws IOException {
        URL url = Resources.getResource(scheduleResourceName);
        String text = Resources.toString(url, Charsets.UTF_8);
        when(scheduleInfo.getXmlContent()).thenReturn(text);
    }

    private List<TestCampaign.Item> mockTestCampaignItems() {
        TestCampaign.Item firstCampaignItem = new TestCampaign.Item();
        firstCampaignItem.setId(1);

        TestCampaign.Item secondCampaignItem = new TestCampaign.Item();
        secondCampaignItem.setId(2);

        List<TestCampaign.Item> itemList = Lists.newArrayList();
        itemList.add(firstCampaignItem);
        itemList.add(secondCampaignItem);
        return itemList;
    }

}
