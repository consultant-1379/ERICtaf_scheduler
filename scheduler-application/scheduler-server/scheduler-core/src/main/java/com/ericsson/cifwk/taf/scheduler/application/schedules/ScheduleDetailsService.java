package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.LineNumberTrackingXmlParser;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse.ParserException;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.TestwareMapper;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleDetailsService.class);

    private static final String ITEM_TAG = "item";
    private static final String COMPONENT_TAG = "component";
    private static final String SUITES_TAG = "suites";

    @Autowired
    private TestwareRepository testwareRepository;
    @Autowired
    private TestwareMapper testwareMapper;
    @Autowired
    private TestRegistryClient testRegistryClient;

    public List<TestwareInfo> getExistingTestwareListFromScheduleXml(String scheduleXml, Long testwareIsoId) {
        List<TestwareInfo> testwareInfoList = Lists.newArrayList();

        Optional<NodeList> optionalScheduleItems = getItemsFromXml(scheduleXml);
        if (!optionalScheduleItems.isPresent()) {
            return testwareInfoList;
        }

        NodeList scheduleItems = optionalScheduleItems.get();
        for (int i = 0; i < scheduleItems.getLength(); i++) {
            Node itemNode = scheduleItems.item(i);

            Optional<String> maybeComponentValue = parseItem(itemNode);
            if (maybeComponentValue.isPresent()) {
                Optional<TestwareInfo> maybeTestwareInfo = getTestwareInfoByComponentValue(maybeComponentValue.get(), testwareIsoId);
                maybeTestwareInfo.ifPresent(testwareInfo -> {
                    int indexOf = testwareInfoList.indexOf(testwareInfo);
                    if (indexOf < 0) {
                        testwareInfoList.add(testwareInfo);
                    } else {
                        testwareInfo = testwareInfoList.get(indexOf);
                    }

                    populateWithSelectedSuites(testwareInfo, itemNode);
                });
            }
        }
        populateWithSuitesFromTR(testwareInfoList);
        return testwareInfoList;
    }

    private void populateWithSuitesFromTR(List<TestwareInfo> testwareInfoList) {
        List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestwareList =
                testRegistryClient.findTestwareDetails(testwareInfoList);

        if (trTestwareList.size() < testwareInfoList.size()) {
            LOGGER.warn("Some testware were not found in the Test Registry!");
        }

        for (TestwareInfo testwareInfo : testwareInfoList) {
            Optional<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestware =
                    findTrTestwareByTestwareInfo(trTestwareList, testwareInfo);

            if (!trTestware.isPresent()) {
                LOGGER.warn("No testware exist in TAF Test Registry!");
                continue;
            }
            String[] suites = trTestware.get().getSuites();
            if (suites.length < 1) {
                LOGGER.warn("No suites exist for testware!", trTestware);
                continue;
            }
            testwareInfo.addAllExistingSuites(Sets.newHashSet(suites));
        }
    }

    private static Optional<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> findTrTestwareByTestwareInfo(
            List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> trTestwareList,
            TestwareInfo testwareInfo) {

        for (com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware testware : trTestwareList) {
            if (testware.getGroupId().equals(testwareInfo.getGroupId()) &&
                    testware.getArtifactId().equals(testwareInfo.getArtifactId()) &&
                    testware.getVersion().equals(testwareInfo.getVersion())) {
                return Optional.of(testware);
            }
        }
        return Optional.empty();
    }

    private void populateWithSelectedSuites(TestwareInfo testwareInfo, Node itemNode) {
        Optional<Node> suitesNode = findChildByTag(SUITES_TAG, itemNode);

        // get suites xml file names
        Set<String> scheduleSuites = suitesNode
                .map(Node::getTextContent)
                .map(s -> s.split("\\s*,\\s*"))
                .map(Sets::newHashSet)
                .get();

        // remove empty strings
        Set<String> filteredSuites = scheduleSuites.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
        testwareInfo.addAllSelectedSuites(filteredSuites);
    }

    private static Optional<String> parseItem(Node itemNode) {
        Optional<Node> componentNode = findChildByTag(COMPONENT_TAG, itemNode);
        if (!componentNode.isPresent()) {
            LOGGER.warn("Item node is empty", itemNode.getTextContent());
            return Optional.empty();
        }
        return Optional.of(componentNode.get().getTextContent());
    }

    private Optional<TestwareInfo> getTestwareInfoByComponentValue(String componentValue, Long testwareIsoId) {
        String[] gavParts = componentValue.trim().split(":");
        if (gavParts.length < 2 || gavParts.length > 3) {
            LOGGER.warn("GAV has incorrect 'group:artifact:version' format:", componentValue);
            return Optional.empty();
        }
        String groupId = gavParts[0];
        String artifactId = gavParts[1];
        String version = (gavParts.length >= 3 && !"LATEST".equals(gavParts[2])) ? gavParts[2] : "";

        Testware testware = null;
        if (Strings.isNullOrEmpty(version)) {
            List<Testware> testwareList =
                    testwareRepository.findLatestByGroupAndArtifactId(groupId, artifactId, testwareIsoId, new PageRequest(0, 1));
            if (!testwareList.isEmpty()) {
                testware = testwareList.get(0);
            }
        } else {
            testware = testwareRepository.findByGroupAndArtifactIdAndVersion(groupId, artifactId, version);
        }
        if (testware == null) {
            return Optional.empty();
        }
        TestwareInfo testwareInfo = testwareMapper.map(testware);
        return Optional.of(testwareInfo);
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

    private static Optional<NodeList> getItemsFromXml(String xml) {
        try {
            Document doc = LineNumberTrackingXmlParser.parse(xml);
            NodeList scheduleItems = doc.getElementsByTagName(ITEM_TAG);
            if (scheduleItems.getLength() != 0) {
                return Optional.of(scheduleItems);
            }
        } catch (ParserException e) {
            LOGGER.error("Couldn't retrieve list of items from schedule XML", e);
        }
        return Optional.empty();
    }
}
