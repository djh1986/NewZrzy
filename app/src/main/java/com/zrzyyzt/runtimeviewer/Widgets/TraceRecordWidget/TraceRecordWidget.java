package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.Extent;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.SpatialReference;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Adapter.TraceRecordListViewAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Entity.TraceRecordEntity;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Manager.TraceRecordManager;

import java.util.List;

import gisluq.lib.Util.DateUtils;
import gisluq.lib.Util.ToastUtils;

import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.TraceRecordStatusPause;
import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.TraceRecordStatusStart;
import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.TraceRecordStatusStop;
import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.ptSym;

public class TraceRecordWidget extends BaseWidget {

    private static final String TAG = "TraceRecordWidget";
    public View traceRecordView = null;//
    private Context context;
    private GraphicsOverlay graphicsLayer = null;
    private PointCollection pointCollection = null;
    private Point lastPoint = null;
    private LocationDisplay locationDisplay;

    private TraceRecordManager traceRecordManager;
    private List<TraceRecordEntity> traceRecordEntityList;
    private ListView traceRecordListView;
    private TraceRecordListViewAdapter traceRecordListViewAdapter;

    //?????????Start Stop Pause
    private String status;

    View btnStart;
    View btnPause;
    View btnStop;

    @Override
    public void create() {
        this.context = super.context;
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        traceRecordView = mLayoutInflater.inflate(R.layout.widget_view_trace_record,null);


        //????????????
        btnStart =  traceRecordView.findViewById(R.id.widget_view_trace_record_start);
        btnPause =  traceRecordView.findViewById(R.id.widget_view_trace_record_pause);
        btnStop =  traceRecordView.findViewById(R.id.widget_view_trace_record_stop);

        btnStart.setOnClickListener(clickListener);
        btnPause.setOnClickListener(clickListener);
        btnStop.setOnClickListener(clickListener);


        btnPause.setClickable(false);
        btnStop.setClickable(false);
        btnStop.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
        btnPause.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));

        //?????????????????????
        status = TraceRecordStatusStop;

        //??????????????????
        graphicsLayer = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsLayer);


        //????????????????????????
        traceRecordManager = new TraceRecordManager(projectPath);
        traceRecordEntityList = traceRecordManager.getTraceRecordList2();

        if(traceRecordEntityList!=null){
            for (TraceRecordEntity traceRecord:traceRecordEntityList
                 ) {
                Log.d(TAG, "TraceRecordEntity: " + traceRecord.getName()
                        + "," + traceRecord.getCoords()
                        + "," + traceRecord.getCreateTime());
            }

        }
        traceRecordListViewAdapter = new TraceRecordListViewAdapter(context, traceRecordEntityList,
                super.mapView, projectPath, graphicsLayer, status);

        traceRecordListView =  traceRecordView.findViewById(R.id.widget_view_trace_record_listView);
        traceRecordListView.setAdapter(traceRecordListViewAdapter);

        //??????????????????

