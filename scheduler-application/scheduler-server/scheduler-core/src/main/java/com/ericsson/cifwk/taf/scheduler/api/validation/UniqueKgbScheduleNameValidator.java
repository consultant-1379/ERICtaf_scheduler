package com.ericsson.cifwk.taf.scheduler.api.validation;


import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.application.constant.KgbConstant;
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
import static java.util.stream.Collectors.toList;

public class UniqueKgbScheduleNameValidator implements ConstraintValidator<KgbScheduleNameIsUniqueForTeam, ScheduleInfo> {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DropRepository dropRepository;

    @Override
    public void initialize(KgbScheduleNameIsUniqueForTeam constraintAnnotation) {
        //NOSONAR
    }

    @Override
    public boolean isValid(ScheduleInfo schedule, ConstraintValidatorContext context) {
        DropInfo drop = schedule.getDrop();
        if (drop != null) {
            return true; //non KGB schedule
        }
        Drop kgbDrop = dropRepository.findByProductAndDropNames(KgbConstant.PRODUCT_NAME.value(), KgbConstant.DROP_NAME.value());
        String team = schedule.getTeam();
        List<Schedule> teamKgbSchedules = scheduleRepository.findSchedulesByDropAndTeam(kgbDrop, team);
        if (teamKgbSchedules.isEmpty()) {
            return true;
        }
        List<Schedule> teamKgbSchedulesWithSameName = extractSchedulesWithSameName(teamKgbSchedules, schedule.getName());
        if (teamKgbSchedulesWithSameName.isEmpty()) {
            return true;
        }
        // An update dto will have the same name, version and originalId of the latest schedule version
        Integer latestVersionOfScheduleWithSameName = latestVersionOf(teamKgbSchedulesWithSameName);
        Long originalIdOfLatestVersionWithSameName = originalId(teamKgbSchedulesWithSameName, latestVersionOfScheduleWithSameName);
        return originalIdOfLatestVersionWithSameName.equals(schedule.getId()) &&
                latestVersionOfScheduleWithSameName.equals(schedule.getVersion());
    }

    private static List<Schedule> extractSchedulesWithSameName(List<Schedule> schedules, String name) {
        return schedules.stream()
                .filter(s -> name.equals(s.getName()))
                .collect(toList());
    }
}
