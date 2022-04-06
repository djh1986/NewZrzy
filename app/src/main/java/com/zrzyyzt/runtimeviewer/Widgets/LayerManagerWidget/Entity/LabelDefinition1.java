package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.TextSymbol;

public class LabelDefinition1 {
    private LabelExpressionInfo labelExpressionInfo;
    private String labelPlacement;
    private Color color;
    private int minScale;
    private int maxScale;
    private String where;
    private TextSymbol symbol;
    public void setLabelExpressionInfo(LabelExpressionInfo labelExpressionInfo) {
        this.labelExpressionInfo = labelExpressionInfo;
    }
    public LabelExpressionInfo getLabelExpressionInfo() {
        return labelExpressionInfo;
    }

    public void setLabelPlacement(String labelPlacement) {
        this.labelPlacement = labelPlacement;
    }
    public String getLabelPlacement() {
        return labelPlacement;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
    }
    public int getMinScale() {
        return minScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }
    public int getMaxScale() {
        return maxScale;
    }

    public void setWhere(String where) {
        this.where = where;
    }
    public String getWhere() {
        return where;
    }

    public void setSymbol(TextSymbol symbol) {
        this.symbol = symbol;
    }
    public TextSymbol getSymbol() {
        return symbol;
    }
}
