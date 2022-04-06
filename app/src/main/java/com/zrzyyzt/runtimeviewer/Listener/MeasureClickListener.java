package com.zrzyyzt.runtimeviewer.Listener;


import com.zrzyyzt.runtimeviewer.Entity.DrawEntity;

public interface MeasureClickListener {
    void prevClick(boolean hasPrev);
    void nextClick(boolean hasNext);
    void lengthClick();
    void areaClick();
    void clearClick(DrawEntity draw);
    void endClick(DrawEntity draw);
}
