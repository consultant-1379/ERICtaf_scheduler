package com.ericsson.cifwk.taf.scheduler.model;

import com.ericsson.cifwk.taf.scheduler.application.security.SecurityMock;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by evlailj on 17/06/2015.
 */
public class EntityBaseTest {

    public static final String ORIGINAL_USER = "original-user";

    public static final String ANOTHER_USER = "another-user";

    private EntityBase<Long> entity;

    @Before
    public void setUp() {
        entity = new EntityBase<>();
        SecurityMock.mockPrincipal(ORIGINAL_USER);
    }

    @Test
    public void auditFieldsOnPersist() {
        entity.prePersist();
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
        assertEquals(entity.getCreated(), entity.getUpdated());
        assertEquals(ORIGINAL_USER, entity.getCreatedBy());
        assertEquals(ORIGINAL_USER, entity.getUpdatedBy());
    }

    @Test
    public void auditFieldsOnUpdate() throws InterruptedException {

        // entity saved previously
        entity.prePersist();
        Thread.sleep(1); //NOSONAR

        // updated by another user
        SecurityMock.mockPrincipal(ANOTHER_USER);
        entity.prePersist();
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
        assertTrue(entity.getUpdated().getTime() > entity.getCreated().getTime());
        assertEquals(ORIGINAL_USER, entity.getCreatedBy());
        assertEquals(ANOTHER_USER, entity.getUpdatedBy());
    }

    @Test
    public void auditFieldsOnCustomVersionUpdate() throws InterruptedException {

        // new entity, but with defined creation audit fields
        entity.setCreatedBy(ORIGINAL_USER);
        entity.setCreated(new Date(0L));

        // updated by another user
        SecurityMock.mockPrincipal(ANOTHER_USER);
        entity.prePersist();

        assertEquals(new Date(0L), entity.getCreated());
        assertTrue(entity.getUpdated().getTime() > entity.getCreated().getTime());
        assertEquals(ORIGINAL_USER, entity.getCreatedBy());
        assertEquals(ANOTHER_USER, entity.getUpdatedBy());
    }


    @Test
    public void auditFieldsOnCustomVersionUpdateWhenLdapUserAuthenticated() throws InterruptedException {
        SecurityMock.mockLdapPrincipal(ANOTHER_USER);

        // new entity, but with defined creation audit fields
        entity.setCreatedBy(ORIGINAL_USER);
        entity.setCreated(new Date(0L));

        entity.prePersist();

        assertEquals(new Date(0L), entity.getCreated());
        assertTrue(entity.getUpdated().getTime() > entity.getCreated().getTime());
        assertEquals(ORIGINAL_USER, entity.getCreatedBy());
        assertEquals(ANOTHER_USER, entity.getUpdatedBy());
    }

}
