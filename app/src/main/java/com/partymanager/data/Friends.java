package com.partymanager.data;

import android.graphics.Bitmap;

public class Friends {

    String code = null;
    String name = null;
    boolean selected = false;
    boolean appInstalled;
    Bitmap foto = null;

    public Friends(String code, String name, boolean selected, boolean appI) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
        this.appInstalled = appI;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Boolean getAppInstalled() {
        return appInstalled;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFoto(Bitmap fotot) {
        this.foto = fotot;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}

