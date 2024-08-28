package com.ericsson.oss.axis.types;

public enum ScheduleType {

    KGB("KGB+N"),
    MTE_P("MTE-P"),
    MTE_V("MTE-V"),
    Physical("Physical"),
    Physical_E("Physical-E"),
    Physical_Entry_Loop("Physical-Entry-Loop"),
    Physical_KVM("Physical_KVM"),
    RFA_P("RFA-P"),
    RFA_V("RFA-V"),
    RNCDB("RNCDB"),
    RVB("RVB"),
    STCDB("STCDB"),
    System_Test("System_Test"),
    vCDB_INSTALL("vCDB-INSTALL"),
    Virtual("Virtual"),
    Virtual_Entry_Loop("Virtual-Entry-Loop"),
    Virtual_Install("Virtual-Install"),
    Virtual_Upgrade("Virtual-Upgrade"),
    Virtual1("Virtual1"),
    Virtual2("Virtual2");

    private final String typeId;

    ScheduleType(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public static ScheduleType fromTypeId(String typeId) {
        for (ScheduleType type : values()) {
            if (type.getTypeId().equals(typeId)) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("No ScheduleType with type ID '%s'", typeId));
    }
}