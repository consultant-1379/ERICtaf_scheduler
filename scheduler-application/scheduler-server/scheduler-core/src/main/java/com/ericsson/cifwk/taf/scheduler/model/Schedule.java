package com.ericsson.cifwk.taf.scheduler.model;

import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Schedule")
public class Schedule extends EntityBase<Long> implements Serializable {

    @Column(name = "title", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "xml", nullable = false)
    private String xml;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "isLastVersion")
    private boolean lastVersion = true;

    @Column(name = "originalScheduleId")
    private Long originalId;

    @ManyToOne
    @JoinColumn(name = "dropId", nullable = false)
    private Drop drop;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    private List<Comment> comments;

    @Column(name = "approvalStatus")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "approvedBy")
    private String approvedBy;

    @Column(name = "approvalMsg")
    private String approvalMsg;

    @ManyToMany
    @JoinTable(name = "ScheduleReviewers",
            joinColumns = {@JoinColumn(name = "scheduleId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "userId", referencedColumnName = "id")})
    private Set<User> reviewers;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "team")
    private String team;

    @Column(name = "valid")
    private boolean valid;

    protected Schedule() {
    }

    public Schedule(String name, int type, String xml, Drop drop, String approvalStatus, String team, boolean valid) {
        this.name = name;
        this.type = type;
        this.xml = xml;
        this.drop = drop;
        this.reviewers = new HashSet<>();
        this.comments = Lists.newLinkedList();
        this.approvalStatus = ApprovalStatus.valueOf(approvalStatus);
        this.team = team;
        this.valid = valid;
    }

    /**
     * Hidden API, for ORM usage only
     */
    protected void setDrop(Drop drop) {
        this.drop = drop;
    }

    /**
     * Hidden API, for ORM usage only
     */
    protected void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Hidden API, for ORM usage only
     */
    protected void setLastVersion(boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * Hidden API, for ORM usage only
     */
    protected void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Drop getDrop() {
        return drop;
    }

    public Integer getVersion() {
        return version;
    }

    public boolean isLastVersion() {
        return lastVersion;
    }

    public Long getOriginalId() {
        return originalId == null ? getId() : originalId;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
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

    public Set<User> getReviewers() {
        return reviewers;
    }

    public void setReviewers(Set<User> reviewers) {
        this.reviewers = reviewers;
    }

    public void addReviewer(User reviewer) {
        reviewers.add(reviewer);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Schedule createNewVersion() {
        Schedule schedule = new Schedule(name, type, xml, drop, approvalStatus.name(), team, valid);
        schedule.setCreated(getCreated());
        schedule.setCreatedBy(getCreatedBy());
        schedule.version = getVersion() + 1;
        schedule.lastVersion = true;
        schedule.originalId = getOriginalId();

        //when new version is created, this schedule is not last anymore
        lastVersion = false;
        return schedule;
    }

    public void approve(String approvalMsg) {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedBy = getUser();
        this.approvalMsg = approvalMsg;
    }

    public void reject(String approvalMsg) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.approvedBy = getUser();
        this.approvalMsg = approvalMsg;
    }

    public void revokeApproval() {
        this.approvalStatus = ApprovalStatus.UNAPPROVED;
        this.approvedBy = null;
        this.approvalMsg = null;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    public void addComment(String message) {
        comments.add(new Comment(this, message));
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
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

    public void setValidStatus(boolean valid) {
        this.valid = valid;
    }

    public Schedule copyForPromotionTo(Drop d) {
        Schedule promotedSchedule = new Schedule(this.getName(), this.getType(), this.getXml(), d, this.getApprovalStatus().toString(), this.getTeam(), this.isValid());
        promotedSchedule.setOriginalId(null);
        promotedSchedule.setVersion(1);
        promotedSchedule.setApprovedBy(this.getApprovedBy());
        promotedSchedule.setApprovalMsg(this.getApprovalMsg());
        return promotedSchedule;
    }
}
