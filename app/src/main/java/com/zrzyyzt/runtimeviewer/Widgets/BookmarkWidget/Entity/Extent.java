package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity;

public class Extent {
    private double xmin;
    private double ymin;
    private double xmax;
    private double ymax;
    private SpatialReference spatialReference;

    public Extent(double xmin, double ymin, double xmax, double ymax, SpatialReference spatialReference) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.spatialReference = spatialReference;
    }

    public double getXmin() { return xmin; }
    public void setXmin(double value) { this.xmin = value; }

    public double getYmin() { return ymin; }
    public void setYmin(double value) { this.ymin = value; }

    public double getXmax() { return xmax; }
    public void setXmax(double value) { this.xmax = value; }

    public double getYmax() { return ymax; }
    public void setYmax(double value) { this.ymax = value; }

    public SpatialReference getSpatialReference() { return spatialReference; }
    public void setSpatialReference(SpatialReference value) { this.spatialReference = value; }
}
