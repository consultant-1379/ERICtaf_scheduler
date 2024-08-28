package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.application.constant.IsoDetails;
import com.ericsson.cifwk.taf.scheduler.application.constant.KgbConstant;
import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.IsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleDetailsService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleService;
import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.TestwareUtils;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.CiPortalClient;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.LatestTestwareHolder;
import com.ericsson.cifwk.taf.scheduler.integration.registry.TestRegistryClient;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TestwareService {

    private static final String NO_ISO = "None";

    @Autowired
    private CiPortalClient ciPortalClient;

    @Autowired
    private TestRegistryClient testRegistryClient;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private IsoRepository isoRepository;

    @Autowired
    private TestwareIsoRepository testwareIsoRepository;

    @Autowired
    private TestwareRepository testwareRepository;

    @Autowired
    private TestwareUtils testwareUtils;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleDetailsService scheduleDetailsService;

    @Transactional
    public List<TestwareInfo> getLatestTestwareForDrop(String productName, String dropName, Long scheduleId) {
        Optional<TestwareIso> maybeTestwareIso = getTestwareIso(productName, dropName);
        if (maybeTestwareIso.isPresent()) {
            TestwareIso testwareIso = maybeTestwareIso.get();
            return getTestwareListWithUsedTestware(testwareIso, scheduleId);
        }
        return Lists.newArrayList();
    }

    @Transactional
    public List<TestwareInfo> getLatestTestware(Long scheduleId) {
        TestwareIso kgbTestwareIso = updateKgbTestwareIso();
        return getTestwareListWithUsedTestware(kgbTestwareIso, scheduleId);
    }

    private TestwareIso updateKgbTestwareIso() {
        LatestTestwareHolder holder = ciPortalClient.getLatestTestware().get();
        Set<TestwareInfo> testwareInfos = testwareUtils.mapCiTestwareToDtos(holder.getArtifatcs());
        testwareUtils.saveNewTestwareFrom(testwareInfos);

        TestwareIso kgbTestwareIso =
                testwareIsoRepository.findByNameAndVersion(KgbConstant.TW_ISO_NAME.value(), KgbConstant.TW_ISO_VERSION.value());
        kgbTestwareIso.removeTestware();

        testwareUtils.populateTestwareAndSave(kgbTestwareIso, testwareInfos);
        return testwareIsoRepository.save(kgbTestwareIso);
    }

    private Optional<TestwareIso> getTestwareIso(String productName, String dropName) {
        String isoName = IsoDetails.getIsoNameByProduct(productName);
        String latestVersion = ciPortalClient.getLatestIsoVersion(productName, dropName);

        if (NO_ISO.equalsIgnoreCase(latestVersion)) {
            return Optional.empty();
        }

        Drop drop = dropRepository.findByProductAndDropNames(productName, dropName);
        ISO latestIso = isoRepository.findByNameAndVersion(isoName, latestVersion);

        // ISO is not in db so obsolete previous latest ISOs (product and testware) and create new ISOs which are now latest.
        if (latestIso == null) {
            latestIso = createNewProductIsoAndMarkAsLatest(isoName, latestVersion, drop);
        }

        return getOrCreateLatestTestwareIso(latestIso, drop);
    }

    private ISO createNewProductIsoAndMarkAsLatest(String isoName, String isoVersion, Drop drop) {
        obsoleteCurrentProductIso(drop);
        return createNewProductIso(isoName, isoVersion, drop, true);
    }

    private void obsoleteCurrentProductIso(Drop drop) {
        String productName = drop.getProduct().getName();
        String dropName = drop.getName();
        ISO currentIso = isoRepository.findLatestByProductAndDrop(productName, dropName);
        if (currentIso != null) {
            currentIso.obsolete();
            isoRepository.save(currentIso);
        }
    }

    private ISO createNewProductIso(String isoName, String isoVersion, Drop drop, boolean latestInDrop) {
        ISO newIso = new ISO(isoName, isoVersion, drop);
        if (latestInDrop) {
            newIso.setLatestInDrop();
        }
        return isoRepository.save(newIso);
    }

    private Optional<TestwareIso> getOrCreateLatestTestwareIso(ISO iso, Drop drop) {
        Optional<IsoContentHolder> maybeTestwareIsoContent = ciPortalClient.getTestwareIso(iso.getName(), iso.getVersion());

        if (!maybeTestwareIsoContent.isPresent()) {
            return Optional.empty();
        }

        IsoContentHolder content = maybeTestwareIsoContent.get();
        String latestTestwareIsoName = content.getTestwareIsoName();
        String latestTestwareIsoVersion = content.getTestwareIsoVersion();
        List<CiArtifact> ciArtifacts = content.getArtifactList();

        TestwareIso latestTestwareIso = testwareIsoRepository.findByNameAndVersion(latestTestwareIsoName, latestTestwareIsoVersion);

        if (latestTestwareIso != null) {
            return Optional.of(latestTestwareIso);
        } else {
            obsoleteCurrentTestwareIso(drop);
            Set<TestwareInfo> testwareDtos = testwareUtils.mapCiArtifactsToDtos(ciArtifacts);
            TestwareIso testwareIso = createNewTestwareIso(latestTestwareIsoName, latestTestwareIsoVersion, iso, testwareDtos, true);
            return Optional.of(testwareIso);
        }
    }

    private List<TestwareInfo> getTestwareListWithUsedTestware(TestwareIso testwareIso, Long scheduleId) {
        List<Testware> latestTestware = testwareRepository.findByIsoId(testwareIso.getId());

        Set<TestwareInfo> testwareDtos = testwareUtils.mapEntitiesToDtos(latestTestware);
        List<TestwareInfo> testwareInfoList = Lists.newArrayList(testwareDtos);
        if (scheduleId == null) {
            return testwareInfoList;
        }

        Optional<ScheduleInfo> schedule = scheduleService.getSchedule(scheduleId);
        if (!schedule.isPresent()) {
            return testwareInfoList;
        }

        List<TestwareInfo> foundTestwareInfos = scheduleDetailsService
                .getExistingTestwareListFromScheduleXml(schedule.get().getXmlContent(), testwareIso.getId());

        mergeTestwareChanges(testwareInfoList, foundTestwareInfos);

        return testwareInfoList;
    }

    private void obsoleteCurrentTestwareIso(Drop drop) {
        String productName = drop.getProduct().getName();
        String dropName = drop.getName();
        TestwareIso currentLatest = testwareIsoRepository.findLatestByProductAndDrop(productName, dropName);
        if (currentLatest != null) {
            currentLatest.obsolete();
            testwareIsoRepository.save(currentLatest);
        }
    }

    private TestwareIso createNewTestwareIso(String isoName, String isoVersion, ISO iso, Set<TestwareInfo> testwareDtos, boolean latestInDrop) {
        TestwareIso testwareIso = new TestwareIso(isoName, isoVersion, iso);
        if (latestInDrop) {
            testwareIso.setLatestInDrop();
        }
        return testwareUtils.populateTestwareAndSave(testwareIso, testwareDtos);
    }

    @Transactional
    public List<TestwareInfo> getTestwareListByScheduleXml(String productName, String dropName, String scheduleXml) {
        Optional<TestwareIso> testwareIso =
                (productName == null || dropName == null) ? Optional.of(updateKgbTestwareIso()) : getTestwareIso(productName, dropName);

        if (testwareIso.isPresent()) {
            List<TestwareInfo> testwareInfoList = scheduleDetailsService
                    .getExistingTestwareListFromScheduleXml(scheduleXml, testwareIso.get().getId());
            testwareInfoList.forEach(this::populateTestwareInfoForDistinctions);
            return testwareInfoList;
        }
        return Lists.newArrayList();
    }

    private void mergeTestwareChanges(List<TestwareInfo> testwareInfoList, List<TestwareInfo> foundTestwareInfos) {
        if (foundTestwareInfos.isEmpty()) {
            return;
        }

        for (TestwareInfo foundTestwareInfo : foundTestwareInfos) {
            int indexOf = testwareInfoList.indexOf(foundTestwareInfo);
            if (indexOf < 0) {
                continue;
            }
            TestwareInfo testwareInfo = testwareInfoList.get(indexOf);
            Set<String> selectedSuites = foundTestwareInfo.getSelectedSuites();
            testwareInfo.addAllSelectedSuites(selectedSuites);
            Set<String> existingSuites = foundTestwareInfo.getExistingSuites();
            testwareInfo.addAllExistingSuites(existingSuites);

            populateTestwareInfoForDistinctions(testwareInfo);
        }
    }

    private void populateTestwareInfoForDistinctions(TestwareInfo testwareInfo) {
        testwareInfo.setIncluded(true);
        Boolean distinguishedSuites = isDistinguishedSuites(
                testwareInfo.getSelectedSuites(),
                testwareInfo.getExistingSuites()
        );
        testwareInfo.setDistinguishedSuites(distinguishedSuites);
    }

    private static Boolean isDistinguishedSuites(Set<String> selectedSuites, Set<String> existingSuites) {
        if (existingSuites.size() != selectedSuites.size()) {
            return true;
        }
        Sets.SetView<String> distinguishedSuites = Sets.difference(existingSuites, selectedSuites);
        return !distinguishedSuites.isEmpty();
    }

    public List<String> getSuites(Long testwareId) {
        Testware testware = testwareRepository.findOne(testwareId);
        TestwareInfo testwareInfo = testwareUtils.entityToDto().apply(testware);
        Optional<com.ericsson.cifwk.taf.scheduler.integration.registry.model.Testware> artifact =
                testRegistryClient.findTestwareDetailsByArtifact(testwareInfo);

        if (artifact.isPresent()) {
            return Lists.newArrayList(artifact.get().getSuites());
        }
        return Collections.emptyList();
    }

}
