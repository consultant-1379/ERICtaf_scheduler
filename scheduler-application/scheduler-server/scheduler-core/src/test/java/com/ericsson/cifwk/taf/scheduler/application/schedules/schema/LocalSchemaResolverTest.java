package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 23/07/2015
 */
public class LocalSchemaResolverTest {

    LocalSchemaResolver schemaResolver;

    @Before
    public void setUp() throws Exception {
        schemaResolver = new LocalSchemaResolver();
    }

    @Test
    public void testGetSchemaAsStream() throws Exception {
        InputStream stream = schemaResolver.getSchemaAsStream();
        assertNotNull(stream);
    }
}