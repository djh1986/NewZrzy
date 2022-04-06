package com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget;

import android.content.Intent;
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
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
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

public class PeripheralQueryWidget extends BaseWidget {
    public  final static String TAG="PeripheralQueryWidget";
    public View mWidgetView = null;//
    //private MapView mMapView;
    private GraphicsOverlay mGraphicsOverlay;
    private GraphicsOverlay mGraphicsOverlay1;
    private PictureMarkerSymbol mPinSourceSymbol;
    private PictureMarkerSymbol mPinSourceSymbol1;
    private Callout mCallout;
    private Point locationPoint=null;
    private String  radius="500";
    private String keyWords;
    private EditText txtPoi;
    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {
        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示
        //super.showMessageBox(super.name);

        if(mGraphicsOverlay==null){
            mGraphicsOverlay=new GraphicsOverlay();
        }
        if(!mapView.getGraphicsOverlays().contains(mGraphicsOverlay)){
            mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        }

        if(mGraphicsOverlay1==null){
            mGraphicsOverlay1=new GraphicsOverlay();
        }
        if(!mapView.getGraphicsOverlays().contains(mGraphicsOverlay1)){
            mapView.getGraphicsOverlays().add(mGraphicsOverlay1);
        }
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
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

        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.widget_view_peripheralquery_qz);
        try {
            mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable).get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(MPApplication.getContext(), "没有加载定位图标！", Toast.LENGTH_LONG).show();
        }
        mPinSourceSymbol.setWidth(30f);
        mPinSourceSymbol.setHeight(72f);

        BitmapDrawable pinDrawable1 = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.placenamesearch_pin);
        try {
            mPinSourceSymbol1 = PictureMarkerSymbol.createAsync(pinDrawable1).get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(MPApplication.getContext(), "没有加载定位图标！", Toast.LENGTH_LONG).show();
        }
        mPinSourceSymbol1.setWidth(19f);
        mPinSourceSymbol1.setHeight(72f);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(locationPoint==null){
                   ToastUtils.showLong(context,"请点击屏幕，选择周边查询的位置");
                   return;
               }else {
                   keyWords=txtPoi.getText().toString();
                   if(keyWords==null||keyWords==""){
                       ToastUtils.showLong(context,"没有添加查询关键字");
                       return;
                   }
                   radius=queryRadius.getText().toString();

                   if(radius==null){
                       radius="500";
                   }
                   else {
                       Boolean resultbool= isNumberFunction(radius);
                       if(!resultbool){
                           ToastUtils.showLong(context,"输入的缓冲半径不是有效的数字！");
                           return;
                       }
                   }

                   PoiBean poiBean=getPoi(keyWords,radius);
                   if(poiBean!=null){
                       List<PoiBean.PoisBean> list=poiBean.getPois();
                       PoiResultAdapter poiResultAdapter=new PoiResultAdapter(context,list);
                       queryList.setAdapter(poiResultAdapter);

                   }
                   else {
                       Toast.makeText(MPApplication.getContext(), "没有查询到符合要求的记录！", Toast.LENGTH_LONG).show();
                   }
               }
            }
        });

        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this.mapView.getContext(),this.mapView){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(!txtPoi.getText().toString().equals("")){
                    locationPoint = mapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
                    identifyGraphic(locationPoint);
                }
               return super.onSingleTapUp(e);
            }
        });



        queryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj=queryList.getItemAtPosition(position);
                if(obj!=null){
                    PoiBean.PoisBean poisBean=(PoiBean.PoisBean)obj;
                    String lonlat=poisBean.getLonlat();
                    String [] strLonlat=lonlat.trim().split(" ");
                    if(strLonlat.length<1)return;
                    Map<String,Object>map=attributeMap(poisBean);
                    Double lon=Double.valueOf(strLonlat[0]);
                    Double lat=Double.valueOf(strLonlat[1]);

                    Point addressPoint=new Point(lon,lat);
                    displaySearchResult(addressPoint,map);
                }
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
            //数字
            result = true;
        } else {
            //非数字
        }
        //带小数的
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
            //数字
            result = true;
        } else {
            //非数字
        }
        return  result;
    }
    private void identifyGraphic(Point point) {
        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
        Map<String,Object>map=new HashMap<String,Object>();
        map.put("name","定位点");
        // clear map of existing graphics
        //mapView.getGraphicsOverlays().clear();
        //mGraphicsOverlay.getGraphics().clear();
        // create graphic object for resulting location
        //mGraphicsOverlay=new GraphicsOverlay();

        Point resultPoint = point;
        Graphic resultLocGraphic = new Graphic(resultPoint,map,mPinSourceSymbol);
        // add graphic to location layer
        mGraphicsOverlay1.getGraphics().add(resultLocGraphic);
        // zoom map to result over 3 seconds
        mapView.setViewpointAsync(new Viewpoint(resultPoint.getExtent()), 0.5f);
        // set the graphics overlay to the map
        //mapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }

    private PoiBean getPoi(String keyWord,String radius){

        String address="http://api.tianditu.gov.cn/search?";
        String key="1a2360bdad0e682aba76bfb388fcb849";
        String level="15";
        String mapBound="91.63,32.351,109.66,42.97";
        String count="20";
        String postStr="postStr={\"keyWord\":\""+keyWord+"\",\"level\":\""+level+"\"," +
                "\"mapBound\":\""+mapBound+"\",\"queryType\":\"3\",\"pointLonlat\":\""+locationPoint.getX()+","+locationPoint.getY()+"\",\"queryRadius\":\""+radius+"\",\"count\":\""+count+"\",\"start\":\"0\"}" ;
        String keyType="&type=query&tk="+key;
        String url=address+postStr+keyType;
        PoiBean temp = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        GetPoiThread getPoiThread = new GetPoiThread(temp,url);
        Future<PoiBean> future  = pool.submit(getPoiThread);
        while(true){
            if(future.isDone()){
                try{
                    temp= future.get();
                }catch (Exception e){
                    Log.e(TAG, "PlaceNameSearchWidget: " + e.getMessage());
                }
                pool.shutdown();
                break;
            }
        }
        return temp;
    }

    private Map<String,Object> attributeMap(PoiBean.PoisBean poisBean) {

        Map<String,Object>map=new HashMap<String,Object>();
        map.put("name",poisBean.getName());
        map.put("address",poisBean.getAddress());
        map.put("phone",poisBean.getPhone());
        return map;
    }

    private void displaySearchResult(Point point, Map<String,Object> map) {
        // dismiss any callout
        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
        // clear map of existing graphics
        //mapView.getGraphicsOverlays().clear();
        mGraphicsOverlay.getGraphics().clear();
        // create graphic object for resulting location
        //mGraphicsOverlay=new GraphicsOverlay();

        Point resultPoint = point;
        Graphic resultLocGraphic = new Graphic(resultPoint,map,mPinSourceSymbol1);
        // add graphic to location layer
        mGraphicsOverlay.getGraphics().add(resultLocGraphic);
        // zoom map to result over 3 seconds
        mapView.setViewpointAsync(new Viewpoint(resultPoint.getExtent()), 0.5f);
        // set the graphics overlay to the map
       // mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        showCallout(resultLocGraphic);
    }

    private void showCallout(final Graphic graphic) {
        // create a TextView for the Callout
        TextView calloutContent = new TextView(MPApplication.getContext());
        calloutContent.setTextColor(Color.BLACK);
        // set the text of the Callout to graphic's attributes
        calloutContent.setText("名称："+graphic.getAttributes().get("name").toString() + "\n"
                + "地址："+graphic.getAttributes().get("address").toString()+"\n"+"电话："+graphic.getAttributes().get("phone"));
        // get Callout
        mCallout = mapView.getCallout();
        // set Callout options: animateCallout: true, recenterMap: false, animateRecenter: false
        mCallout.setShowOptions(new Callout.ShowOptions(true, false, false));
        mCallout.setContent(calloutContent);
        // set the leader position and show the callout
        Point calloutLocation = graphic.computeCalloutLocation(graphic.getGeometry().getExtent().getCenter(), mapView);
        mCallout.setGeoElement(graphic, calloutLocation);
        mCallout.show();
    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();
        txtPoi.setText("");
        if(mGraphicsOverlay!=null){
            mGraphicsOverlay.getGraphics().clear();
            mapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            //mCallout.dismiss();
        }

        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }

        if(mGraphicsOverlay1!=null){
            mGraphicsOverlay1.getGraphics().clear();
            mapView.getGraphicsOverlays().remove(mGraphicsOverlay1);
        }
    }
}
