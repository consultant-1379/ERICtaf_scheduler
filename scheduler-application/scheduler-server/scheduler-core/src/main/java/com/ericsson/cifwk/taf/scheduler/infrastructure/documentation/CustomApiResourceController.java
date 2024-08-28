package com.ericsson.cifwk.taf.scheduler.infrastructure.documentation;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/*
/ This class is a custom implementation of 'ApiResourceController' class located in
/ springfox-swagger-common-2.0.0jar/springfox.documentation.swagger/web
*/

@Controller
@ApiIgnore
public class CustomApiResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomApiResourceController.class);

    @Value("${springfox.documentation.swagger.v2.path:/v2/api-docs}")
    private String swagger2Url;
    @Autowired
    private DocumentationCache documentationCache;

    // url changed to include 'api/' in the request path
    @RequestMapping({"api/swagger-resources"})
    @ResponseBody
    ResponseEntity<List<SwaggerResource>> swaggerResources() {
        // swagger2Controller points to CustomSwagger2Controller (instead of standard Swagger2Controller)
        Class swagger2Controller = this.classOrNull("com.ericsson.cifwk.taf.scheduler.infrastructure.documentation.CustomSwagger2Controller");
        List<SwaggerResource> resources = Lists.newArrayList();

        for (Object o : this.documentationCache.all().entrySet()) {
            Entry entry = (Entry) o;
            String swaggerGroup = (String) entry.getKey();
            SwaggerResource swaggerResource;

            if (swagger2Controller != null) {
                swaggerResource = this.resource(swaggerGroup, this.swagger2Url);
                swaggerResource.setSwaggerVersion("2.0");
                resources.add(swaggerResource);
            }
        }

        Collections.sort(resources);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    private SwaggerResource resource(String swaggerGroup, String baseUrl) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(swaggerGroup);
        swaggerResource.setLocation(this.swaggerLocation(baseUrl, swaggerGroup));
        return swaggerResource;
    }

    private String swaggerLocation(String swaggerUrl, String swaggerGroup) {
        String base = Optional.of(swaggerUrl).get();
        return base + "?group=" + swaggerGroup;
    }

    private Class classOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not find the class " + className, e);
            return null;
        }
    }
}

