package com.ericsson.oss.axis.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestServiceUtilsTest {

    @Test
    public void testCleanEndpointAddress() throws Exception {
        assertEquals("http://endpoint:8080", RestServiceUtils.cleanEndpointAddress("http://endpoint:8080"));
        assertEquals("http://endpoint:8080", RestServiceUtils.cleanEndpointAddress("http://endpoint:8080/"));
        assertEquals("http://endpoint:8080", RestServiceUtils.cleanEndpointAddress("http://endpoint:8080/ "));
    }

}