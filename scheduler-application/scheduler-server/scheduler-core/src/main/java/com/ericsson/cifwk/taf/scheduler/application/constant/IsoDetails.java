package com.ericsson.cifwk.taf.scheduler.application.constant;

import java.util.Arrays;

public enum IsoDetails {
    ENM("ENM", "ERICenm_CXP9027091", "ERICenmtestware_CXP9027746"),
    OSS("OSS", "ERICOSS-RC_CXP9027260", "");

    private final String productName;
    private final String productIsoName;
    private final String testwareIsoName;

    IsoDetails(String productName, String productIsoName, String testwareIsoName) {
        this.productName = productName;
        this.productIsoName = productIsoName;
        this.testwareIsoName = testwareIsoName;
    }

    public static String getIsoNameByProduct(String product) {
        return Arrays.stream(IsoDetails.values())
                .filter(i -> i.productName.equalsIgnoreCase(product))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .productIsoName;
    }

    public static String getTestwareIsoNameByProduct(String product) {
        return Arrays.stream(IsoDetails.values())
                .filter(i -> i.productName.equalsIgnoreCase(product))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .testwareIsoName;
    }
}
