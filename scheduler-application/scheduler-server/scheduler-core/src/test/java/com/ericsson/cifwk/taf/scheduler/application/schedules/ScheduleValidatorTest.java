package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidator;
import com.ericsson.cifwk.taf.scheduler.api.dto.ErrorRange;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleErrorInfo;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleValidatorTest {

    private ScheduleValidator validator;
    private static Schema schema;

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleValidatorTest.class);

    @BeforeClass
    public static void setUpSchema() throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = schemaFactory.newSchema(new StreamSource(new StringReader(
                getResource("schedules/schema/schedule.xsd"))));
    }

    @Before
    public void setUp() throws Exception {
        validator = new ScheduleValidator(schema);
    }

    @Test
    public void testValid() throws Exception {
        assertTrue(isValid("schedule-valid"));
    }

    @Test
    public void testValidWithLatestVersion() throws Exception {
        assertTrue(isValid("schedule-valid-with-latest"));
    }

    @Test
    public void testNoNamespace() throws Exception {
        assertTrue(isValid("schedule-valid-without-namespace"));
    }

    @Test
    public void testUndeclaredElement() throws Exception {
        List<ScheduleErrorInfo> errors = validate("schedule-with-undeclared-element");
        assertThat(errors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(7, 21),
                        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'undeclared'."
                                + " One of '{\"http://taf.lmera.ericsson.se/schema/te\":suites}' is expected.")));
    }


    @Test
    public void testFindDuplicateItems() throws Exception {
        List<ScheduleErrorInfo> errors = validate("schedule-with-duplicates");
        assertThat(errors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(10, 31),
                        "Duplicate unique value [duplicate] declared for identity constraint of element \"schedule\".")));
    }

    @Test
    public void testFindDeepDuplicateItems() throws Exception {
        List<ScheduleErrorInfo> errors = validate("schedule-with-deep-duplicates");
        assertThat(errors, hasItem(
                new ScheduleErrorInfo(new ErrorRange(11, 35),
                        "Duplicate unique value [duplicate] declared for identity constraint of element \"schedule\".")));
    }

    @Test
    public void testWrongGav() throws Exception {
        List<ScheduleErrorInfo> errors = validate("schedule-with-wrong-gav");
        assertThat(errors, hasItems(
                new ScheduleErrorInfo(new ErrorRange(11, 1, 11, 50),
                        "cvc-type.3.1.3: The value 'com.ericsson.cifwk' of element 'component' is not valid."),
                new ScheduleErrorInfo(new ErrorRange(6, 1, 6, 42),
                        "cvc-type.3.1.3: The value 'g.r:a1:err' of element 'component' is not valid.")));
    }

    @Test
    public void testEmpty() throws Exception {
        List<ScheduleErrorInfo> errors = validate("schedule-empty");
        assertThat(errors, hasItem(
                new ScheduleErrorInfo(new ErrorRange(), "Premature end of file.")));
    }

    private List<ScheduleErrorInfo> validate(String resourceName) throws IOException {
        String schedule = getResource("schedules/basic/" + resourceName + ".xml");
        List<ScheduleErrorInfo> errors = validator.validate(schedule);
        LOG.info("Errors: : {}", errors);
        return errors;
    }

    private boolean isValid(String name) throws IOException {
        return validate(name).isEmpty();
    }

    private static String getResource(String resourceName) throws IOException {
        return IOUtils.toString(ScheduleValidatorTest.class.getClassLoader().getResourceAsStream(resourceName));
    }
}
