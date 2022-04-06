package com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Listener;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.google.gson.Gson;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Bean.PoiBean;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Bean.PoiParmBean;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Thread.GetPoiThread;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Adapter.PoiResultAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PeripheralQueryListener extends DefaultMapViewOnTouchListener {

    public  final static String TAG="PeripheralQueryListener";
    private Context context;
    //private GraphicsOverlay graphicsOverlay;
    private MapView mapView;
    private  ListView listView;
    private Callout callout;
    private Point locationPoint;
    private PoiResultAdapter poiResultAdapter;

    private GraphicsOverlay mGraphicsOverlay;
    private PictureMarkerSymbol mPinSourceSymbol;
    private  ListView queryList;
    private String keyWords;
    private String radius;

    public PeripheralQueryListener(Context context, MapView mapView, ListView listView,String keyWords,String radius) {
        super(context, mapView);

        this.mapView=mapView;
        this.context=context;
        this.callout=mapView.getCallout();
        this.listView=listView;
        this.keyWords=keyWords;
        this.radius=radius;

        if(mGraphicsOverlay==null){
            mGraphicsOverlay=new GraphicsOverlay();
        }
        if(!mapView.getGraphicsOverlays().contains(mGraphicsOverlay)){
            mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        }

        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.widget_view_peripheralquery_qz);
        try {
            mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable).get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(MPApplication.getContext(), "没有加载定位图标！", Toast.LENGTH_LONG).show();
        }
        mPinSourceSymbol.setWidth(30f);
        mPinSourceSymbol.setHeight(72f);

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        locationPoint = mapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
        identifyGraphic(locationPoint);
        PoiBean poiBean = getPoi(keyWords, radius);
        if (poiBean != null) {
            List<PoiBean.PoisBean> list = poiBean.getPois();
            poiResultAdapter = new PoiResultAdapter(context, list, mapView);
            listView.setAdapter(poiResultAdapter);

        } else {
            Toast.makeText(MPApplication.getContext(), "没有查询到符合要求的记录！", Toast.LENGTH_LONG).show();
        }
        return super.onSingleTapUp(e);
    }


    private void identifyGraphic(Point point) {
        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("name","定位点");

        Point resultPoint = point;
        Graphic resultLocGraphic = new Graphic(resultPoint,map,mPinSourceSymbol);
        mGraphicsOverlay.getGraphics().add(resultLocGraphic);
        mapView.setViewpointAsync(new Viewpoint(resultPoint.getExtent()), 0.5f);
    }

    private PoiBean getPoi(String keyWord, String radius){

        String address="http://api.tianditu.gov.cn/search?";
        String key="1a2360bdad0e682aba76bfb388fcb849";
        PoiParmBean parmBean=new PoiParmBean();
        parmBean.setCount("20");
        parmBean.setKeyWord(keyWord);
        parmBean.setLevel("15");
        parmBean.setMapBound("107.104804,35.174813,107.741562,35.524559");
        parmBean.setPointLonlat(locationPoint.getX()+","+locationPoint.getY());
        parmBean.setQueryRadius(radius);
        parmBean.setQueryType("3");
        parmBean.setStart("0");
        Gson gson = new Gson();
        String parmJson=gson.toJson(parmBean);

        String keyType="&type=query&tk="+key;
        String url=address+"postStr="+parmJson+keyType;
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

    public void clear(){
        mGraphicsOverlay.getGraphics().clear();
        listView.setAdapter(null);
        if(poiResultAdapter!=null){
            poiResultAdapter.clearPeripheralQuery();
        }
    }
}
