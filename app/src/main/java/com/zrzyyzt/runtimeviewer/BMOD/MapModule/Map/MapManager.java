package com.zrzyyzt.runtimeviewer.BMOD.MapModule.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.data.VectorTileCache;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.Raster;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseMap.BaseMapManager;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseMap.BasemapLayerInfo;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Listener.MapQueryListener;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;
import com.zrzyyzt.runtimeviewer.Common.Variable;
import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.UserLayers.GoogleLayer.GoogleWebTiledLayer;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.UserLayers.TianDiTuLayer.TianDiTuMethodsClass;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gisluq.lib.Util.ToastUtils;


/**
 * 地图组件管理类
 * Created by gis-luq on 2018/4/10.
 */

public class MapManager {

    private static String TAG = "MapManager";

    private Context context;
    private ResourceConfig resourceConfig;

    private ConfigEntity configEntity;

    private String projectPath;

    private ArcGISMap map;//地图容器

    private List<BasemapLayerInfo> basemapLayerInfoList = null;


    public MapManager(Context context, ResourceConfig resourceConfig, ConfigEntity ce, String dirPath){
        this.context = context;
        this.resourceConfig = resourceConfig;
        this.configEntity = ce;
        this.projectPath = dirPath;

//        this.map = new ArcGISMap();//初始化
//        resourceConfig.mapView.setMap(map);
        addBasemap();
        //addBasemapJygYx();
        addBasemapTdtSl();
        addBasemapTdtYx();
        addBasemapJygYx();
        //initBaseMapResource(); //初始化底图资源

        initMapResource();//初始化配置

        //切换到天地图影像
        changeBasemap("tdtyx");
    }


    /**
     * 初始化地图资源
     */
    private void initMapResource() {

        try {
            /**设置许可**/
            ArcGISRuntimeEnvironment.setLicense(configEntity.getRuntimrKey());
            Log.d(TAG, "init: lisence" + configEntity.getRuntimrKey());
            String version = ArcGISRuntimeEnvironment.getAPIVersion();
            String lic = ArcGISRuntimeEnvironment.getLicense().getLicenseLevel().name();
            //ToastUtils.showShort(context, "ArcGIS Runtime版本:" + version + "; 许可信息:" + lic);
        } catch (Exception e) {
            ToastUtils.showShort(context, "ArcGIS Runtime 许可设置异常:" + e.getMessage());
        }

        resourceConfig.mapDefualtListener = new DefaultMapViewOnTouchListener(context, resourceConfig.mapView);

        /***显示放大镜*/
        resourceConfig.mapView.setMagnifierEnabled(false);
        resourceConfig.mapView.setCanMagnifierPanMap(false);

//        /**最大最小比例尺设置*/
//        resourceConfig.mapView.getMap().setMinScale(999999999);//最小比例尺
//        resourceConfig.mapView.getMap().setMaxScale(1500);//最大比例尺

        /***不显示Esri LOGO*/
        resourceConfig.mapView.setAttributionTextVisible(false);

        /**旋转*/
//        final Compass mCompass = new Compass(context, null, resourceConfig.mapView);
//        mCompass.setClickable(true);
//        resourceConfig.compassView.addView(mCompass);
//        // Set a single tap listener on the MapView.
//        mCompass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // When a single tap gesture is received, reset the map to its default rotation angle,
//                // where North is shown at the top of the device.
//                resourceConfig.mapView.setViewpointRotationAsync(0);
//
//                //设置旋转角度
//                mCompass.setRotationAngle(0);
//                //设置旋转隐藏
//                mCompass.setVisibility(View.GONE);
//            }
//        });

        /**显示比例尺级别*/
        final DecimalFormat df = new DecimalFormat("###");

        //设置默认比例尺级别
        String scale = df.format(resourceConfig.mapView.getMapScale());
        resourceConfig.txtMapScale.setText("比例尺 1:" + scale);


        //根据缩放设置比例尺级别
        resourceConfig.mapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                String scale = df.format(resourceConfig.mapView.getMapScale());
                resourceConfig.txtMapScale.setText("比例尺 1:" + scale);
//                Log.d(TAG, "initMapResource: " + resourceConfig.mapView.getMap().getSpatialReference().getWkid());
            }
        });

        /**
         * 设置定位相关事件
         */
