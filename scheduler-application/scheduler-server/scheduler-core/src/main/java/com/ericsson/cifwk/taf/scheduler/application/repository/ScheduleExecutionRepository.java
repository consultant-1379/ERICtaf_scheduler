package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.ScheduleExecution;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleExecutionRepository extends CrudRepository<ScheduleExecution, Long> {

    List<ScheduleExecution> findAll();

    @Query("SELECT s from ScheduleExecution s WHERE s.schedule = :schedule AND s.iso = :iso AND s.testwareIso = :testwareIso")
    ScheduleExecution findByScheduleIsoAndTestwareIso(@Param("schedule") Schedule schedule,
                                                      @Param("iso") ISO iso,
                                                      @Param("testwareIso") TestwareIso testwareIso);
}
