package com.ericsson.cifwk.taf.scheduler.application.schedules.validation;

import com.ericsson.cifwk.taf.scheduler.model.Gav;
import com.ericsson.cifwk.taf.scheduler.model.Testware;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.ericsson.cifwk.taf.scheduler.application.schedules.validation.ScheduleValidationUtils.findTestwareByGav;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         15/10/2015
 */

@RunWith(Parameterized.class)
public class ScheduleValidationUtilsTest {

    private String gav;
    private Boolean expectedResult;
    private List<Testware> testwareList;

    public ScheduleValidationUtilsTest(String gav, Boolean expectedResult) {
        this.gav = gav;
        this.expectedResult = expectedResult;
    }

    @Before
    public void setUp() throws Exception {
        Gav gav = new Gav();
        gav.setArtifactId("a1");
        gav.setGroupId("g1");
        gav.setVersion("1.0.1");

        testwareList = new LinkedList<>();
        testwareList.add(new Testware(gav, "CXP12345", null));
    }


    @Test
    public void testFindTestwareByGav() throws Exception {
        assertThat(findTestwareByGav(testwareList, gav).isPresent(), is(expectedResult));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"g1:a1:1.0.1", true},
                {"g1:a1", true},
                {"g1:a1:LATEST", true},
                {"g1:a1:RELEASE", true},
                {"g1:a1:2.0", false}
        });
    }

}