package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.Common.Variable;
import com.zrzyyzt.runtimeviewer.R;

import gisluq.lib.Util.ToastUtils;

public class DrawWidget extends BaseWidget {

    private static final String TAG = "DrawWidget";
    public View drawView ;
    private  DrawClickListener drawClickListener;
    private Variable.DrawType drawType;
    private DefaultMapViewOnTouchListener mapListener;
    private MapDraw mapDraw;

    @Override
    public void active() {
        super.active();
        super.showWidget(drawView);
        init();
    }

    @Override
    public void create() {
        context = super.context;
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        drawView = mLayoutInflater.inflate(R.layout.widget_view_draw,null);

        initView();
    }

    public  void init(){

        mapDraw = new MapDraw(context, mapView);

        DefaultMapViewOnTouchListener listener = new DefaultMapViewOnTouchListener(context, mapView){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(drawType == Variable.DrawType.POINT){
                    Log.d(TAG, "onSingleTapUp: point");
                    mapDraw.startDrawPoint(e.getX(),e.getY());
                }else if(drawType == Variable.DrawType.LINE){
                    Log.d(TAG, "onSingleTapUp: line");
                    mapDraw.startDrawLine(e.getX(), e.getY());
                }else if(drawType == Variable.DrawType.POLYGON){
                    Log.d(TAG, "onSingleTapUp: polygon");
                    mapDraw.startDrawPolygon(e.getX(), e.getY());
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }

            @Override
            public boolean onRotate(MotionEvent event, double rotationAngle) {
                return super.onRotate(event, rotationAngle);
            }
        };

        mapView.setOnTouchListener(listener);
    }
    private void initView() {
        View btnDrawPoint = drawView.findViewById(R.id.widget_view_draw_point);
        View btnDrawPolyline = drawView.findViewById(R.id.widget_view_draw_polyline);
        View btnDrawPolygon = drawView.findViewById(R.id.widget_view_draw_polygon);
        View btnDrawClear = drawView.findViewById(R.id.widget_view_draw_clear);

        btnDrawPoint.setOnClickListener(listener);

        btnDrawPolyline.setOnClickListener(listener);

        btnDrawPolygon.setOnClickListener(listener);

        btnDrawClear.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.widget_view_draw_point) {
                ToastUtils.showShort(context, "绘制点，点击屏幕开始绘制点");
                drawType = Variable.DrawType.POINT;
                mapDraw.endMeasure();
                if (drawClickListener != null) {
                    drawClickListener.pointClick();
                }
            } else if (id == R.id.widget_view_draw_polyline) {
                ToastUtils.showShort(context, "绘制线，点击屏幕开始绘制线");
                drawType = Variable.DrawType.LINE;
                mapDraw.endMeasure();
                if (drawClickListener != null) {
                    drawClickListener.polylineClick();
                }
            } else if (id == R.id.widget_view_draw_polygon) {
                drawType = Variable.DrawType.POLYGON;
                mapDraw.endMeasure();
                if (drawClickListener != null) {
                    drawClickListener.polygonClick();
                }
            }else if(id == R.id.widget_view_draw_clear){
                mapDraw.endMeasure();
                mapDraw.clearMeasure();;
            }
        }
    };

    @Override
    public void inactive() {
        super.inactive();

    }


}
