package com.ericsson.cifwk.taf.scheduler.constant;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 26/11/2015
 */
public class BuildTypeTest {

    @Test
    public void testGetTypeById() {
        String typeById = BuildType.getTypeById(1);
        assertThat(typeById, is(BuildType.KGB.getType()));
    }

    @Test
    public void testGetIdByType() {
        int id = BuildType.getIdByType("MTE-P");
        assertThat(id, is(BuildType.TYPE2.getId()));
    }

    @Test
    public void testGetById() {
        BuildType type = BuildType.getById(9);
        assertThat(type, is(BuildType.TYPE9));
    }
}
