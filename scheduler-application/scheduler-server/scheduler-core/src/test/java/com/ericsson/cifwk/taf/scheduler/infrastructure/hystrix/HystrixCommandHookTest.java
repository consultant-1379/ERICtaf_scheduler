package com.ericsson.cifwk.taf.scheduler.infrastructure.hystrix;

import com.netflix.hystrix.HystrixInvokable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 23/07/2015
 */

@RunWith(MockitoJUnitRunner.class)
public class HystrixCommandHookTest {

    @Mock
    Exception e;
    @Mock
    HystrixInvokable invokable;

    private HystrixCommandHook hystrixCommandHook;

    @Before
    public void setUp() {
        hystrixCommandHook = new HystrixCommandHook();
    }

    @Test
    public void onExecutionError_shouldReturnSameException() {
        Exception exception = hystrixCommandHook.onExecutionError(invokable, e);
        assertThat(exception, is(e));
    }
}
