package com.ericsson.cifwk.taf.scheduler.infrastructure.documentation;

import com.google.common.base.Optional;
import com.wordnik.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import java.net.URI;

/*
/ This class is a custom implementation of 'Swagger2Controller' class located in
/ springfox-swagger2-2.0.0jar/springfox.documentation.swagger2/web
*/

@Controller
@ApiIgnore
public class CustomSwagger2Controller {

    // default url changed to include 'api/'
    public static final String DEFAULT_URL = "api/v2/api-docs";

    private static final String DEFAULT_STRING = "DEFAULT";

    @Value("${springfox.documentation.swagger.v2.host:DEFAULT}")
    private String hostNameOverride;

    @Autowired
    private DocumentationCache documentationCache;

    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired
    private JsonSerializer jsonSerializer;

    // DEFAULT_URL added to the request path
    @ApiIgnore
    @RequestMapping(value = {"${springfox.documentation.swagger.v2.path:" + DEFAULT_URL + "}"},
                    method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<Json> getDocumentation(@RequestParam(value = "group", required = false) String swaggerGroup) {
        String groupName = Optional.fromNullable(swaggerGroup).or("default");
        Documentation documentation = this.documentationCache.documentationByGroup(groupName);
        if (documentation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Swagger swagger = this.mapper.mapDocumentation(documentation);
            swagger.host(this.hostName());
            return new ResponseEntity<>(this.jsonSerializer.toJson(swagger), HttpStatus.OK);
        }
    }

    private String hostName() {
        if (DEFAULT_STRING.equals(this.hostNameOverride)) {
            URI uri = ControllerLinkBuilder.linkTo(CustomSwagger2Controller.class).toUri();
            String host = uri.getHost();
            int port = uri.getPort();
            return port > -1 ? String.format("%s:%d", host, port) : host;
        } else {
            return this.hostNameOverride;
        }
    }
}

