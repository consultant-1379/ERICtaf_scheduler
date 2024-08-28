package com.ericsson.cifwk.taf.scheduler.api.validation;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.security.SecurityService;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UserRoleValidator implements ConstraintValidator<UserHasValidRole, ScheduleInfo> {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void initialize(UserHasValidRole constraintAnnotation) {
        //ignore
    }

    @Override
    public boolean isValid(ScheduleInfo dto, ConstraintValidatorContext context) {
        List<String> userRoles = securityService.getCurrentUser().getRoles();
        if (dto.getId() == null) {
            return userRoles.contains(dto.getTeam());
        } else {
            Schedule entity = scheduleRepository.findVersion(dto.getId(), dto.getVersion());
            return userRoles.contains(entity.getTeam()) && userRoles.contains(dto.getTeam());
        }
    }
}