//        MapConfigInfo.dmUserLocationManager = new DMUserLocationManager(context,resourceConfig.mapView);
//        resourceConfig.togbtnLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    ToastUtils.showShort(context,"定位开");
//                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_on);
//                    MapConfigInfo.dmUserLocationManager.start();
//                }else{
//                    ToastUtils.showShort(context,"定位关");
//                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_off);
//                    MapConfigInfo.dmUserLocationManager.stop();
//                    resourceConfig.txtLocation.setText(context.getString(R.string.txt_location_info));
//                }
//            }
//        });

        /**
         *设置放大缩小按钮相关
         */
        resourceConfig.zoomToolView.init(resourceConfig.mapView);
        resourceConfig.zoomToolView.isHorizontal(false);
        resourceConfig.zoomToolView.setZoomInNum(2);
        resourceConfig.zoomToolView.setZoomOutNum(2);

        resourceConfig.measureToolView.init(resourceConfig.mapView);
        resourceConfig.measureToolView.setShowText(false);
        resourceConfig.measureToolView.setSpatialReference(SpatialReference.create(4490));//TODO 配置文件读取
        resourceConfig.measureToolView.setLengthType(Variable.Measure.KM);
        resourceConfig.measureToolView.setAreaType(Variable.Measure.KM2);

        resourceConfig.mapRotateView.init(resourceConfig.mapView);

        resourceConfig.mapNorthView.init(resourceConfig.mapView);

        resourceConfig.mapQueryListener = new MapQueryListener(context, resourceConfig.mapView, resourceConfig.mapQueryViewLayout);
        resourceConfig.mapQueryView.init(resourceConfig.mapView, resourceConfig.mapDefualtListener, resourceConfig.mapQueryListener);
//        Log.d(TAG, "initMapResource: " + resourceConfig.mapLocationView);
//        resourceConfig.mapLocationView.init(resourceConfig.mapView);


