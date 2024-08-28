package com.ericsson.oss.axis.types;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Created by eniakel on 26/04/2016.
 */
public class ScheduleTypeTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testFindScheduleByType() throws Exception {
        ScheduleType type = ScheduleType.fromTypeId("MTE-P");
        assertEquals("MTE-P", type.getTypeId());
    }

    @Test
    public void testFindSchedule_TypeDoesNotExist() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ScheduleType.fromTypeId("NOT_VALID_TYPE");
    }
}
