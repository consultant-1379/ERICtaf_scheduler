package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 23/07/2015
 */
public class RemoteSchemaResolverTest {

    RemoteSchemaResolver schemaResolver;

    @Before
    public void setUp() throws Exception {
        schemaResolver = new RemoteSchemaResolver();
    }

    @Test(expected = RuntimeException.class)
    public void testGetSchemaAsStream_shouldThrowExceptionWhenUrlIsInvalid() throws Exception {
        schemaResolver.schemaJarUrl = "wrong";
        schemaResolver.schemaPath = "wrong";

        schemaResolver.getSchemaAsStream();
    }
}