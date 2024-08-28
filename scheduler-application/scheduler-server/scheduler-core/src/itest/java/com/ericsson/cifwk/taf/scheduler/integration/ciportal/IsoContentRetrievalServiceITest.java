package com.ericsson.cifwk.taf.scheduler.integration.ciportal;

import com.ericsson.cifwk.taf.scheduler.Application;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true)
public class IsoContentRetrievalServiceITest {

    @Autowired
    IsoContentRetrievalService isoContentRetrievalService;

    @Test
    public void shouldReturnTestware() {
        Optional<IsoContentHolder> content = isoContentRetrievalService.getTestwareIso("ERICenm_CXP9027091", "1.12.54");
        assertFalse(content.get().getArtifactList().isEmpty());
    }

}
