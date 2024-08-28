package com.ericsson.cifwk.taf.scheduler.application.constant;


import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class IsoDetailsTest {

    public static final String ENM = "ENM";

    public static final String NON_EXISTENT_PRODUCT = "NME";

    @Test
    public void testGetIsoNameByProduct() {
        String isoName = IsoDetails.getIsoNameByProduct(ENM);
        assertThat(isoName, equalTo("ERICenm_CXP9027091"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIsoNameByNonExistentProduct() {
        IsoDetails.getIsoNameByProduct(NON_EXISTENT_PRODUCT);
    }

    @Test
    public void testGetTestwareIsoNameByProduct() {
        String isoName = IsoDetails.getTestwareIsoNameByProduct(ENM);
        assertThat(isoName, equalTo("ERICenmtestware_CXP9027746"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTestwareIsoNameByNonExistentProduct() {
        IsoDetails.getTestwareIsoNameByProduct(NON_EXISTENT_PRODUCT);
    }

}
