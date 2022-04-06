package com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Bean.PoiBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PoiResultAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public LinearLayout view;
        public ImageView imageView;
        public TextView textView;//图层
    }

    private static final String TAG = "PoiResultAdapter";
    private Context context;
    private List<PoiBean.PoisBean>poisBeanList;
    private MapView mapView;
    private Callout mCallout;
    private GraphicsOverlay mGraphicsOverlay;
    private PictureMarkerSymbol mPinSourceSymbol;

    public PoiResultAdapter(Context context, List<PoiBean.PoisBean> poisBeanList, MapView mapView) {
        this.context=context;
        this.poisBeanList=poisBeanList;
        this.mapView=mapView;

        if(mGraphicsOverlay==null){
            mGraphicsOverlay=new GraphicsOverlay();
        }
        if(!mapView.getGraphicsOverlays().contains(mGraphicsOverlay)){
            mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        }

        BitmapDrawable pinDrawable1 = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.placenamesearch_pin);
        try {
            mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable1).get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(MPApplication.getContext(), "没有加载定位图标！", Toast.LENGTH_LONG).show();
        }
        mPinSourceSymbol.setWidth(19f);
        mPinSourceSymbol.setHeight(72f);
    }
    @Override
    public int getCount() {
        return poisBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return poisBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterHolder holder=new AdapterHolder();
        convertView= LayoutInflater.from(context).inflate(R.layout.widget_view_peripheralquerywidget_poi_item,null);
        holder.view=(LinearLayout)convertView.findViewById(R.id.widget_view_peripheralquerywidget_poi_item_view);
        holder.textView = convertView.findViewById(R.id.widget_view_peripheralquerywidget_poi_item_txtName);
        final  PoiBean.PoisBean poisBean=poisBeanList.get(position);
        holder.textView.setText(poisBean.getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearPeripheralQuery();

                String lonlat=poisBean.getLonlat();
                String [] strLonlat=lonlat.trim().split(" ");
                if(strLonlat.length<1)return;
                Map<String,Object> map=attributeMap(poisBean);
                Double lon=Double.valueOf(strLonlat[0]);
                Double lat=Double.valueOf(strLonlat[1]);

                Point addressPoint=new Point(lon,lat);
                displaySearchResult(addressPoint,map);
            }
        });
       return convertView;
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

        mGraphicsOverlay.getGraphics().clear();

        Point resultPoint = point;
        Graphic resultLocGraphic = new Graphic(resultPoint,map,mPinSourceSymbol);
        // add graphic to location layer
        mGraphicsOverlay.getGraphics().add(resultLocGraphic);
        // zoom map to result over 3 seconds
        mapView.setViewpointAsync(new Viewpoint(resultPoint.getExtent()), 0.5f);
        // set the graphics overlay to the map
        mapView.getGraphicsOverlays().add(mGraphicsOverlay);
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

        Callout.Style calloutStyle=mCallout.getStyle();
        calloutStyle.setBorderColor(Color.WHITE);
        calloutStyle.setBorderWidth(0);
        //calloutStyle.setLeaderLength(100);
        calloutStyle.setLeaderPosition(Callout.Style.LeaderPosition.LOWER_MIDDLE);
        mCallout.setStyle(calloutStyle);

        Point calloutLocation = graphic.computeCalloutLocation(graphic.getGeometry().getExtent().getCenter(), mapView);
        mCallout.setGeoElement(graphic, calloutLocation);
        mCallout.show();
    }

    public void clearPeripheralQuery(){

        if(mGraphicsOverlay!=null){
            mGraphicsOverlay.getGraphics().clear();
            mapView.getGraphicsOverlays().remove(mGraphicsOverlay);
        }

        if (mapView.getCallout() != null && mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
    }
}
