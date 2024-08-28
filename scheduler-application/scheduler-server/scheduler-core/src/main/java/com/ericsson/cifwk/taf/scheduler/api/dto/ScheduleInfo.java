package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.ericsson.cifwk.taf.scheduler.api.validation.UserHasValidRole;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

@UserHasValidRole
public class ScheduleInfo extends AuditInfo {

    private Long id;

    private String name;

    private TypeInfo type;

    private Integer version;

    private List<Integer> versionList;

    private boolean lastVersion;

    @NotEmpty
    private String xmlContent;

    private DropInfo drop;

    private String approvalStatus;

    private String approvedBy;

    private String approvalMsg;

    @NotEmpty
    private String team;

    private Set<UserInfo> reviewers;

    private boolean valid;

    public ScheduleInfo() {
        this.approvalStatus = ApprovalStatus.UNAPPROVED.name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setType(TypeInfo type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public void setLastVersion(boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public DropInfo getDrop() {
        return drop;
    }

    public void setDrop(DropInfo drop) {
        this.drop = drop;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isLastVersion() {
        return lastVersion;
    }

    public void setIsLastVersion(boolean isLastVersion) {
        this.lastVersion = isLastVersion;
    }

    public List<Integer> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Integer> versionList) {
        this.versionList = versionList;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approved) {
        this.approvalStatus = approved;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovalMsg() {
        return approvalMsg;
    }

    public void setApprovalMsg(String approvalMsg) {
        this.approvalMsg = approvalMsg;
    }

    public Set<UserInfo> getReviewers() {
        return reviewers;
    }

    public void setReviewers(Set<UserInfo> reviewers) {
        this.reviewers = reviewers;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
