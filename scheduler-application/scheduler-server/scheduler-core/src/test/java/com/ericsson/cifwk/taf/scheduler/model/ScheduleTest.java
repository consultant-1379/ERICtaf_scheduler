package com.ericsson.cifwk.taf.scheduler.model;

import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleTest {


    public static final long ORIGINAL_ID = 1L;

    public static final String USER = "enikoal";

    public static final Date ORIGINAL_CREATION_DATE = new Date();
    public static final String TEAM_CI_TOR = "CI-TOR";
    private static final boolean SCHEDULE_VALIDATED = true;

    @Mock
    Drop drop;

    private Schedule firstVersionOfSchedule;

    @Before
    public void setUp() {
        firstVersionOfSchedule = spy(new Schedule(null, 1, null, drop, ApprovalStatus.UNAPPROVED.name(), TEAM_CI_TOR, SCHEDULE_VALIDATED));
        when(firstVersionOfSchedule.getId()).thenReturn(ORIGINAL_ID);
        when(firstVersionOfSchedule.getCreated()).thenReturn(ORIGINAL_CREATION_DATE);
        when(firstVersionOfSchedule.getCreatedBy()).thenReturn(USER);
    }

    @Test
    public void createNewVersion_ShouldIncrementVersion() {
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertThat(newVersion.getVersion(), is(2));
    }

    @Test
    public void createNewVersion_ShouldCreateNewEntity() {
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertNotSame(newVersion, firstVersionOfSchedule);
        assertThat(newVersion.getId(), nullValue());
    }

    @Test
    public void createNewVersion_NotLastVersionAnyMore() {
        assertTrue(firstVersionOfSchedule.isLastVersion());
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertTrue(newVersion.isLastVersion());
        assertFalse(firstVersionOfSchedule.isLastVersion());
    }

    @Test
    public void createNewVersion_OriginalIdIsFinal() {

        // first clone
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertThat(newVersion.getOriginalId(), is(ORIGINAL_ID));

        // clone from clone
        Schedule newVersion2 = firstVersionOfSchedule.createNewVersion();
        assertThat(newVersion2.getOriginalId(), is(ORIGINAL_ID));
    }

    @Test
    public void createNewVersionShouldSetDataToSameValues() {
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertThat(newVersion.getCreated(), is(ORIGINAL_CREATION_DATE));
        assertThat(newVersion.getCreatedBy(), is(USER));
        assertThat(newVersion.getUpdated(), nullValue());
        assertThat(newVersion.getUpdatedBy(), nullValue());
        assertSame(drop, newVersion.getDrop());
    }

    @Test
    public void getOriginalId_ShouldEqualToIdWhenFirstVersion() {
        assertEquals(firstVersionOfSchedule.getOriginalId().longValue(), ORIGINAL_ID);

        //check new version
        Schedule newVersion = firstVersionOfSchedule.createNewVersion();
        assertNotEquals(newVersion.getOriginalId(), newVersion.getId());
    }

    @Test
    public void delete() {
        firstVersionOfSchedule.delete();
        assertTrue(firstVersionOfSchedule.isDeleted());
    }
}
