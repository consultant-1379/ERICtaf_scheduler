package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import java.io.InputStream;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 22/07/2015
 */

public interface SchemaResolver {

    InputStream getSchemaAsStream();

}
