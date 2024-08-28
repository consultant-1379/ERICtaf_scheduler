package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.api.dto.ValidationResults;
import com.ericsson.cifwk.taf.scheduler.application.constant.KgbConstant;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.schema.SchemaService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidator;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidatorFactory;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ValidationScenario;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.exceptions.NoScheduleItemsDefinedException;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTrackingXmlParser;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.ParserException;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.IncludedScheduleValidationRule;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.RequiredItemValidationRule;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.ScheduleComponentValidationRule;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.ScheduleSuitesValidationRule;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleValidationResult;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.TestCampaignIdValidationRule;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.integration.tms.TmsClient;
import com.ericsson.cifwk.taf.scheduler.integration.tms.model.TestCampaign;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */

@Service
public class ScheduleValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleValidationService.class);
    public static final String COMPONENT_TAG = "component";
    public static final String SUITES_TAG = "suites";
    public static final String ITEM_TAG = "item";
    public static final String INCLUDE_TAG = "include";
    public static final String TEST_CAMPAIGN_TAG = "test-campaign";

    @Autowired
    TestwareIsoRepository testwareIsoRepository;
    @Autowired
    TestRegistryClient testRegistryClient;
    @Autowired
    SchemaService schemaService;
    @Autowired
    ScheduleValidatorFactory scheduleValidatorFactory;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    TmsClient tmsClient;

    public ScheduleValidationResult validate(ScheduleInfo schedule) {
        ScheduleValidationResult result = new ScheduleValidationResult();
        result.setSchedule(schedule);
        result.setSchemaErrors(validateAgainstSchema(schedule.getXmlContent()));
        boolean validWithSchema = result.getSchemaErrors().isEmpty();
        if (validWithSchema) {
            result.setSuiteErrors(validateSuites(schedule));
            result.setIncludeErrors(validateIncludedSchedules(schedule));
            result.setTestCampaignErrors(validateTestCampaigns(schedule));
        }
        return result;
    }

    public ValidationResults validate(List<ScheduleInfo> schedules) {
        ValidationResults results = new ValidationResults();
        schedules.forEach(s -> results.addResult(validate(s)));
        return results;
    }

    protected List<ScheduleErrorInfo> validateAgainstSchema(String scheduleXml) {
        ScheduleValidator validator = scheduleValidatorFactory.create(schemaService.getScheduleSchema());
        return validator.validate(scheduleXml);
    }

    protected List<ScheduleErrorInfo> validateSuites(ScheduleInfo schedule) {
        List<ScheduleErrorInfo> errors = Lists.newArrayList();
        List<Testware> testware = resolveTestware(schedule);

        NodeList scheduleItems = getNodesByTagName(schedule.getXmlContent(), ITEM_TAG)
                .orElseThrow(NoScheduleItemsDefinedException::new);

        for (int i = 0; i < scheduleItems.getLength(); i++) {
            Node itemNode = scheduleItems.item(i);
            errors.addAll(validateItem(testware, itemNode));
        }
        return errors;
    }

    protected List<ScheduleErrorInfo> validateTestCampaigns(ScheduleInfo schedule) {
        List<ScheduleErrorInfo> errors = Lists.newArrayList();
        Optional<NodeList> maybeTestCampaigns = getNodesByTagName(schedule.getXmlContent(), TEST_CAMPAIGN_TAG);

        if (maybeTestCampaigns.isPresent()) {
            List<TestCampaign.Item> allTestCampaigns = tmsClient.findAllTestCampaignIds();
            NodeList testCampaigns = maybeTestCampaigns.get();
            for (int i = 0; i < testCampaigns.getLength(); i++) {
                Node testCampaignNode = testCampaigns.item(i);
                int testCampaignId = Integer.parseInt(testCampaignNode.getAttributes().getNamedItem("id").getNodeValue());
                errors.addAll(validateTestCampaignIds(testCampaignId, allTestCampaigns, testCampaignNode));
            }
        }
        return errors;
    }

    private List<Testware> resolveTestware(ScheduleInfo schedule) {
        DropInfo drop = schedule.getDrop();
        Set<Testware> testware;
        if (drop == null) {
            TestwareIso kgbTestwareIso =
                    testwareIsoRepository.findByNameAndVersion(KgbConstant.TW_ISO_NAME.value(), KgbConstant.TW_ISO_VERSION.value());
            testware = kgbTestwareIso.getTestwares();
        } else {
            TestwareIso latestIso = testwareIsoRepository.findLatestByProductAndDrop(drop.getProductName(), drop.getName());
            testware = latestIso.getTestwares();
        }
        return Lists.newArrayList(testware);
    }

    private List<ScheduleErrorInfo> validateItem(List<Testware> testwares, Node itemNode) {
        Optional<Node> componentNode = findChildByTag(COMPONENT_TAG, itemNode);
        Optional<Node> suitesNode = findChildByTag(SUITES_TAG, itemNode);

        return new ValidationScenario()
                .addRule(new RequiredItemValidationRule(COMPONENT_TAG, itemNode), componentNode)
                .addRule(new RequiredItemValidationRule(SUITES_TAG, itemNode), suitesNode)
                .addRule(new ScheduleComponentValidationRule(testwares), componentNode)
                .addRule(new ScheduleSuitesValidationRule(testwares, testRegistryClient, componentNode), suitesNode)
                .run();
    }

    private static List<ScheduleErrorInfo> validateTestCampaignIds(int testCampaignId, List<TestCampaign.Item> allTestCampaigns, Node testCampaignNode) {
        return new ValidationScenario()
                .addRule(new TestCampaignIdValidationRule(testCampaignId, allTestCampaigns), Optional.of(testCampaignNode))
                .run();
    }

    private static Optional<Node> findChildByTag(String component, Node item) {
        NodeList itemChildNodes = item.getChildNodes();
        for (int i = 0; i < itemChildNodes.getLength(); i++) {
            Node itemChild = itemChildNodes.item(i);
            if (component.equals(itemChild.getNodeName())) {
                return Optional.of(itemChild);
            }
        }
        return Optional.empty();
    }

    protected List<ScheduleErrorInfo> validateIncludedSchedules(ScheduleInfo schedule) {
        List<ScheduleErrorInfo> errors = Lists.newArrayList();
        Optional<NodeList> maybeIncludedSchedules = getNodesByTagName(schedule.getXmlContent(), INCLUDE_TAG);

        if (maybeIncludedSchedules.isPresent()) {
            NodeList includedSchedules = maybeIncludedSchedules.get();
            for (int i = 0; i < includedSchedules.getLength(); i++) {
                Node includedScheduleNode = includedSchedules.item(i);
                errors.addAll(validateInclude(includedScheduleNode));
            }
        }
        return errors;
    }

    private List<ScheduleErrorInfo> validateInclude(Node includeNode) {
        return new ValidationScenario()
                .addRule(new IncludedScheduleValidationRule(scheduleRepository), Optional.of(includeNode))
                .run();
    }

    private static Optional<NodeList> getNodesByTagName(String xml, String tagName) {
        try {
            Document doc = LineNumberTrackingXmlParser.parse(xml);
            NodeList scheduleItems = doc.getElementsByTagName(tagName);
            if (scheduleItems.getLength() != 0) {
                return Optional.of(scheduleItems);
            }
        } catch (ParserException e) {
            LOGGER.error("Couldn't retrieve list of " + tagName + "s from schedule XML", e);
        }
        return Optional.empty();
    }
}
