package com.ericsson.cifwk.taf.scheduler.constant;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum BuildType {
    KGB(1, "KGB+N", true),
    TYPE2(2, "MTE-P", true),
    TYPE3(3, "MTE-V", true),
    TYPE4(4, "Physical", false),
    TYPE5(5, "Physical-E", false),
    TYPE6(6, "Physical-Entry-Loop", true),
    TYPE7(7, "Physical_KVM", true),
    TYPE8(8, "RFA-P", true),
    TYPE9(9, "RFA-V", true),
    TYPE10(10, "RNCDB", false),
    TYPE11(11, "RVB", false),
    TYPE12(12, "STCDB", false),
    TYPE13(13, "System_Test", false),
    TYPE14(14, "vCDB-INSTALL", false),
    TYPE15(15, "Virtual", false),
    TYPE16(16, "Virtual-Entry-Loop", false),
    TYPE17(17, "Virtual-Install", false),
    TYPE18(18, "Virtual-Upgrade", false),
    TYPE19(19, "Virtual1", false),
    TYPE20(20, "Virtual2", false);

    private final int id;
    private final String type;
    private boolean restricted;

    BuildType(final int id, final String type, boolean restricted) {
        this.id = id;
        this.type = type;
        this.restricted = restricted;
    }

    public static Map<Integer, String> getTypes() {
        Map<Integer, String> buildTypes = Maps.newLinkedHashMap();
        for (BuildType buildType : BuildType.values()) {
            buildTypes.put(buildType.id, buildType.type);
        }
        return buildTypes;
    }

    public static String getTypeById(int id) {
        return getTypes().get(id);
    }

    public static int getIdByType(String type) {
        return Arrays.stream(BuildType.values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findAny()
                .get().getId();
    }

    public static BuildType getById(int id) {
        return Arrays.stream(BuildType.values())
                .filter(t -> t.getId() == id)
                .findAny().get();
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isRestricted() {
        return restricted;
    }
}
