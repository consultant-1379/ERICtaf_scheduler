package com.ericsson.cifwk.taf.scheduler.application.constant;

public enum KgbConstant {

    PRODUCT_NAME("KGB"),
    DROP_NAME("KGB"),
    ISO_NAME("KGB_ISO"),
    ISO_VERSION("1.0"),
    TW_ISO_NAME("KGB_TW_ISO"),
    TW_ISO_VERSION("1.0");

    private String value;

    KgbConstant(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