//        if (basemapLayerInfoList != null){
//            BaseMapLayerImageViewAdapter adapter = new BaseMapLayerImageViewAdapter(context, resourceConfig.mapView.getMap().getBasemap().getBaseLayers(), basemapLayerInfoList);
//            resourceConfig.baseMapLayerListview.setAdapter(adapter);
//        }
        resourceConfig.img_jyg_yx.setOnClickListener(listener);
        resourceConfig.img_tdt_sl.setOnClickListener(listener);
        resourceConfig.img_tdt_yx.setOnClickListener(listener);
    }

    //添加地图切换事件
    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tdt_sl) {
                changeBasemap("tdtsl");
            } else if (v.getId() == R.id.tdt_yx) {
                changeBasemap("tdtyx");
            } else if (v.getId() == R.id.jyg_yx) {
                changeBasemap("dom2018");
            }
        }
    };

    private void changeBasemap(String layerName) {
        LayerList layers =  resourceConfig.mapView.getMap().getBasemap().getBaseLayers();
        for (Layer layer:layers
        ) {
            if(layer.getName().contains(layerName)){
                layer.setVisible(true);
            }else{
                layer.setVisible(false);
            }
        }
    }

    void addBasemap(){
        ArcGISMap map = new ArcGISMap();
        resourceConfig.mapView.setMap(map);
    }

    /**
     * 本地影像
     */
    void addBasemapJygYx(){
        //加载影像底图
        String theURLString =
                "http://61.178.245.189:9092/arcgis/rest/services/jcyx/MapServer";
        //http://61.178.245.189:9092/arcgis/rest/services/jccqyx/MapServer
        //http://61.178.152.45:6080/arcgis/rest/services/OneMap/JYGDOM2018/MapServer
        ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(theURLString);
        tiledLayer.setVisible(true);
        tiledLayer.setName("dom2018");
        resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(tiledLayer);
//        Basemap basemap = new Basemap(tiledLayer);
//        ArcGISMap map = new ArcGISMap(basemap);
//        resourceConfig.mapView.setMap(map);
    }

    /**
     * 天地图影像
     */
    void addBasemapTdtYx(){

        //加载天地图影像
        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_2000);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000);

        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
        webTiledLayer.setRequestConfiguration(requestConfiguration);
        webTiledLayer.loadAsync();
        webTiledLayer.setName("tdtyx");
        webTiledLayer.setVisible(false);

        webTiledLayer1.setRequestConfiguration(requestConfiguration);
        webTiledLayer1.loadAsync();
        webTiledLayer1.setName("tdtyxzj");
        webTiledLayer1.setVisible(false);

        resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);
        resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer1);

    }

    /**
     * 天地图矢量
     */
    void addBasemapTdtSl(){
        //加载天地图矢量
        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_2000);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000);

        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
        webTiledLayer.setRequestConfiguration(requestConfiguration);
        webTiledLayer.loadAsync();
        webTiledLayer.setName("tdtsl");
        webTiledLayer.setVisible(false);

        webTiledLayer1.setRequestConfiguration(requestConfiguration);
        webTiledLayer1.loadAsync();
        webTiledLayer1.setName("tdtslzj");
        webTiledLayer1.setVisible(false);

        resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);
        resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer1);

    }

    /**
     * 截图
     */
    public Bitmap getMapViewBitmap() {
        MapView v = resourceConfig.mapView;
        v.clearFocus();
        v.setPressed(false);
        //能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while(cacheBitmap == null){
            final ListenableFuture<Bitmap> export = v.exportImageAsync();
            try {
                cacheBitmap =export.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }




    /**
     * 初始化基础底图资源
     */
    private void initBaseMapResource() {
        Log.d(TAG, "initBaseMapResource: start");
//        String strMapUrl="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
//        ArcGISTiledMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISTiledMapServiceLayer(strMapUrl);
//        resourceConfig.mapView.addLayer(arcGISTiledMapServiceLayer);
        String configPath = getBasemapPath("basemap.json");
        BaseMapManager basemapManager = new BaseMapManager(context,resourceConfig.mapView,configPath);
        basemapLayerInfoList= basemapManager.getBasemapLayerInfos();
        if (basemapLayerInfoList==null) return;
        for (int i=0;i<basemapLayerInfoList.size();i++){
            BasemapLayerInfo layerInfo = basemapLayerInfoList.get(i);
            String type = layerInfo.Type;
            if(type.equals(BasemapLayerInfo.LYAER_TYPE_TPK)){//TPK
                String path =getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)){//判断是否存在
                    TileCache tileCache = new TileCache(path);
                    ArcGISTiledLayer localTiledLayer = new ArcGISTiledLayer(tileCache);
                    localTiledLayer.setName(layerInfo.Name);
                    localTiledLayer.setVisible(layerInfo.Visable);
                    localTiledLayer.setOpacity((float) layerInfo.Opacity);
                    resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(localTiledLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalTiledPackage)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalTiledPackage)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIFF)){//Tiff
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    Raster raster = new Raster(path);
                    RasterLayer rasterLayer = new RasterLayer(raster);
                    rasterLayer.setName(layerInfo.Name);
                    rasterLayer.setVisible(layerInfo.Visable);
                    rasterLayer.setOpacity((float) layerInfo.Opacity);
                    resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(rasterLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalGeoTIFF)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalGeoTIFF)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_SERVERCACHE)){//Server缓存切片
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    TileCache tileCache = new TileCache(path);
                    ArcGISTiledLayer localTiledLayer = new ArcGISTiledLayer(tileCache);
                    localTiledLayer.setName(layerInfo.Name);
                    localTiledLayer.setVisible(layerInfo.Visable);
                    localTiledLayer.setOpacity((float) layerInfo.Opacity);
                    resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(localTiledLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalServerCache)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalServerCache)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_ONLINE_TILEDLAYER)){//在线瓦片
                String url = layerInfo.Path;
                ArcGISTiledLayer tiledMapServiceLayer = new ArcGISTiledLayer(url);
                tiledMapServiceLayer.setName(layerInfo.Name);
                tiledMapServiceLayer.setVisible(layerInfo.Visable);
                tiledMapServiceLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(tiledMapServiceLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_ONLINE_DYNAMICLAYER)) {//在线动态图层
                String url = layerInfo.Path;
                ArcGISMapImageLayer dynamicMapServiceLayer = new ArcGISMapImageLayer(url);
                dynamicMapServiceLayer.setName(layerInfo.Name);
                dynamicMapServiceLayer.setVisible(layerInfo.Visable);
                dynamicMapServiceLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(dynamicMapServiceLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_VTPK)){//VTPK
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    VectorTileCache vectorTileCache = new VectorTileCache(path);
                    ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vectorTileCache);
                    arcGISVectorTiledLayer.setName(layerInfo.Name);
                    arcGISVectorTiledLayer.setVisible(layerInfo.Visable);
                    arcGISVectorTiledLayer.setOpacity((float)layerInfo.Opacity);
                    resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(arcGISVectorTiledLayer);
                }else{
                    Log.d(TAG,"vtpk文件不存在,"+path);
                    Toast.makeText(context, "vtpk文件不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIANDITU_MAP)) {//天地图
//                TianDiTuLayerInfo tdtInfo = new TianDiTuLayerInfo();
//
//                TianDiTuLayerInfo tdtInfo01 = tdtInfo.initwithlayerType(TianDiTuLayerInfo.TianDiTuLayerType.TDT_VECTOR,
//                        TianDiTuLayerInfo.TianDiTuSpatialReferenceType.TDT_2000);
//                TianDiTuLayer ltl1 = new TianDiTuLayer(tdtInfo01.getTileInfo(), tdtInfo01.getFullExtent());
//                ltl1.setName(layerInfo.Name);
//                ltl1.setVisible(layerInfo.Visable);
//                ltl1.setOpacity((float) layerInfo.Opacity);
//
//                ltl1.setLayerInfo(tdtInfo01);
                WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_MERCATOR);
                webTiledLayer.setName(layerInfo.Name);
                webTiledLayer.setVisible(layerInfo.Visable);
                webTiledLayer.setOpacity((float) layerInfo.Opacity);
                RequestConfiguration requestConfiguration = new RequestConfiguration();
                requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
                webTiledLayer.setRequestConfiguration(requestConfiguration);
                webTiledLayer.loadAsync();

                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);

            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIANDITU_MAP_LABEL)){
                WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000);
                webTiledLayer.setName(layerInfo.Name);
                webTiledLayer.setVisible(layerInfo.Visable);
                webTiledLayer.setOpacity((float) layerInfo.Opacity);
                RequestConfiguration requestConfiguration = new RequestConfiguration();
                requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
                webTiledLayer.setRequestConfiguration(requestConfiguration);
                webTiledLayer.loadAsync();
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIANDITU_IMAGE)) {//天地图影像图
//                TianDiTuLayerInfo tdtInfo = new TianDiTuLayerInfo();
//                TianDiTuLayerInfo tdtInfo01 = tdtInfo.initwithlayerType(TianDiTuLayerInfo.TianDiTuLayerType.TDT_IMAGE,
//                        TianDiTuLayerInfo.TianDiTuSpatialReferenceType.TDT_2000);
//                TianDiTuLayer ltl1 = new TianDiTuLayer(tdtInfo01.getTileInfo(), tdtInfo01.getFullExtent());
//                ltl1.setName(layerInfo.Name);
//                ltl1.setVisible(layerInfo.Visable);
//                ltl1.setOpacity((float) layerInfo.Opacity);
//                ltl1.setLayerInfo(tdtInfo01);
//                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(ltl1);
                WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_MERCATOR);
                webTiledLayer.setName(layerInfo.Name);
                webTiledLayer.setVisible(layerInfo.Visable);
                webTiledLayer.setOpacity((float) layerInfo.Opacity);

                RequestConfiguration requestConfiguration = new RequestConfiguration();
                requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
                webTiledLayer.setRequestConfiguration(requestConfiguration);
                webTiledLayer.loadAsync();
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);

            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIANDITU_IMAGE_LABEL)) {//天地图影像标注图层
//                TianDiTuLayerInfo tdtannoInfo = new TianDiTuLayerInfo();
////                TianDiTuLayerInfo tdtannoInfo02 = tdtannoInfo.initwithlayerType(TianDiTuLayerInfo.TianDiTuLayerType.TDT_IMAGE,
////                        TianDiTuLayerInfo.TianDiTuLanguageType.TDT_CN, TianDiTuLayerInfo.TianDiTuSpatialReferenceType.TDT_2000);
////                TianDiTuLayer ltl2 = new TianDiTuLayer(tdtannoInfo02.getTileInfo(), tdtannoInfo02.getFullExtent());
////                ltl2.setName(layerInfo.Name);
////                ltl2.setVisible(layerInfo.Visable);
////                ltl2.setOpacity((float) layerInfo.Opacity);
////                ltl2.setLayerInfo(tdtannoInfo02);
////                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(ltl2);
                WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000);
                webTiledLayer.setName(layerInfo.Name);
                webTiledLayer.setVisible(layerInfo.Visable);
                webTiledLayer.setOpacity((float) layerInfo.Opacity);
                RequestConfiguration requestConfiguration = new RequestConfiguration();
                requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
                webTiledLayer.setRequestConfiguration(requestConfiguration);
                webTiledLayer.loadAsync();
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);

            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_GOOGLE_VECTOR)) {//谷歌矢量
                WebTiledLayer googleWebTiledLayer = GoogleWebTiledLayer.CreateGoogleLayer(GoogleWebTiledLayer.MapType.VECTOR);
                googleWebTiledLayer.setName(layerInfo.Name);
                googleWebTiledLayer.setVisible(layerInfo.Visable);
                googleWebTiledLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(googleWebTiledLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_GOOGLE_IMAGE)) {//谷歌影像
                WebTiledLayer googleWebTiledLayer = GoogleWebTiledLayer.CreateGoogleLayer(GoogleWebTiledLayer.MapType.IMAGE);
                googleWebTiledLayer.setName(layerInfo.Name);
                googleWebTiledLayer.setVisible(layerInfo.Visable);
                googleWebTiledLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(googleWebTiledLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_GOOGLE_TERRAIN)) {//谷歌地形
                WebTiledLayer googleWebTiledLayer = GoogleWebTiledLayer.CreateGoogleLayer(GoogleWebTiledLayer.MapType.TERRAIN);
                googleWebTiledLayer.setName(layerInfo.Name);
                googleWebTiledLayer.setVisible(layerInfo.Visable);
                googleWebTiledLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(googleWebTiledLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_GOOGLE_ROAD)) {//谷歌道路
                WebTiledLayer googleWebTiledLayer = GoogleWebTiledLayer.CreateGoogleLayer(GoogleWebTiledLayer.MapType.ROAD);
                googleWebTiledLayer.setName(layerInfo.Name);
                googleWebTiledLayer.setVisible(layerInfo.Visable);
                googleWebTiledLayer.setOpacity((float) layerInfo.Opacity);
                resourceConfig.mapView.getMap().getBasemap().getBaseLayers().add(googleWebTiledLayer);
            }
        }
    }

    /**
     * 获取基础底图路径
     * @param path
     * @return
     */
    private String getBasemapPath(String path){
        return projectPath+ File.separator+"BaseMap"+File.separator + path;
    }

}
