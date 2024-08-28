package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.Drop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DropRepository extends CrudRepository<Drop, Long> {

    List<Drop> findAll();

    @Query("select d from Drop d where d.product.name = :productName")
    List<Drop> findByProductName(@Param("productName") String productName);

    @Query("select d from Drop d where d.product.name = :productName and d.name = :dropName")
    Drop findByProductAndDropNames(@Param("productName") String productName, @Param("dropName") String dropName);

}
