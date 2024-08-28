package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareIsoRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.TestwareRepository;
import com.ericsson.cifwk.taf.scheduler.application.services.DiffHelper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.TestwareMapper;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiTestware;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
public class TestwareUtils {

    @Autowired
    private TestwareIsoRepository testwareIsoRepository;

    @Autowired
    private TestwareRepository testwareRepository;

    @Autowired
    private TestwareMapper testwareMapper;

    public TestwareIso populateTestwareAndSave(TestwareIso testwareIso, Set<TestwareInfo> newTestwareDtos) {
        List<Testware> storedIsoTestwareEntities = testwareRepository.findByIsoId(testwareIso.getId());
        Set<TestwareInfo> storedIsoTestwareDtos = mapEntitiesToDtos(storedIsoTestwareEntities);

        DiffHelper<TestwareInfo> testwareDiff = new DiffHelper<>(newTestwareDtos, storedIsoTestwareDtos);
        List<Testware> testwareListToStore = mapDtosToEntities(testwareDiff.getOnlyInA());

        //check db if testware already exists before adding to iso
        for (Testware t : testwareListToStore) {
            Testware existingTestware = testwareRepository.findByArtifactIdAndVersion(
                    t.getGav().getArtifactId(),
                    t.getGav().getVersion());
            if (existingTestware == null) {
                testwareIso.addTestware(t);
            } else {
                testwareIso.addTestware(existingTestware);
            }
        }
        return testwareIsoRepository.save(testwareIso);
    }

    public void saveNewTestwareFrom(Set<TestwareInfo> latestTestwareDtos) {
        List<Testware> allTestware = testwareRepository.findAll();
        Set<TestwareInfo> allTestwareDtos = mapEntitiesToDtos(allTestware);
        DiffHelper<TestwareInfo> testwareDiff = new DiffHelper<>(latestTestwareDtos, allTestwareDtos);
        List<Testware> newTestware = mapDtosToEntities(testwareDiff.getOnlyInA());
        testwareRepository.save(newTestware);
    }

    public List<Testware> mapDtosToEntities(Set<TestwareInfo> testwareDtos) {
        return map(testwareDtos, dtoToEntity())
                .stream()
                .collect(toList());
    }

    public Set<TestwareInfo> mapCiArtifactsToDtos(List<CiArtifact> ciArtifacts) {
        Collection<TestwareInfo> dtos = map(ciArtifacts, ciToDto());
        return new HashSet<>(dtos);
    }

    public Set<TestwareInfo> mapEntitiesToDtos(List<Testware> entities) {
        Collection<TestwareInfo> dtos = map(entities, entityToDto());
        return new HashSet<>(dtos);
    }

    public Set<TestwareInfo> mapCiTestwareToDtos(List<CiTestware> ciTestware) {
        Collection<TestwareInfo> dtos = map(ciTestware, ciTestwareToDto());
        return new HashSet<>(dtos);
    }

    public Function<CiArtifact, TestwareInfo> ciToDto() {
        return ciArtifact -> {
            TestwareInfo testwareInfo = new TestwareInfo();
            testwareInfo.setGroupId(ciArtifact.getGroup());
            testwareInfo.setArtifactId(ciArtifact.getName());
            testwareInfo.setVersion(ciArtifact.getVersion());
            testwareInfo.setCxpNumber(ciArtifact.getNumber());
            return testwareInfo;
        };
    }

    public Function<CiTestware, TestwareInfo> ciTestwareToDto() {
        return ciTestware -> {
            TestwareInfo testwareInfo = new TestwareInfo();
            testwareInfo.setGroupId(ciTestware.getGroupId());
            testwareInfo.setArtifactId(ciTestware.getArtifactId());
            testwareInfo.setVersion(ciTestware.getVersion());
            testwareInfo.setCxpNumber(extractCxp(ciTestware.getArtifactId()));
            return testwareInfo;
        };
    }

    public static String extractCxp(String artifactId) {
        String cxpPattern = "CXP\\d*([/\\\\]{1})?([0-9]{1})?$";
        Pattern pattern = Pattern.compile(cxpPattern);
        Matcher matcher = pattern.matcher(artifactId);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "UNKNOWN";
    }

    public Function<Testware, TestwareInfo> entityToDto() {
        return entity -> testwareMapper.map(entity);
    }

    public Function<TestwareInfo, Testware> dtoToEntity() {
        return dto -> testwareMapper.map(dto);
    }

    public <S, T> Collection<T> map(Collection<S> sources, Function<S, T> mapper) {
        return sources.stream().map(mapper).collect(toList());
    }

}
