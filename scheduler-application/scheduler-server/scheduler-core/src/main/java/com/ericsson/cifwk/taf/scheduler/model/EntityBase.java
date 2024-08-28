package com.ericsson.cifwk.taf.scheduler.model;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@MappedSuperclass
public class EntityBase<PK extends Serializable> extends AbstractPersistable<PK> implements Serializable { // NOSONAR

    public static final String UNKNOWN_USER = "UNKNOWN_USER";

    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "createdBy", nullable = false)
    private String createdBy;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "updatedBy")
    private String updatedBy;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        Date now = new Date();
        String user = getUser();

        // init creation audit fields on demand
        if (this.getCreated() == null) {
            this.setCreatedBy(user);
            this.setCreated(now);
        }

        this.setUpdatedBy(user);
        this.setUpdated(now);
    }

    protected String getUser() {
        Optional<Authentication> maybeAuthentication = ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication);
        String user = UNKNOWN_USER;
        if (maybeAuthentication.isPresent()) {
            Object principal = maybeAuthentication.get().getPrincipal();
            if (principal instanceof UserDetails) {
                user = ((UserDetails) principal).getUsername();
            } else {
                user = principal.toString();
            }
        }
        return user;
    }

    public Date getCreated() {
        return created;
    }

    protected void setCreated(Date created) {
        this.created = created;
    }

    protected void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdated() {
        return updated;
    }

    protected void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    protected void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
