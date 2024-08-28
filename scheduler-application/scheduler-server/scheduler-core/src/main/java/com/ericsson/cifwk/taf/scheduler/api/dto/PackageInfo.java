package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class PackageInfo extends ArtifactInfo {

    private String cxpNumber;

    private Set<IsoInfo> isos = Sets.newHashSet();

    private List<TestwareInfo> testwareList = Lists.newArrayList();

    public String getCxpNumber() {
        return cxpNumber;
    }

    public void setCxpNumber(String cxpNumber) {
        this.cxpNumber = cxpNumber;
    }

    public Set<IsoInfo> getIsos() {
        return isos;
    }

    public void addIsos(Set<IsoInfo> isos) {
        this.isos = isos;
    }

    public List<TestwareInfo> getTestwareList() {
        return testwareList;
    }

    public void setTestwareList(List<TestwareInfo> testwareList) {
        this.testwareList = testwareList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageInfo)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PackageInfo that = (PackageInfo) o;
        return Objects.equal(cxpNumber, that.cxpNumber);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return 31 * result + Objects.hashCode(cxpNumber);
    }
}
