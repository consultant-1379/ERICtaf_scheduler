package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    List<Schedule> findAll();

    @Query("select s from Schedule s where s.lastVersion is true and s.drop = :drop and s.deleted = false")
    List<Schedule> findLatestVersionsByDrop(@Param("drop") Drop drop);

    @Query("select s from Schedule s where s.drop = :drop and s.deleted = false and s.approvalStatus = :approved")
    List<Schedule> findApprovedSchedulesByDrop(@Param("drop") Drop drop, @Param("approved") ApprovalStatus approved);

    @Query("select s from Schedule s where s.drop = :drop and s.type = :buildType and s.deleted = false and s.approvalStatus = :approved")
    List<Schedule> findApprovedSchedulesByDropAndType(@Param("drop") Drop drop, @Param("buildType") Integer buildType, @Param("approved") ApprovalStatus approved);

    @Query("select s from Schedule s where s.drop = :drop and s.type = :buildType and s.deleted = false")
    List<Schedule> findSchedulesByDropAndType(@Param("drop") Drop drop, @Param("buildType") Integer buildType);

    @Query("select s from Schedule s where s.lastVersion is true and (s.id = :scheduleId or s.originalId = :scheduleId) and s.deleted = false")
    Schedule findLastVersionByOriginalScheduleId(@Param("scheduleId") Long id);

    @Query("select s from Schedule s where (s.id = :scheduleId or s.originalId = :scheduleId) and s.deleted = false")
    List<Schedule> findAllVersions(@Param("scheduleId") Long id);

    @Query("select s from Schedule s where s.version = :version and (s.id = :scheduleId or s.originalId = :scheduleId) and s.deleted = false")
    Schedule findVersion(@Param("scheduleId") Long id, @Param("version") Integer version);

    @Query("select s from Schedule s where s.type = :id and s.lastVersion is true")
    List<Schedule> findByType(@Param("id") int id);

    @Query("select s from Schedule s where s.drop.name = :dropName and s.name = :scheduleName and s.lastVersion is true")
    Schedule findLatestScheduleByName(@Param("dropName") String dropName, @Param("scheduleName") String scheduleName);

    @Query("select s from Schedule s where s.drop.name = :dropName and s.name = :scheduleName and s.version = :scheduleVersion")
    Schedule findScheduleByNameAndVersion(@Param("dropName") String dropName, @Param("scheduleName") String scheduleName, @Param("scheduleVersion") int scheduleVersion);

    @Query("select distinct s.team from Schedule s")
    List<String> findAllTeams();

    @Modifying
    @Query("delete from Schedule s where s.id = :scheduleId")
    void deleteOriginalVersion(@Param("scheduleId") Long scheduleId);

    @Modifying
    @Query("delete from Schedule s where s.originalId = :scheduleId")
    void deleteDependentVersions(@Param("scheduleId") Long scheduleId);

    @Query("select s from Schedule s where s.name = :name and s.drop = :drop and s.deleted = false")
    List<Schedule> findNonDeletedByNameAndDrop(@Param("name") String name, @Param("drop") Drop drop);

    @Query("select s from Schedule s where s.id in :ids and s.approvalStatus = com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus.APPROVED")
    List<Schedule> findApprovedSchedulesByIds(@Param("ids") List<Long> ids);

    @Query("select s from Schedule s where s.drop = :drop and s.deleted = false and s.approvalStatus = com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus.APPROVED")
    List<Schedule> findApprovedSchedulesByDrop(@Param("drop") Drop drop);

    @Query("select s from Schedule s where s.drop = :drop and s.team = :team and s.deleted = false and s.approvalStatus = com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus.APPROVED")
    List<Schedule> findApprovedSchedulesByDropAndTeam(@Param("drop") Drop drop, @Param("team") String team);

    @Query("select s from Schedule s where s.drop = :drop and s.team = :team and s.deleted = false")
    List<Schedule> findSchedulesByDropAndTeam(@Param("drop") Drop drop, @Param("team") String team);
}
