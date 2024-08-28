package com.ericsson.cifwk.taf.scheduler.api.dto;


import com.google.common.collect.Sets;

import java.util.Set;

public class TestwareInfo extends ArtifactInfo {

    private String cxpNumber;

    private Boolean included = false;

    private Boolean distinguishedSuites = false;

    private Set<String> selectedSuites = Sets.newHashSet();

    private Set<String> existingSuites = Sets.newHashSet();

    public String getCxpNumber() {
        return cxpNumber;
    }

    public void setCxpNumber(String cxpNumber) {
        this.cxpNumber = cxpNumber;
    }

    public Boolean isIncluded() {
        return included;
    }

    public void setIncluded(Boolean included) {
        this.included = included;
    }

    public Boolean isDistinguishedSuites() {
        return distinguishedSuites;
    }

    public void setDistinguishedSuites(Boolean distinguishedSuites) {
        this.distinguishedSuites = distinguishedSuites;
    }

    public Set<String> getSelectedSuites() {
        return selectedSuites;
    }

    public void addAllSelectedSuites(Set<String> suitesNames) {
        selectedSuites.addAll(suitesNames);
    }

    public Set<String> getExistingSuites() {
        return existingSuites;
    }

    public void addAllExistingSuites(Set<String> suitesNames) {
        existingSuites.addAll(suitesNames);
    }

}
