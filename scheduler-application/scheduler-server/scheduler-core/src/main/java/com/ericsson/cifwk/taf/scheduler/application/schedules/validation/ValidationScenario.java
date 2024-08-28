package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.ValidationRule;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.google.common.collect.Lists;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 * <p>
 * Validation scenario allows to create set of rules to validate.
 *
 */
public class ValidationScenario {
    private LinkedList<RuleWithArguments> rules = Lists.newLinkedList();

    private List<ScheduleErrorInfo> errors = Lists.newArrayList();

    public ValidationScenario addRule(ValidationRule rule, Optional<Node> arg) {
        rules.add(new RuleWithArguments().addRule(rule).withArguments(arg));
        return this;
    }

    public List<ScheduleErrorInfo> run() {
        for (RuleWithArguments holder : rules) {
            if (!holder.rule.isMandatory() && !errors.isEmpty()) {
                continue;
            }
            holder.rule.validate(holder.argument).ifPresent(errors::add);
        }

        return errors;
    }

    private static class RuleWithArguments {
        ValidationRule rule;
        Optional<Node> argument;

        public RuleWithArguments withArguments(Optional<Node> node) {
            argument = node;
            return this;
        }

        public RuleWithArguments addRule(ValidationRule rule) {
            this.rule = rule;
            return this;
        }
    }

}
