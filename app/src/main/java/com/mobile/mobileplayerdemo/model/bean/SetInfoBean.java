package com.mobile.mobileplayerdemo.model.bean;

import java.io.Serializable;

public class SetInfoBean implements Serializable {
    private String setInfo;
    private int type;

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(String setInfo) {
        this.setInfo = setInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
