package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.google.common.base.Strings;
import org.w3c.dom.Node;

import java.util.Optional;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */
public class RequiredItemValidationRule implements ValidationRule {

    private String tagName;
    private Node parentNode;

    public RequiredItemValidationRule(String tagName, Node parentNode) {
        this.tagName = tagName;
        this.parentNode = parentNode;
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    @Override
    public Optional<ScheduleErrorInfo> validate(Optional<Node> maybeItem) {
        if (!maybeItem.isPresent()) {
            return Optional.of(errorForItem(parentNode, String.format("<%s> should be defined", tagName)));
        }

        Node node = maybeItem.get();
        String nodeValue = node.getTextContent();

        if (Strings.isNullOrEmpty(nodeValue)) {
            return Optional.of(errorForItem(node, String.format("<%s> body should not be empty", tagName)));
        }
        return Optional.empty();
    }
}
