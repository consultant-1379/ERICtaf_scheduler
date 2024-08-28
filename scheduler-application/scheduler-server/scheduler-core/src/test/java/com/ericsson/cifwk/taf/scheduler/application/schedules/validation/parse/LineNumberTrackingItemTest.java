package com.ericsson.cifwk.taf.scheduler.application.schedules.validation.parse;

import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 20/07/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class LineNumberTrackingItemTest {

    @Mock
    Node item;

    @Before
    public void setUp() {
        when(item.getUserData(LineNumberTracking.ROW_START)).thenReturn("10");
        when(item.getUserData(LineNumberTracking.ROW_END)).thenReturn("100");
        when(item.getUserData(LineNumberTracking.COL_START)).thenReturn("20");
        when(item.getUserData(LineNumberTracking.COL_END)).thenReturn("200");
    }

    @Test
    public void shouldReturnCorrectErrorRange() {
        ErrorRange errorRange = LineNumberTrackingItem.getErrorRange(item);
        assertThat(errorRange.getStartLine(), is(10));
        assertThat(errorRange.getEndLine(), is(100));
        assertThat(errorRange.getStartColumn(), is(20));
        assertThat(errorRange.getEndColumn(), is(200));
    }
}
