package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget;

import android.content.Context;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.Common.Draw;
import com.zrzyyzt.runtimeviewer.Common.Variable;

import java.util.ArrayList;
import java.util.List;

public class MapDraw extends Draw {

    private Context context;

    private Variable.DrawType drawType=null;

    private Variable.Measure measureLengthType= Variable.Measure.M;
    private Variable.Measure measureAreaType=Variable.Measure.M2;
    private double lineLength=0;
    private List<Double> lengthList;
    private List<Double> tmpLengthList;

    public MapDraw(Context context, MapView mapView) {
        super(context, mapView);

        this.context=context;
        lengthList=new ArrayList<>();
        tmpLengthList=new ArrayList<>();
    }

    public void startDrawLine(float screenX, float screenY){
        if (drawType == null){
            super.startLine();
            drawType = Variable.DrawType.LINE;
        }
//        super.endDraw();
        super.drawByScreenXY(screenX,screenY);
    }

    public void startDrawPolygon(float screenX, float screenY) {
        if (drawType == null){
            super.startPolygon();
            drawType = Variable.DrawType.POLYGON;
        }
//        super.endDraw();
        super.drawByScreenXY(screenX,screenY);

    }

    public void startDrawPoint(float screenX, float screenY) {
        if (drawType == null){
            super.startPoint();
            drawType = Variable.DrawType.POINT;
        }
//        super.endDraw();
        super.drawByScreenXY(screenX,screenY);

    }

    public void endMeasure(){
        drawType=null;
        lineLength=0;
        tmpLengthList.clear();
        lengthList.clear();
    }

    public void clearMeasure(){
        drawType=null;
        lineLength=0;
        tmpLengthList.clear();
        lengthList.clear();
    }
}
