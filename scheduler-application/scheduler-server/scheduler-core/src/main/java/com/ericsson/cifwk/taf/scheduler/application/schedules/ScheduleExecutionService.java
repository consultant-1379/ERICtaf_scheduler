package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.application.constant.IsoDetails;
import com.ericsson.cifwk.taf.scheduler.application.repository.IsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleExecutionRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.TestwareUtils;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.CiPortalClient;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.ScheduleExecution;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ScheduleExecutionService {

    @Autowired
    private ScheduleExecutionRepository executionRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private IsoRepository isoRepository;

    @Autowired
    private TestwareIsoRepository testwareIsoRepository;

    @Autowired
    private TestwareUtils testwareUtils;

    @Autowired
    private CiPortalClient ciPortalClient;

    public Optional<ScheduleExecution> createExecution(Long scheduleId, String isoVersion, String testwareIsoVersion) {
        Schedule schedule = scheduleRepository.findOne(scheduleId);
        if (schedule == null) {
            return Optional.empty();
        }
        Drop drop = schedule.getDrop();
        String productName = drop.getProduct().getName();
        String isoName = IsoDetails.getIsoNameByProduct(productName);
        String testwareIsoName = IsoDetails.getTestwareIsoNameByProduct(productName);

        ISO iso = isoRepository.findByNameAndVersion(isoName, isoVersion);
        if (iso == null) {
            iso = createIso(isoName, isoVersion, drop);
        }

        TestwareIso testwareIso = testwareIsoRepository.findByNameAndVersion(testwareIsoName, testwareIsoVersion);
        if (testwareIso == null) {
            Optional<TestwareIso> created = createTestwareIso(testwareIsoName, testwareIsoVersion, iso);
            if (!created.isPresent()) {
                return Optional.empty();
            } else {
                testwareIso = created.get();
            }
        }

        ScheduleExecution execution = findScheduleExecution(schedule, iso, testwareIso);
        if (execution == null) {
            execution = new ScheduleExecution(schedule, iso, testwareIso);
            execution = executionRepository.save(execution);
        }

        return Optional.of(execution);
    }

    private ISO createIso(String isoName, String isoVersion, Drop drop) {
        ISO iso = new ISO(isoName, isoVersion, drop);
        return isoRepository.save(iso);
    }

    private Optional<TestwareIso> createTestwareIso(String isoName, String isoVersion, ISO iso) {
        TestwareIso testwareIso = new TestwareIso(isoName, isoVersion, iso);
        Optional<IsoContentHolder> testwareIsoContent = ciPortalClient.getTestwareIso(iso.getName(), iso.getVersion());

        if (!testwareIsoContent.isPresent()) {
            return Optional.empty();
        }

        IsoContentHolder content = testwareIsoContent.get();
        List<CiArtifact> ciArtifacts = content.getArtifactList();
        Set<TestwareInfo> testwareDtos = testwareUtils.mapCiArtifactsToDtos(ciArtifacts);
        testwareIso = testwareUtils.populateTestwareAndSave(testwareIso, testwareDtos);

        return Optional.of(testwareIso);
    }

    private ScheduleExecution findScheduleExecution(Schedule schedule, ISO iso, TestwareIso testwareIso) {
        return executionRepository.findByScheduleIsoAndTestwareIso(schedule, iso, testwareIso);
    }
}
