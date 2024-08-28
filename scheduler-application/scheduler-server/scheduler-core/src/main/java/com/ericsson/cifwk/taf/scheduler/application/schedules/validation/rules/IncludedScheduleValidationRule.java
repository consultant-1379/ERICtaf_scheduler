package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.rules;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.w3c.dom.Node;

import java.util.Optional;

public class IncludedScheduleValidationRule implements ValidationRule {

    private ScheduleRepository scheduleRepository;

    public IncludedScheduleValidationRule(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public Optional<ScheduleErrorInfo> validate(Optional<Node> maybeSchedule) {
        Node includedSchedule = maybeSchedule.get();
        String urn = includedSchedule.getFirstChild().getTextContent();
        Long scheduleId = Long.parseLong(urn.split(":")[2]);
        Schedule schedule = scheduleRepository.findOne(scheduleId);
        if (schedule == null) {
            return Optional.of(errorForItem(includedSchedule,
                    "Schedule with id " + scheduleId + " does not exist (" + urn + ")"));
        }
        if (!schedule.isApproved()) {
            return Optional.of(errorForItem(includedSchedule,
                    "Schedule with id " + scheduleId + " is not approved (" + urn + ")"));
        }
        return Optional.empty();
    }

}
