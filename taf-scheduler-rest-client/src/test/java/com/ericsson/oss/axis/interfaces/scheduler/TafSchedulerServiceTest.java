package com.ericsson.oss.axis.interfaces.scheduler;

import com.ericsson.oss.axis.interfaces.scheduler.exceptions.TafSchedulerItemNotFoundException;
import com.ericsson.oss.axis.types.ScheduleType;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class TafSchedulerServiceTest {

    private TafSchedulerService unit;
    private List<TafScheduleInfo> allSchedules;

    @Before
    public void setUp() throws Exception {
        unit = new TafSchedulerService("http://endpointAddress", null, null);
        unit = spy(unit);
        allSchedules = Arrays.asList(
                createSchedule(1L, "s1", 1, false),
                createSchedule(2L, "s1", 2, false),
                createSchedule(3L, "s1", 3, true),
                createSchedule(4L, "s2", 1, false),
                createSchedule(5L, "s3", 1, false)
        );
        doReturn(new ArrayList<TafScheduleInfo>()).when(unit).getSchedules(anyString(), anyString(), any(ScheduleType.class));
        doReturn(allSchedules).when(unit).getSchedules(eq("ENM"), eq("15.4"), eq(ScheduleType.RFA_P));

        doReturn(new ArrayList<TafScheduleInfo>()).when(unit).getKgbSchedules(anyString());
        doReturn(allSchedules).when(unit).getKgbSchedules(eq("Team"));
    }

    @Test
    public void testFindSchedule_notFound() throws Exception {
        assertSame(Optional.absent(), unit.findSchedule(allSchedules, "s4", "1"));
        assertSame(Optional.absent(), unit.findSchedule(allSchedules, "s2", "2"));
    }

    @Test
    public void testFindSchedule_happyPath() throws Exception {
        Optional<TafScheduleInfo> scheduleHolder = unit.findSchedule(allSchedules, "s1", "2");
        assertTrue(scheduleHolder.isPresent());
        assertEquals(2L, scheduleHolder.get().getId().longValue());
    }

    @Test
    public void testFindSchedule_lastVersion() throws Exception {
        Optional<TafScheduleInfo> scheduleHolder = unit.findSchedule(allSchedules, "s1", "");
        assertTrue(scheduleHolder.isPresent());
        TafScheduleInfo schedule = scheduleHolder.get();
        assertEquals(3L, schedule.getId().longValue());
    }

    @Test
    public void testGetSchedule_happyPath() throws Exception {
        TafScheduleInfo schedule = unit.getSchedule("ENM", "15.4", ScheduleType.RFA_P, "s1", "");
        assertEquals(3L, schedule.getId().longValue());
        schedule = unit.getSchedule("ENM", "15.4", ScheduleType.RFA_P, "s1", "2");
        assertEquals(2L, schedule.getId().longValue());
    }

    @Test(expected = TafSchedulerItemNotFoundException.class)
    public void testGetSchedule_notFound() throws Exception {
        unit.getSchedule("ENM", "15.4", ScheduleType.RFA_P, "s1", "22");
        fail("Exception expected");
    }

    @Test
    public void testGetKgbSchedule_happyPath() throws Exception {
        TafScheduleInfo schedule = unit.getKgbSchedule("Team", "s1", "");
        assertEquals(3L, schedule.getId().longValue());
        schedule = unit.getKgbSchedule("Team", "s1", "2");
        assertEquals(2L, schedule.getId().longValue());
    }

    @Test(expected = TafSchedulerItemNotFoundException.class)
    public void testGetKgbSchedule_notFound() throws Exception {
        unit.getKgbSchedule("Team", "s1", "22");
        fail("Exception expected");
    }

    private TafScheduleInfo createSchedule(long id, String name, int version, boolean lastVersion) {
        TafScheduleInfo result = new TafScheduleInfo();
        result.setId(id);
        result.setName(name);
        result.setVersion(version);
        result.setLastVersion(lastVersion);

        return result;
    }

}