package com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.google.gson.Gson;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Adapter.PoiResultAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Entity.PoiBean;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Entity.PoiParm;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Thread.GetPoiThread;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlaceNameSearchWidget extends BaseWidget {
    private static final String TAG = "PlaceNameSearchWidget";
    private Context context;

    private  PoiBean poiBean=null;
    public View mWidgetView = null;
    private ListView resultList;

    private GraphicsOverlay mGraphicsOverlay;
    private PictureMarkerSymbol mPinSourceSymbol;
    private Callout mCallout;
    //public LocationClient mLocationClient = null;
    //private MyLocationListener myListener = new MyLocationListener();
    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {

        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示

        if(mGraphicsOverlay==null){
            mGraphicsOverlay=new GraphicsOverlay();
        }

        if(!mapView.getGraphicsOverlays().contains(mGraphicsOverlay)){
            mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        }

        //super.showMessageBox(super.name);//显示组件名称
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {

        context=super.context;

        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //设置widget组件显示内容
        mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_placenamesearch,null);

        resultList=mWidgetView.findViewById(R.id.widget_view_placenamesearch_resultListview);
        final SearchView mAddressSearchView = mWidgetView.findViewById(R.id.widget_view_placenamesearch_addressSearchView);
        TextView sTextView=mAddressSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        sTextView.setTextSize(14);
        //sTextView.setBackgroundColor(Color.BLUE);
        mAddressSearchView.setQueryHint("输入查询关键字！");
        mAddressSearchView.setIconifiedByDefault(false);
        mAddressSearchView.setIconified(false);
        mAddressSearchView.clearFocus();
        //mAddressSearchView.setFocusable(false);

        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.placenamesearch_pin);
        try {
            mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Picture Marker Symbol error: " + e.getMessage());
            Toast.makeText(MPApplication.getContext(), "没有加载定位图标！", Toast.LENGTH_LONG).show();
        }
        mPinSourceSymbol.setWidth(19f);
        mPinSourceSymbol.setHeight(72f);


        mAddressSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.contains("泾川")){
                    s="泾川"+s;
                }
                poiBean=getPoi(s);
                if(poiBean!=null){
                    List<PoiBean.PoisBean> list=poiBean.getPois();
                    PoiResultAdapter poiResultAdapter=new PoiResultAdapter(context,list);
                    resultList.setAdapter(poiResultAdapter);
                    mAddressSearchView.clearFocus();
                    //mAddressSearchView.onActionViewCollapsed();
                }
                else {
                    Toast.makeText(MPApplication.getContext(), "查询关键字不符合查询要求！", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object obj=resultList.getItemAtPosition(position);
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

    }

    private void identifyGraphic(MotionEvent motionEvent) {
        // get the screen point
        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                Math.round(motionEvent.getY()));
        // from the graphics overlay, get graphics near the tapped location
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultsFuture = mapView
                .identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10, false);
        identifyResultsFuture.addDoneListener(new Runnable() {
            @Override public void run() {
                try {
                    IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = identifyResultsFuture.get();
                    List<Graphic> graphics = identifyGraphicsOverlayResult.getGraphics();
                    // if a graphic has been identified
                    if (graphics.size() > 0) {
                        //get the first graphic identified
                        Graphic identifiedGraphic = graphics.get(0);
                        showCallout(identifiedGraphic);
                    } else {
                        // if no graphic identified
                        mCallout.dismiss();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Identify error: " + e.getMessage());
                }
            }
        });
    }


    private PoiBean getPoi(String keyWord) {

        String address = "http://api.tianditu.gov.cn/search?";
        String key = "1a2360bdad0e682aba76bfb388fcb849";
        PoiParm poiParm = new PoiParm();
        poiParm.setCount("20");
        poiParm.setKeyWord(keyWord);
        poiParm.setLevel("14");
        poiParm.setMapBound("107.104804,35.174813,107.741562,35.524559");
        poiParm.setQueryType("1");
        poiParm.setStart("0");
        Gson gson = new Gson();
        String strGson = gson.toJson(poiParm);
        //Log.v(TAG, strGson);
        String keyType = "&type=query&tk=" + key;
        String url = address + "postStr="+strGson + keyType;

        PoiBean temp = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        GetPoiThread getPoiThread = new GetPoiThread(temp, url);
        Future<PoiBean> future = pool.submit(getPoiThread);
        while (true) {
            if (future.isDone()) {
                try {
                    temp = future.get();
                } catch (Exception e) {
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

    private void displaySearchResult(Point point,Map<String,Object> map) {
        // dismiss any callout
        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
        mGraphicsOverlay.getGraphics().clear();

        Point resultPoint = point;
        Graphic resultLocGraphic = new Graphic(resultPoint,map,mPinSourceSymbol);
        mGraphicsOverlay.getGraphics().add(resultLocGraphic);
        mapView.setViewpointAsync(new Viewpoint(resultPoint.getExtent()), 0.5f);
        showCallout(resultLocGraphic);
    }

    private void showCallout(final Graphic graphic) {

        TextView calloutContent = new TextView(MPApplication.getContext());
        calloutContent.setTextColor(Color.BLACK);

        calloutContent.setText("名称："+graphic.getAttributes().get("name").toString() + "\n"
                + "地址："+graphic.getAttributes().get("address").toString()+"\n"+"电话："+graphic.getAttributes().get("phone"));

        mCallout = mapView.getCallout();
        mCallout.setShowOptions(new Callout.ShowOptions(true, false, false));
        mCallout.setContent(calloutContent);

        Callout.Style calloutStyle=mCallout.getStyle();
        calloutStyle.setBorderColor(Color.WHITE);
        calloutStyle.setBorderWidth(0);
        calloutStyle.setLeaderLength(15);
        calloutStyle.setLeaderPosition(Callout.Style.LeaderPosition.LOWER_MIDDLE);
        mCallout.setStyle(calloutStyle);

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
       // super.hideCenterView();
//
        if(mGraphicsOverlay!=null){
            mGraphicsOverlay.getGraphics().clear();
            mapView.getGraphicsOverlays().remove(mGraphicsOverlay);
        }

        if(resultList!=null) {
            resultList.setAdapter(null);
        }

        if(mapView.getCallout()!=null&& mapView.getCallout().isShowing()){
            mCallout.dismiss();
        }
    }
}
