package com.ericsson.cifwk.taf.scheduler.application.schedules.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * Created by Vladimirs Iljins vladimirs.iljins@ericsson.com
 * 22/07/2015
 */

@Component
@Profile({"prod"})
public class RemoteSchemaResolver implements SchemaResolver {

    @Value("${schedule.schema.jar.url}")
    String schemaJarUrl;

    @Value("${schedule.schema.path}")
    String schemaPath;

    @Override
    public InputStream getSchemaAsStream() {
        try {
            URL jarUrl = new URL("jar:" + schemaJarUrl + "!/");
            JarURLConnection connection = (JarURLConnection) jarUrl.openConnection();
            JarFile jarFile = connection.getJarFile();
            return jarFile.getInputStream(jarFile.getEntry(schemaPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve Schedule XML schema", e);
        }
    }
}
