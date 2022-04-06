package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool.DrawEvent;
import com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool.DrawEventListener;
import com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool.DrawTool;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DrawWidget1 extends BaseWidget implements DrawEventListener {

    private static final String TAG = "DrawWidget1";
    public View drawView ;
    private DrawTool drawTool;
    private GraphicsOverlay graphicsLayer = null;
    public MapView.OnTouchListener mapDefaultOnTouchListener;//默认点击事件
    public DrawEventListener drawEventListener;//要素绘制点击事件

    private Graphic selectGraphic = null;


    private  View btnDrawPoint = null;
    private  View btnDrawPolyline = null;
    private  View btnDrawPolygon = null;
//    private  View btnDrawCircle  = null;
    private  View btnDrawEnvelop  = null;
    private  View btnDrawText  = null;
    private  View btnDrawFreePolyline  = null;
    private  View btnDrawFreePolygon = null;
    private View btnDrawPicture = null;

    private TextView textDescription = null;

    List<View> viewList = null;

    private View drawTextSubView;
    List<View> subViewList = null;

    private Resources resources;
    @Override
    public void active() {
        super.active();
        super.showWidget(drawView);
        if(graphicsLayer == null){
            graphicsLayer = new GraphicsOverlay();
        }
        if(!mapView.getGraphicsOverlays().contains(graphicsLayer)){
            mapView.getGraphicsOverlays().add(graphicsLayer);
        }
    }

    @Override
    public void create() {

        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        drawView = mLayoutInflater.inflate(R.layout.widget_view_draw,null);
        initView();
    }

    private void initView() {
        resources = context.getResources();
        btnDrawPoint = drawView.findViewById(R.id.widget_view_draw_point);
        btnDrawPolyline = drawView.findViewById(R.id.widget_view_draw_polyline);
        btnDrawPolygon = drawView.findViewById(R.id.widget_view_draw_polygon);
//        btnDrawCircle = drawView.findViewById(R.id.widget_view_draw_circle);
        btnDrawEnvelop = drawView.findViewById(R.id.widget_view_draw_envelop);
        btnDrawText = drawView.findViewById(R.id.widget_view_draw_text);
        btnDrawFreePolyline = drawView.findViewById(R.id.widget_view_draw_freepolyline);
        btnDrawFreePolygon = drawView.findViewById(R.id.widget_view_draw_freepolygon);
        btnDrawPicture = drawView.findViewById(R.id.widget_view_draw_picture);

        viewList =new ArrayList<>();
        viewList.add(btnDrawPoint);
        viewList.add(btnDrawPolyline);
        viewList.add(btnDrawPolygon);
//        viewList.add(btnDrawCircle);
        viewList.add(btnDrawEnvelop);
        viewList.add(btnDrawText);
        viewList.add(btnDrawFreePolyline);
        viewList.add(btnDrawFreePolygon);
        viewList.add(btnDrawPicture);

        textDescription = drawView.findViewById(R.id.widget_view_draw_description);

        drawTextSubView = drawView.findViewById(R.id.widget_view_draw_text_groupview);
        subViewList = new ArrayList<>();
        subViewList.add(drawTextSubView);

        View btnDrawClear = drawView.findViewById(R.id.widget_view_draw_clear);

        //文本标注内容设置
        EditText txtLabelContent = drawView.findViewById(R.id.widget_view_draw_text_content);
        txtLabelContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                drawTool.setTextSymbolText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置标注文字大小
        Spinner spinnerFontsize = drawView.findViewById(R.id.widget_view_draw_text_size);
        spinnerFontsize.setSelection(5,true);
        spinnerFontsize.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] fontsize = resources.getStringArray(R.array.fontsizearr);
                drawTool.setTextSymbolSize(Float.parseFloat(fontsize[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        this.drawTool = new DrawTool(context, mapView);
        this.drawTool.addEventListener(this);

        //设置地图事件
        mapDefaultOnTouchListener = new DefaultMapViewOnTouchListener(mapView.getContext(), mapView);
        drawEventListener = this;

        btnDrawClear.setOnClickListener(toolsOnClickListener);

        btnDrawPoint.setOnClickListener(toolsOnClickListener);
        btnDrawPolyline.setOnClickListener(toolsOnClickListener);
        btnDrawPolygon.setOnClickListener(toolsOnClickListener);

//        btnDrawCircle.setOnClickListener(toolsOnClickListener);
        btnDrawEnvelop.setOnClickListener(toolsOnClickListener);
        btnDrawText.setOnClickListener(toolsOnClickListener);
        btnDrawFreePolyline.setOnClickListener(toolsOnClickListener);
        btnDrawFreePolygon.setOnClickListener(toolsOnClickListener);
        btnDrawPicture.setOnClickListener(toolsOnClickListener);

        btnDrawClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicsLayer.getGraphics().clear();
            }
        });
    }

    private View.OnClickListener toolsOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            setBackgroundColor(viewList,v);
            switch (v.getId()) {
                case R.id.widget_view_draw_point://绘制点
                    drawTool.activate(DrawTool.POINT);
                    break;
                case R.id.widget_view_draw_polyline://绘制线
                    drawTool.activate(DrawTool.POLYLINE);
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
//                case R.id.widget_view_draw_circle://绘制圆
//                    drawTool.activate(DrawTool.CIRCLE);
//                    break;
                case R.id.widget_view_draw_envelop://绘制矩形
                    drawTool.activate(DrawTool.ENVELOPE);
                    break;
                case R.id.widget_view_draw_text:
                    drawTool.activate(DrawTool.TEXT);
                    break;
                case R.id.widget_view_draw_picture:
                    drawTool.activate(DrawTool.PICTURE);
                    break;
            }
        }
    };

    private void setBackgroundColor(List<View> viewList, View v) {
        for (View view:viewList
             ) {
            if(view.equals(v)){
                view.setBackgroundColor(Color.LTGRAY);
            }else{
                view.setBackgroundColor(Color.WHITE);
            }
        }
        if(v.getId() == R.id.widget_view_draw_text){
            drawTextSubView.setVisibility(View.VISIBLE);
           textDescription.setText(R.string.draw_text_description);

        }else{
            drawTextSubView.setVisibility(View.GONE);
            textDescription.setText(R.string.draw_description);
        }
    }

    @Override
    public void inactive() {
        super.inactive();
        if(graphicsLayer!=null){
            graphicsLayer.getGraphics().clear();
            mapView.getGraphicsOverlays().remove(graphicsLayer);
        }

//        drawTool = null;
    }

    @Override
    public void handleDrawEvent(DrawEvent event) throws FileNotFoundException {
        // 将画好的图形（已经实例化了Graphic），添加到drawLayer中并刷新显示
        this.graphicsLayer.getGraphics().add(event.getDrawGraphic());

        // 修改点击事件为默认
        this.mapView.setOnTouchListener(mapDefaultOnTouchListener);
    }
}
