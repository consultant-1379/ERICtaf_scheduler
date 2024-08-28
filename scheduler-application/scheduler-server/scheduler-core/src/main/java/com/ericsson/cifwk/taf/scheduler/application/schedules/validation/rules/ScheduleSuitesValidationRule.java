package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.application.services.DiffHelper;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidationUtils.findTestwareByGav;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */

public class ScheduleSuitesValidationRule implements ValidationRule {

    private List<Testware> testwares;
    private TestRegistryClient testRegistryClient;
    private Optional<Node> component;

    public ScheduleSuitesValidationRule(List<Testware> testwares, TestRegistryClient testRegistryClient, Optional<Node> component) {
        this.testwares = testwares;
        this.testRegistryClient = testRegistryClient;
        this.component = component;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public Optional<ScheduleErrorInfo> validate(Optional<Node> maybeItem) {
        Node componentNode = component.get();

        String componentValue = componentNode.getTextContent();

        TestwareInfo testware = findTestwareByGav(testwares, componentValue)
                .map(t -> mapGavToTestwareInfo(t.getGav()))
                .orElseThrow(() -> new IllegalArgumentException("No testware found for " + componentValue));

        Set<String> scheduleSuites = maybeItem
                .map(Node::getTextContent)
                .map(s -> s.split("\\s*,\\s*")).map(Sets::newHashSet).get();

        List<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> details = testRegistryClient.findTestwareDetails(Lists.newArrayList(testware));
        if (details.size() != 1) {
            return Optional.of(errorForItem(componentNode, "Testware doesn't exist in TAF Test Registry"));
        }

        HashSet<String> existingSuites = Sets.newHashSet(details.get(0).getSuites());
        if (existingSuites.isEmpty()) {
            return Optional.of(errorForItem(componentNode, "No suites exist for Testware in TAF Test Registry"));
        }

        DiffHelper<String> stringDiffHelper = new DiffHelper<>(scheduleSuites, existingSuites);
        Sets.SetView<String> invalidSuites = stringDiffHelper.getOnlyInA();

        if (!invalidSuites.isEmpty()) {
            return Optional.of(errorForItem(maybeItem.get(), String.format("Suites %s don't exist in Testware", invalidSuites)));
        }

        return Optional.empty();
    }

    private static TestwareInfo mapGavToTestwareInfo(Gav gav) {
        TestwareInfo info = new TestwareInfo();
        info.setGroupId(gav.getGroupId());
        info.setArtifactId(gav.getArtifactId());
        info.setVersion(gav.getVersion());
        return info;
    }
}
