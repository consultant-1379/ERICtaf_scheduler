package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestwareRepository extends CrudRepository<Testware, Long> {

    List<Testware> findAll();

    @Query("SELECT i.testwares FROM TestwareIso i WHERE i.name = :isoName AND i.version = :isoVersion")
    List<Testware> findByIsoVersionAndName(@Param("isoName") String isoName, @Param("isoVersion") String isoVersion);

    @Query("SELECT i.testwares FROM TestwareIso i WHERE i.id = :isoId")
    List<Testware> findByIsoId(@Param("isoId") Long isoId);

    @Query("SELECT t FROM Testware t WHERE t.gav.artifactId = :artifactId AND t.gav.version = :version")
    Testware findByArtifactIdAndVersion(@Param("artifactId") String artifactId, @Param("version") String version);

    @Query("SELECT t FROM Testware t " +
           "WHERE t.gav.groupId = :groupId AND t.gav.artifactId = :artifactId AND t.gav.version = :version")
    Testware findByGroupAndArtifactIdAndVersion(
            @Param("groupId") String groupId,
            @Param("artifactId") String artifactId,
            @Param("version") String version);

    @Query("SELECT t FROM Testware t left join t.isos i " +
           "WHERE t.gav.groupId = :groupId AND t.gav.artifactId = :artifactId AND i.id = :testwareIsoId ORDER BY t.id DESC")
    List<Testware> findLatestByGroupAndArtifactId(
            @Param("groupId") String groupId,
            @Param("artifactId") String artifactId,
            @Param("testwareIsoId") Long testwareIsoId,
            Pageable pageable);

}
