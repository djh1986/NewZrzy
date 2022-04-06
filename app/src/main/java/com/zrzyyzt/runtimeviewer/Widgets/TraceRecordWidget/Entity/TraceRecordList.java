package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Entity;

import java.util.ArrayList;

public class TraceRecordList {
    private int count;
    private ArrayList<TraceRecordEntity> traceRecordEntityArrayList;

    public TraceRecordList(int count, ArrayList<TraceRecordEntity> traceRecordEntityArrayList) {
        this.count = count;
        this.traceRecordEntityArrayList = traceRecordEntityArrayList;
    }

    public int getCount() {
        return this.traceRecordEntityArrayList.size();
    }

    public ArrayList<TraceRecordEntity> getTraceRecordEntityArrayList() {
        return traceRecordEntityArrayList;
    }

    public void setTraceRecordEntityArrayList(ArrayList<TraceRecordEntity> traceRecordEntityArrayList) {
        this.traceRecordEntityArrayList = traceRecordEntityArrayList;
    }
}
