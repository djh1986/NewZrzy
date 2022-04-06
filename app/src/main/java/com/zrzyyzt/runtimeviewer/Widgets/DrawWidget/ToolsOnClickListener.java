package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool.DrawTool;


/**
 * 绘图点击事件
 * Created by gis-luq on 2016/1/2.
 */
public class ToolsOnClickListener implements View.OnClickListener {
    private static final String TAG = "ToolsOnClickListener";
    private Context context = null;
    private DrawTool drawTool = null;
    private Graphic selectGraphic =null;
    private MapView mapView = null;

    public ToolsOnClickListener(Context context, DrawTool drawTool, Graphic selectGraphic, MapView mapView) {
        this.context = context;
        this.drawTool = drawTool;
        this.selectGraphic = selectGraphic;
        this.mapView = mapView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_view_draw_point://绘制点
                drawTool.activate(DrawTool.POINT);
                v.setBackgroundColor(Color.LTGRAY);
                break;
            case R.id.widget_view_draw_polyline://绘制线
                drawTool.activate(DrawTool.POLYLINE);
                v.setBackgroundColor(Color.LTGRAY);
                break;
            case R.id.widget_view_draw_freepolyline://绘制流状线
                drawTool.activate(DrawTool.FREEHAND_POLYLINE);
                break;
            case R.id.widget_view_draw_polygon://绘制面
                drawTool.activate(DrawTool.POLYGON);
                break;
            case R.id.widget_view_draw_freepolygon://绘制流状面
                drawTool.activate(DrawTool.FREEHAND_POLYGON);
                break;
//            case R.id.widget_view_draw_circle://绘制圆
//                drawTool.activate(DrawTool.CIRCLE);
//                break;
            case R.id.widget_view_draw_envelop://绘制矩形
                drawTool.activate(DrawTool.ENVELOPE);
                break;
        }

    }


}
