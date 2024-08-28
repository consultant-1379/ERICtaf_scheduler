package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.TestwareInfo;
import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.springframework.stereotype.Service;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.TestwareUtils.extractCxp;

/**
 * Created by evlailj on 15/06/2015.
 */
@Service
public class TestwareMapper {

    public TestwareInfo map(Testware entity) {
        TestwareInfo testwareInfo = new TestwareInfo();
        testwareInfo.setId(entity.getId());
        testwareInfo.setGroupId(entity.getGav().getGroupId());
        testwareInfo.setArtifactId(entity.getGav().getArtifactId());
        testwareInfo.setVersion(entity.getGav().getVersion());
        testwareInfo.setCxpNumber(entity.getCxpNumber());
        return testwareInfo;
    }

    public Testware map(TestwareInfo dto) {
        Testware entity = new Testware();
        entity.setGav(mapGav(dto));
        // using extractCxp here because of inconsistencies in CI Portal data. See CIP-12858 and CIP-12889
        entity.setCxpNumber(extractCxp(dto.getCxpNumber()));
        return entity;
    }

    private static Gav mapGav(TestwareInfo t) {
        Gav entity = new Gav();
        entity.setGroupId(t.getGroupId());
        entity.setArtifactId(t.getArtifactId());
        entity.setVersion(t.getVersion());
        return entity;
    }
}
