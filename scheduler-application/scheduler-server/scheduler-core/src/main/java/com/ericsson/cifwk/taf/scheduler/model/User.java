package com.ericsson.cifwk.taf.scheduler.model;

import com.google.common.base.Objects;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isEmpty;


@Entity
@Table(name = "Users")
public class User extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public User() {
        // Empty constructor required
    }

    public User(String externalId, String name, String email) {
        this.externalId = externalId;
        this.name = name;
        this.email = email;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        User user = (User) o;
        return Objects.equal(externalId, user.externalId) &&
                Objects.equal(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), externalId, email);
    }

    @Override
    public String toString() {
        if (!isEmpty(email)) {
            return String.format("%s <%s>", name, email);
        } else {
            return externalId;
        }
    }
}
