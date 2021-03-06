package com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Listener.PeripheralQueryListener;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Adapter.PoiResultAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Entity.PoiBean;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Thread.GetPoiThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import gisluq.lib.Util.ToastUtils;

public class PeripheralQueryWidget1 extends BaseWidget {
    public  final static String TAG="PeripheralQueryWidget1";
    public View mWidgetView = null;//
    //private MapView mMapView;
    private PeripheralQueryListener myListener;
    private DefaultMapViewOnTouchListener defaultMapViewOnTouchListener;
    private String  radius="500";
    private String keyWords;
    private EditText txtPoi;
    /**
     * ???????????????????????????????????????
     * ?????????widget?????????, WidgetManager?????????????????????????????????????????????????????????.
     * ???????????????????????? "inactive" ??????
     */
    @Override
    public void active() {
        super.active();//?????????????????????????????????????????????widget?????????widget??????????????????inactive()???????????????
        super.showWidget(mWidgetView);//??????UI?????????
        //super.showMessageBox(super.name);
        defaultMapViewOnTouchListener=new DefaultMapViewOnTouchListener(this.mapView.getContext(),this.mapView);
    }

    /**
     * widget???????????????????????????????????????view??????????????????
     * ?????????????????????????????????????????????
     */
    @Override
    public void create() {

        //mMapView=super.mapView;

        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_peripheralquerywidget,null);
        txtPoi=mWidgetView.findViewById(R.id.widget_view_peripheralquery_txtPoi);
        final EditText queryRadius=mWidgetView.findViewById(R.id.widget_view_peripheralquery_queryRadius);
        final Button btnQuery=mWidgetView.findViewById(R.id.widget_view_peripheralquery_btnQuery);
        final Button poiType1=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype1);
        final Button poiType2=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype2);
        final Button poiType3=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype3);
        final Button poiType4=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype4);
        final Button poiType5=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype5);
        final Button poiType6=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype6);
        final Button poiType7=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype7);
        final Button poiType8=mWidgetView.findViewById(R.id.widget_view_peripheralquery_poitype8);
        final ListView queryList=mWidgetView.findViewById(R.id.widget_view_peripheralquery_queryList);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                keyWords = txtPoi.getText().toString();
                if (keyWords == null || keyWords == "") {
                    ToastUtils.showLong(context, "???????????????????????????");
                    return;
                }
                radius = queryRadius.getText().toString();

                if (radius == null) {
                    radius = "500";
                } else {
                    Boolean resultbool = isNumberFunction(radius);
                    if (!resultbool) {
                        ToastUtils.showLong(context, "?????????????????????????????????????????????");
                        return;
                    }
                }
                myListener=new PeripheralQueryListener(context,mapView,queryList,keyWords,radius);
                ToastUtils.showLong(context, "???????????????????????????????????????");
                mapView.setOnTouchListener(myListener);
            }

        });


        poiType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType1.getText());
            }
        });

        poiType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType2.getText());
            }
        });

        poiType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType3.getText());
            }
        });

        poiType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType4.getText());
            }
        });

        poiType5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType5.getText());
            }
        });

        poiType6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType6.getText());
            }
        });

        poiType7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType7.getText());
            }
        });

        poiType8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPoi.setText(poiType8.getText());
            }
        });
    }
    public boolean isNumberFunction(String string) {
        boolean result = false;
        Pattern pattern = Pattern.compile("^[-+]?[0-9]");
        if(pattern.matcher(string).matches()){
            //??????
            result = true;
        } else {
            //?????????
        }
        //????????????
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('^');
        stringBuilder.append('[');
        stringBuilder.append("-+");
        stringBuilder.append("]?[");
        stringBuilder.append("0-9]+(");
        stringBuilder.append('\\');
        stringBuilder.append(".[0-9");
        stringBuilder.append("]+)");
        stringBuilder.append("?$");
        Pattern pattern1 = Pattern.compile(stringBuilder.toString());
        if(pattern1.matcher(string).matches()){
            //??????
            result = true;
        } else {
            //?????????
        }
        return  result;
    }

    /**
     * ???????????????????????????????????????
     * ???????????????????????? "inactive" ??????
     */
    @Override
    public void inactive(){
        super.inactive();
        txtPoi.setText("");

        if(myListener!=null){
            super.mapView.setOnTouchListener(defaultMapViewOnTouchListener);
            myListener.clear();
        }
    }
}
