package com.mobile.mobileplayerdemo.model.bean;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class
VideoPathBean implements Serializable {
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj){
            return true;
        }
        if(obj==null){
            return false;
        }
        if(getClass()!=obj.getClass()){
            return false;
        }
        VideoPathBean videoPathBean= (VideoPathBean) obj;
        if(path!=videoPathBean.getPath()){
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        return 1;
    }
}
