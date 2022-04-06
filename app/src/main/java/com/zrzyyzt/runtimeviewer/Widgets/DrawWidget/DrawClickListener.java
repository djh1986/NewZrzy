package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget;

public interface DrawClickListener {
    void prevClick(boolean hasPrev);
    void nextClick(boolean hasNext);
    void pointClick();
    void polylineClick();
    void polygonClick();
}
