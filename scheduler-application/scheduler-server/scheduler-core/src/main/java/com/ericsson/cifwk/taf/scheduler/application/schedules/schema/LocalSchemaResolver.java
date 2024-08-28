package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 22/07/2015
 */

@Component
@Profile({"dev", "itest", "test"})
public class LocalSchemaResolver implements SchemaResolver {
    @Override
    public InputStream getSchemaAsStream() {
        return getClass().getClassLoader().getResourceAsStream("schema/schedule.xsd");
    }
}
