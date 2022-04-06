package com.zrzyyzt.runtimeviewer.BMOD.MapModule.Resource;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Listener.MapQueryListener;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView.MapNorthView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView.MapQueryView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView.MapRotateView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView.MapZoomView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView.MeasureToolView;
import com.zrzyyzt.runtimeviewer.R;

/**
 * 资源绑定注册类
 */
public class ResourceConfig {
    private static final String TAG = "ResourceConfig";
    public Context context;
    private Activity activiy;

    public ResourceConfig(Context context){
        this.context = context;
        this.activiy = (Activity)context;
        initConfig();
    }

    /**资源列表**/
    public MapView mapView = null;//地图控件
//    public RelativeLayout compassView =null;//指北针控件
    public TextView txtMapScale = null;//比例尺
    public View baseWidgetView = null;//widget组件
    public ImageView imgCenterView = null;//中心十字叉
    public ToggleButton togbtnLocation=null;//定位按钮
    public TextView txtLocation = null;
    public FloatingActionButton btnPointCollect=null;//采集点

    public LinearLayout baseWidgetToolsView;//widget组件工具列表

    public MapZoomView zoomToolView;//MapZoomView组件
    public MeasureToolView measureToolView;
    public MapRotateView mapRotateView;
    public MapNorthView mapNorthView;
    public MapQueryView mapQueryView;

    public View mapQueryViewLayout;
//    public MapLocationView mapLocationView;

    public ImageView img_tdt_yx;
    public ImageView img_tdt_sl;
    public ImageView img_jyg_yx;

    public ListView baseMapLayerListview;


    public MapQueryListener mapQueryListener;
    public DefaultMapViewOnTouchListener mapDefualtListener;

//    public View view_tdt_yx;
//    public View view_tdt_sl;
//    public View view_china_colour;
//    public View view_china_blue;

    /**
     * 初始化资源列表
     */
    private void initConfig() {
        this.mapView = (MapView)activiy.findViewById(R.id.activity_map_mapview);
//        this.compassView = (RelativeLayout)activiy.findViewById(R.id.activity_map_compass);
        this.txtMapScale =  (TextView)activiy.findViewById(R.id.activity_map_mapview_scale);
        this.baseWidgetView = activiy.findViewById(R.id.base_widget_view_baseview);
        this.imgCenterView = (ImageView)activiy.findViewById(R.id.activity_map_imgCenterView);
//        this.togbtnLocation = (ToggleButton) activiy.findViewById(R.id.activity_map_togbtnLocation);

        this.txtLocation = (TextView)activiy.findViewById(R.id.activity_map_mapview_locationInfo);

        this.baseWidgetToolsView = (LinearLayout)activiy.findViewById(R.id.base_widget_view_tools_linerview);

        this.btnPointCollect = activiy.findViewById(R.id.activity_map_faBtnpointCollect);

        this.zoomToolView = activiy.findViewById(R.id.map_zoom_btn);
        this.measureToolView = activiy.findViewById(R.id.measure_tool);
        this.mapRotateView = activiy.findViewById(R.id.map_rotate_view);
        this.mapNorthView = activiy.findViewById(R.id.map_north_view);
        this.mapQueryView = activiy.findViewById(R.id.map_query_view);

        this.mapQueryViewLayout  = LayoutInflater.from(context).inflate(R.layout.map_query_result_view_1,null);
//        this.mapLocationView = (MapLocationView) activiy.findViewById(R.id.map_location_view);

//        this.view_tdt_yx = activiy.findViewById(R.id.basemap_tdt_yx);
//        this.view_tdt_sl = activiy.findViewById(R.id.basemap_tdt_sl);
//        this.view_china_colour = activiy.findViewById(R.id.basemap_china_colour);
//        this.view_china_blue = activiy.findViewById(R.id.basemap_china_blue);
        img_tdt_yx = activiy.findViewById(R.id.tdt_yx);
        img_tdt_sl = activiy.findViewById(R.id.tdt_sl);
        img_jyg_yx = activiy.findViewById(R.id.jyg_yx);
    }


    public void setMeasureToolViewVisibility() {
        if(measureToolView==null) return;
        Log.d(TAG, "setMeasureToolViewVisibility: " + measureToolView.getVisibility());
        if(measureToolView.getVisibility() == View.VISIBLE){
            measureToolView.setVisibility(View.GONE);
            measureToolView.inactivity();
        }
        else if(measureToolView.getVisibility() == View.GONE){
            measureToolView.setVisibility(View.VISIBLE);
            measureToolView.init(mapView);
        }
    }
}
