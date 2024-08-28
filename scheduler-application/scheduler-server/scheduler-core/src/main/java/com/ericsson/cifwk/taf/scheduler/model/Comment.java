package com.ericsson.cifwk.taf.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Comment")
public class Comment extends EntityBase<Long> {

    @ManyToOne
    @JoinColumn(name = "scheduleId", nullable = false)
    private Schedule schedule;

    @Column(name = "message", nullable = false)
    private String message;

    public Comment() {
        // Empty constructor required
    }

    public Comment(Schedule schedule, String message) {
        this.schedule = schedule;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Comment copy(Schedule schedule) {
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setSchedule(schedule);
        comment.setCreated(getCreated());
        comment.setCreatedBy(getCreatedBy());
        comment.setUpdated(getUpdated());
        comment.setUpdatedBy(getUpdatedBy());
        return comment;
    }
}
