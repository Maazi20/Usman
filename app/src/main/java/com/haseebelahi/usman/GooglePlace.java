package com.haseebelahi.usman;

/**
 * Created by Haseeb Elahi on 5/30/2016.
 */
public class GooglePlace {

    private String desc;
    private String referenceId;

    public GooglePlace(String d, String r) {
        desc = d;
        referenceId = r;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDesc() {
        return desc;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
