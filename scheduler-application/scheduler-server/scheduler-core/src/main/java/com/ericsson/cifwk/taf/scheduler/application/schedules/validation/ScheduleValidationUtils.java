package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.model.Testware;

import java.util.List;
import java.util.Optional;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         15/10/2015
 */
public final class ScheduleValidationUtils {

    private static final String VERSION_LATEST = "LATEST";
    private static final String VERSION_RELEASE = "RELEASE";

    private ScheduleValidationUtils() {
        //should not be instantiated
    }

    public static Optional<Testware> findTestwareByGav(List<Testware> testwares, String gav) {
        String[] gavParts = gav.trim().split(":");
        if (gavParts.length < 2 || gavParts.length > 3) {
            throw new IllegalArgumentException("GAV should be defined in 'group:artifact:version' format: " + gav);
        }

        String group = gavParts[0];
        String artifactId = gavParts[1];
        String version = (gavParts.length >= 3) ? gavParts[2] : "";

        return testwares.stream()
                .filter(t -> t.getGav().getGroupId().equals(group))
                .filter(t -> t.getGav().getArtifactId().equals(artifactId))
                .filter(t -> t.getGav().getVersion().equals(version)
                                || version.isEmpty()
                                || version.equals(VERSION_LATEST)
                                || version.equals(VERSION_RELEASE)
                )
                .findFirst();
    }
}
