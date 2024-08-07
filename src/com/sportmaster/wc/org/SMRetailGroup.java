package com.sportmaster.wc.org;

public enum SMRetailGroup {

    FPD("FPD"),
    FTW_PM("PMs_FTW"),
    FTW_HEAD_PM("Head of PMs_FTW"),
    FTW_HEAD_BRAND_MANAGEMENT("Head of Brand Management_FTW"),
    FTW_BRAND_MANAGEMENT("Brand Management_FTW"),
    FTW_GATE_KEEPER("Gatekeeper_FTW"),
    SEPD("SEPD"),
    SEPD_GATE_KEEPER("Gatekeeper_SEPD"),
    SEPD_HEAD_PM("Head of PMs_SEPD"),
    SEPD_BRAND_MANAGEMENT("Brand Management_SEPD"),
    SEPD_HEAD("Heads_SEPD"),
    SEPD_PM("PMs_SEPD"),
    SM_CORE_TEAM("Sportmaster Core Team");

    public final String value;

    SMRetailGroup(String value) {
        this.value = value;
    }

}
