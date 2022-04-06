package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity;

public class SpatialReference {
    private int wkid;

    public SpatialReference(int wkid) {
        this.wkid = wkid;
    }

    public int getWkid() { return wkid; }
    public void setWkid(int value) { this.wkid = value; }
}
