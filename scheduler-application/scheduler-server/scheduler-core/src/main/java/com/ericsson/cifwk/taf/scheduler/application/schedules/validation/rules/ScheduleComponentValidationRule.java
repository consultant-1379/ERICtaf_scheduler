package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidationUtils.findTestwareByGav;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */
public class ScheduleComponentValidationRule implements ValidationRule {
    private List<Testware> testwares;

    public ScheduleComponentValidationRule(List<Testware> testwares) {
        this.testwares = testwares;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public Optional<ScheduleErrorInfo> validate(Optional<Node> maybeItem) {
        Node componentNode = maybeItem.orElseThrow(() -> new IllegalArgumentException("Node shouldn't be empty"));
        String componentValue = componentNode.getTextContent();
        Optional<Testware> testware = findTestwareByGav(testwares, componentValue);

        if (!testware.isPresent()) {
            return Optional.of(errorForItem(componentNode, componentValue + " not found in list of associated Testware for this Iso"));
        }
        return Optional.empty();
    }
}
