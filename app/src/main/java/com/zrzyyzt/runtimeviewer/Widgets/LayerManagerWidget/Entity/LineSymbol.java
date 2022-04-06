package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity;

import android.graphics.Color;

public class LineSymbol {
    private String linestyle;
    private Color color;
    private int width;

    public void setLinestyle(String linestyle) {
        this.linestyle = linestyle;
    }
    public String getLinestyle() {
        return linestyle;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getWidth() {
        return width;
    }
}
