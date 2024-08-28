package com.ericsson.cifwk.taf.scheduler.application.repository;

import com.ericsson.cifwk.taf.scheduler.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select u from User u where u.externalId = ?1 or u.email = ?1")
    User findByExternalIdOrEmail(String externalId);

    @Query("select u from User u where lower(u.name) like ?1% or u.email like ?1% order by u.name, u.email asc")
    List<User> findByNameOrEmailStartingWith(String name, Pageable limit);
}
