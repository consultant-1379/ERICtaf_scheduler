package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

@Service
public class SchemaService {

    @Autowired
    private SchemaResolver schemaResolver;

    private static final String SCHEMA_ENCODING = "UTF-8";

    @HystrixCommand(
            commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000"),
            threadPoolProperties = @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "60000"))
    public Schema getScheduleSchema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream schemaStream = schemaResolver.getSchemaAsStream()) {
            return schemaFactory.newSchema(new StreamSource(schemaStream));
        } catch (IOException | SAXException e) {
            throw new RuntimeException("Failed to parse Schedule XML schema", e);
        }
    }

    public String getScheduleSchemaAsString() throws IOException {
        InputStream stream = schemaResolver.getSchemaAsStream();
        return IOUtils.toString(stream, SCHEMA_ENCODING);
    }


}
