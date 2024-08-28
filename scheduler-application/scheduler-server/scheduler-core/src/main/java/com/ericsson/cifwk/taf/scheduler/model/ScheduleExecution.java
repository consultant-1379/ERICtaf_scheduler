package com.ericsson.cifwk.taf.scheduler.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ScheduleExecution")
public class ScheduleExecution extends AbstractPersistable<Long> implements Serializable {

    @ManyToOne
    @JoinColumn(name = "scheduleId", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "isoId", nullable = false)
    private ISO iso;

    @ManyToOne
    @JoinColumn(name = "testwareIsoId", nullable = false)
    private TestwareIso testwareIso;

    public ScheduleExecution() {
        // Empty constructor required
    }

    public ScheduleExecution(Schedule schedule, ISO iso, TestwareIso testwareIso) {
        this.schedule = schedule;
        this.iso = iso;
        this.testwareIso = testwareIso;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public ISO getIso() {
        return iso;
    }

    public void setIso(ISO iso) {
        this.iso = iso;
    }

    public TestwareIso getTestwareIso() {
        return testwareIso;
    }

    public void setTestwareIso(TestwareIso testwareIso) {
        this.testwareIso = testwareIso;
    }
}