//        PermissionsUtils.PermissionsChecker(context);



        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                LocationDataSource.Location location = locationChangedEvent.getLocation();
                Point position = location.getPosition();
                if(pointCollection==null){
                    pointCollection = new PointCollection(position.getSpatialReference());
                }
                if(lastPoint == null){
                    lastPoint = position;
                }else{
                    GeodeticDistanceResult geodeticDistanceResult = GeometryEngine.distanceGeodetic(position, lastPoint,
                            new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
                    double distance = geodeticDistanceResult.getDistance();

                    String msg ="x:" + position.getX()+", y:" + position.getY() + ", wkid:" + position.getSpatialReference().getWkid()
                            + ", size:" + pointCollection.size()
//                            + ", graphics size:" + graphicsLayer.getGraphics().size()
                            + ", distance:" + distance;
                    //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    if(distance < 5)
                        return;
                }

                pointCollection.add(position);
                lastPoint = position;

//                SimpleMarkerSymbol ptSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 10);
                Graphic graphic = new Graphic(position, ptSym);

                graphicsLayer.getGraphics().add(graphic);
            }
        });



    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.widget_view_trace_record_start:
                    if(status.equalsIgnoreCase(TraceRecordStatusStart)){
                        ToastUtils.showLong(context,"??????????????????????????????????????????!");
                    }else{
                        locationDisplay.startAsync();
                        status = TraceRecordStatusStart;
                        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        ToastUtils.showLong(context,"??????????????????");

                        btnStart.setClickable(false);
                        btnStop.setClickable(true);
                        btnPause.setClickable(true);

                        btnStart.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
                        btnStop.setBackground(context.getDrawable(R.drawable.map_shadow_bg));
                        btnPause.setBackground(context.getDrawable(R.drawable.map_shadow_bg));
                    }
                    break;
                case R.id.widget_view_trace_record_pause:
                    if(!status.equalsIgnoreCase(TraceRecordStatusStart) ){
                        ToastUtils.showLong(context,"????????????????????????!");
                    }else{
                        if(locationDisplay.isStarted()){
                            locationDisplay.stop();
                            status = TraceRecordStatusPause;
                            ToastUtils.showLong(context,"??????????????????");

                            btnStart.setClickable(true);
                            btnPause.setClickable(false);
                            btnStart.setBackground(context.getDrawable(R.drawable.map_shadow_bg));
                            btnPause.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
                        }
                    }
                    break;
                case R.id.widget_view_trace_record_stop:
                    if(!status.equalsIgnoreCase(TraceRecordStatusStart) && !status.equalsIgnoreCase(TraceRecordStatusPause)){
                        ToastUtils.showLong(context,"????????????????????????!");
                    }else{
                        if(locationDisplay.isStarted()){
                            locationDisplay.stop();
                        }
                        //???????????????????????????????????????
                        if(pointCollection != null && pointCollection.size() > 0){

                            //????????????
                            Viewpoint currentViewpoint = mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY);
                            Envelope envelope = currentViewpoint.getTargetGeometry().getExtent();
                            Extent extent = new Extent(envelope.getXMin(),
                                    envelope.getYMin(),
                                    envelope.getXMax(),
                                    envelope.getYMax(),
                                    new SpatialReference(envelope.getSpatialReference().getWkid()));


                            String pointStrs = PointCollection2String(pointCollection);

                            //??????????????????
                            TraceRecordEntity traceRecordEntity = new TraceRecordEntity(DateUtils.getTimeNow(),DateUtils.getTimeNow(),pointStrs);
                            traceRecordEntity.setExtent(extent);

                            traceRecordEntityList.add(traceRecordEntity);
                            String traceRecordListString = traceRecordManager.TraceRecordList2String(traceRecordEntityList);
                            boolean saveFlag = traceRecordManager.saveTraceRecordListConfig(traceRecordListString);
                            if(saveFlag){
                                ToastUtils.showLong(context,"????????????????????????");
                            }else{
                                ToastUtils.showLong(context,"????????????????????????");
                            }
                            status = TraceRecordStatusStop;
                            traceRecordListViewAdapter.refreshData();
                            lastPoint = null;
                            pointCollection = null;
                            graphicsLayer.getGraphics().clear();
                            ToastUtils.showLong(context,"??????????????????");

                            btnStart.setClickable(true);
                            btnStop.setClickable(false);
                            btnPause.setClickable(false);
                            btnStop.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
                            btnStart.setBackground(context.getDrawable(R.drawable.map_shadow_bg));
                            btnPause.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
                        }
                    }
                    break;
            }
        }


    } ;

    private String PointCollection2String(PointCollection tempPointCollection) {
        if (tempPointCollection == null) return "";
        String pointStr = "";
        for (int i = 0; i < tempPointCollection.size(); i++) {
            pointStr += tempPointCollection.get(i).getX() + " " + tempPointCollection.get(i).getY() + ",";
        }
        pointStr = pointStr.substring(0, pointStr.length() - 1);
        return pointStr;
    }

    @Override
    public void active() {
        super.active();
        super.showWidget(traceRecordView);
    }

    @Override
    public void inactive() {
        super.inactive();
    }
}
