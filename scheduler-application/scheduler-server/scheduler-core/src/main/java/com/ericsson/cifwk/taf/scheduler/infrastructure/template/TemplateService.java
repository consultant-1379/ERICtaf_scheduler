package com.ericsson.cifwk.taf.scheduler.infrastructure.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

@Service
public class TemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);

    public static final String NEW_ASSIGNMENT = "new_assignment.ftl";
    private static final String ENCODING = "UTF-8";
    private Configuration cfg;

    @PostConstruct
    void configure() {
        cfg = new Configuration();

        cfg.setClassForTemplateLoading(TemplateService.class, "/templates");
        cfg.setDefaultEncoding(ENCODING);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLocalizedLookup(false);
    }

    public String generateFromTemplate(String templateName, Map<String, Object> variables) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Template template = cfg.getTemplate(templateName);
            template.process(variables, new OutputStreamWriter(out, ENCODING));

            return out.toString(ENCODING);
        } catch (IOException | TemplateException e) {
            LOG.error("Exception while processing template " + templateName, e);
        }

        return null;
    }
}
