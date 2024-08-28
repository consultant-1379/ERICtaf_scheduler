package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IsoRepository extends CrudRepository<ISO, Long> {

    ISO findByNameAndVersion(String name, String version);

    List<ISO> findAll();

    @Query("select d.isos from Drop d where d = :drop")
    List<ISO> findByDrop(@Param("drop") Drop drop);

    @Query("select i from ISO i left join i.drops d left join d.product p where p.name = :product and d.name = :drop and i.name = :iso")
    ISO findByProductDropAndName(@Param("product") String product, @Param("drop") String drop, @Param("iso") String iso);

    @Query("select i from ISO i left join i.drops d left join d.product p where p.name = :product and d.name = :drop and i.latestInDrop = true")
    ISO findLatestByProductAndDrop(@Param("product") String product, @Param("drop") String drop);

}
