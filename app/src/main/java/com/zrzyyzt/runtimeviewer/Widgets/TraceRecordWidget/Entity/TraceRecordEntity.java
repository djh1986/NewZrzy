package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Entity;

import com.esri.arcgisruntime.geometry.Point;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.Extent;

public class TraceRecordEntity {
    private String name;
    private String createTime;
    private String coords;
    private Point startPoint;
    private Point endPoint;
    private Extent extent;


    public TraceRecordEntity(String name, String createTime, String coords) {
        this.name = name;
        this.createTime = createTime;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }
}
