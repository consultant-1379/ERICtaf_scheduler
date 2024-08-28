package com.ericsson.cifwk.taf.scheduler.db;

import org.flywaydb.core.Flyway;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.annotation.Annotation;

public class FlywayTestExecutionListener implements TestExecutionListener {
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {

    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final Annotation annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(), FlywayTest.class);
        process(testContext, (FlywayTest) annotation);
    }

    private void process(TestContext testContext, FlywayTest annotation) {
        Flyway flyway = getFlyway(testContext);

        String[] originalLocations = flyway.getLocations();
        flyway.setLocations(annotation.locations());

        if (annotation.invokeClean()) {
            flyway.clean();
        }
        flyway.migrate();

        flyway.setLocations(originalLocations);
    }

    private void clean(TestContext testContext, FlywayTest annotation) {
        Flyway flyway = getFlyway(testContext);

        if (annotation.invokeClean()) {
            flyway.clean();
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        final Annotation annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(), FlywayTest.class);
        clean(testContext, (FlywayTest) annotation);
    }

    private Flyway getFlyway(TestContext testContext) {
        return testContext.getApplicationContext().getBean(Flyway.class);
    }
}
