package com.ericsson.cifwk.taf.scheduler.api.validation;

import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.ericsson.cifwk.taf.scheduler.api.validation.ValidationUtils.latestVersionOf;
import static com.ericsson.cifwk.taf.scheduler.api.validation.ValidationUtils.originalId;


public class UniqueScheduleNameValidator implements ConstraintValidator<ScheduleNameIsUniqueInDrop, ScheduleInfo> {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DropRepository dropRepository;

    @Override
    public void initialize(ScheduleNameIsUniqueInDrop constraintAnnotation) {
       //NOSONAR
    }

    @Override
    public boolean isValid(ScheduleInfo scheduleInfo, ConstraintValidatorContext context) {
        DropInfo dropInfo = scheduleInfo.getDrop();
        if (dropInfo == null) {
            return true; // kgb schedules have no associated drop
        }
        String name = scheduleInfo.getName();
        Drop drop = dropRepository.findByProductAndDropNames(dropInfo.getProductName(), dropInfo.getName());
        List<Schedule> schedulesWithSameName = scheduleRepository.findNonDeletedByNameAndDrop(name, drop);
        if (schedulesWithSameName.isEmpty()) {
            return true;
        } else {
            // An update dto will have the same name, version and originalId of the latest schedule version
            Integer latestVersionOfScheduleWithSameName = latestVersionOf(schedulesWithSameName);
            Long originalIdOfLatestVersionWithSameName = originalId(schedulesWithSameName, latestVersionOfScheduleWithSameName);
            return originalIdOfLatestVersionWithSameName.equals(scheduleInfo.getId()) &&
                    latestVersionOfScheduleWithSameName.equals(scheduleInfo.getVersion());
        }
    }
}
