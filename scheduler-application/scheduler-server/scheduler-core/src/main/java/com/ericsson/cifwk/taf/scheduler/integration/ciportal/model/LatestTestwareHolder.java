package com.ericsson.cifwk.taf.scheduler.integration.ciportal.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "latest-testware")
@XmlAccessorType(XmlAccessType.FIELD)
public class LatestTestwareHolder {

    @XmlElement(name = "testwareArtifact")
    private List<CiTestware> artifacts;

    public List<CiTestware> getArtifatcs() {
        return artifacts;
    }

}
