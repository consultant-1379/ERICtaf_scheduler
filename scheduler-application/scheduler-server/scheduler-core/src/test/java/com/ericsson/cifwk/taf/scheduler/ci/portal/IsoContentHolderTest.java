package com.ericsson.cifwk.taf.scheduler.ci.portal;

import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.CiArtifact;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.model.IsoContentHolder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class IsoContentHolderTest {

    IsoContentHolder isoContentHolder = new IsoContentHolder();

    @Test
    public void getArtifactList() {
        isoContentHolder.setArtifactList(null);
        assertTrue(isoContentHolder.getArtifactList().isEmpty());

        List<CiArtifact> artifacts = new ArrayList<>();
        isoContentHolder.setArtifactList(artifacts);
        assertSame(artifacts, isoContentHolder.getArtifactList());
    }
}
