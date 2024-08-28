package com.ericsson.cifwk.taf.scheduler.api.validation;

import com.ericsson.cifwk.taf.scheduler.model.Schedule;

import java.util.List;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static Integer latestVersionOf(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> s.getVersion())
                .mapToInt(i -> i)
                .max()
                .getAsInt();
    }

    public static Long originalId(List<Schedule> schedules, Integer latestVersion) {
        return schedules.stream()
                .filter(s -> s.getVersion().equals(latestVersion))
                .findFirst()
                .map(s -> s.getOriginalId())
                .orElseThrow(IllegalArgumentException::new);
    }
}
