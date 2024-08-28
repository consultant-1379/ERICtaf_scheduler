package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules.ValidationRule;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 17/07/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationScenarioTest {

    ValidationScenario scenario;

    @Mock
    ValidationRule rule;

    @Mock
    ValidationRule skipRule;

    @Mock
    Node node;

    @Mock
    ScheduleErrorInfo error;

    Optional<Node> optionalNode;

    @Before
    public void setUp() {
        scenario = new ValidationScenario();
        optionalNode = Optional.of(node);
        when(skipRule.validate(any())).thenReturn(Optional.of(error));
        when(skipRule.isMandatory()).thenReturn(false);
        when(rule.validate(any())).thenReturn(Optional.of(error));
        when(rule.isMandatory()).thenReturn(true);
    }

    @Test
    public void shouldCallValidate() {
        scenario
                .addRule(rule, optionalNode)
                .run();

        verify(rule).validate(optionalNode);
    }

    @Test
    public void shouldReturnErrors() {
        List<ScheduleErrorInfo> run = scenario
                .addRule(rule, optionalNode)
                .addRule(rule, optionalNode)
                .run();

        assertThat(run, hasSize(2));
    }

    @Test
    public void shouldSkipRulesWhenErrorHappenedAndReturnPreviousErrors() {
        List<ScheduleErrorInfo> run = scenario
                .addRule(rule, optionalNode)
                .addRule(skipRule, optionalNode)
                .run();

        assertThat(run, hasSize(1));
        verify(skipRule, times(0)).validate(any());
    }

    @Test
    public void shouldSkipOnlyProperDefinedRulesWhenErrorHappened() {

        List<ScheduleErrorInfo> run = scenario
                .addRule(rule, optionalNode)
                .addRule(skipRule, optionalNode)
                .addRule(rule, optionalNode)
                .run();

        assertThat(run, hasSize(2));
        verify(skipRule, times(0)).validate(any());
    }

}
