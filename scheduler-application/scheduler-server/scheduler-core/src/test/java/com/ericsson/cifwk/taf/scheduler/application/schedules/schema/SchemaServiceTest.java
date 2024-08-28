package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 23/07/2015
 */

@RunWith(MockitoJUnitRunner.class)
public class SchemaServiceTest {

    @InjectMocks
    private SchemaService schemaService;

    @Mock
    SchemaResolver schemaResolver;

    @Before
    public void setUp() {
        when(schemaResolver.getSchemaAsStream())
                .thenReturn(getClass().getClassLoader().getResourceAsStream("schedules/schema/schedule.xsd"));
    }

    @Test
    public void testGetScheduleSchema() {
        Schema schema = schemaService.getScheduleSchema();
        assertNotNull(schema);
    }

    @Test(expected = RuntimeException.class)
    public void testGetScheduleSchema_shouldThrowExceptionWhenSchemaIsInvalid() {
        when(schemaResolver.getSchemaAsStream()).thenReturn(new ByteArrayInputStream("InvalidSchema".getBytes()));
        schemaService.getScheduleSchema();
    }

    @Test
    public void testGetScheduleSchemaAsString() throws IOException {
        String schema = schemaService.getScheduleSchemaAsString();
        assertThat(schema, containsString("XMLSchema"));
    }

    @Test(expected = IOException.class)
    public void testGetScheduleSchemaAsString_shouldThrowExceptionWhenInputStreamIsInvalid() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("schedules/schema/schedule.xsd");
        is.close();
        when(schemaResolver.getSchemaAsStream()).thenReturn(is);
        schemaService.getScheduleSchemaAsString();
    }
}
