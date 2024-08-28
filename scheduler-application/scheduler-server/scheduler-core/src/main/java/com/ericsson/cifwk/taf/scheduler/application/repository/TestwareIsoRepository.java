package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.ISO;
import com.ericsson.cifwk.taf.scheduler.model.TestwareIso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestwareIsoRepository extends CrudRepository<TestwareIso, Long> {

    List<TestwareIso> findAll();

    TestwareIso findByNameAndVersion(String name, String version);

    @Query("SELECT t FROM TestwareIso t WHERE t.iso = :iso")
    List<TestwareIso> findByProductIso(@Param("iso") ISO iso);

    @Query("select i from TestwareIso i left join i.iso.drops d left join d.product p where p.name = :product and d.name = :drop and i.latestInDrop = true")
    TestwareIso findLatestByProductAndDrop(@Param("product") String product, @Param("drop") String drop);
}
