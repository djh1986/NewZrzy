package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.GeoPackageFeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.Config.AppConfig;
import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Thread.GetPdfFileThread;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Adapter.LayerListviewAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Adapter.LegendListviewAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.OperationLayerInfo;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.OperationLayerInfo2;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Manager.OperationMapManager;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Thread.GetOperationLayerInfo2Thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.DocManagerWidget.configEntity;

/**
 * 图层控制
 * Created by gis-luq on 2017/5/5.
 */
public class LayerManagerWidget extends BaseWidget {

    private static String TAG = "LayerManagerWidget";

    public View mWidgetView = null;//
//    public ListView baseMapLayerListView = null;
    public ListView featureLayerListView = null;

    private Context context;
    public static ConfigEntity configEntity = null;

    private LayerListviewAdapter featureLayerListviewAdapter =null;
    private LegendListviewAdapter legendListviewAdapter = null;

//    private MapQueryOnTouchListener mapQueryOnTouchListener;
//    private View.OnTouchListener defauleOnTouchListener;//默认点击事件

    @Override
    public void active() {
        super.active();
        super.showWidget(mWidgetView);
//        initMapQuery();
    }

    private void initMapQuery() {
//        mapView.setMagnifierEnabled(true);//放大镜
//        if (mapQueryOnTouchListener!=null){
//            super.mapView.setOnTouchListener(mapQueryOnTouchListener);
//        }
//        mapQueryOnTouchListener.clear();//清空当前选择
    }

    @Override
    public void create() {

        context = super.context;
//        defauleOnTouchListener = super.mapView.getOnTouchListener();

        if (configEntity==null){
            configEntity = AppConfig.getConfig(context);
        }

//        initBaseMapResource();//初始化底图

        //initOperationalGdbLayers();//初始化Geo图层

        //initOperationalLayers();//初始化业务图层

        initOperationalLayersWeb();//初始化网络业务图层

        //initGeoPackageLayers();//初始化业务图层 gpkg

        //initJsonLayers();//初始化json图层

        initWidgetView();//初始化UI

        Point point=new Point(107.3625770,35.333313);
        Viewpoint vp = new Viewpoint(point.getExtent());
        mapView.setViewpointCenterAsync(point,20000);
    }

    /**
     * UI初始化
     */
    private void initWidgetView() {
        /**
         * **********************************************************************************
         * 布局容器
         */
        mWidgetView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager,null);
//        TextView txtLayerListBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_layer_manager_txtBtnLayerList);
//        final View viewLayerListSelect = mWidgetView.findViewById(R.id.widget_view_layer_manager_viewLayerList);
//        TextView txtLegendBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_layer_manager_txtBtnLegend);
//        final View viewLegendSelect = mWidgetView.findViewById(R.id.widget_view_layer_manager_viewLegend);
        final RelativeLayout viewContent = mWidgetView.findViewById(R.id.widget_view_layer_manager_contentView);//内容区域

        /**
         * **********************************************************************************
         * 图例
         */
//        final View legendView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager_legend,null);
//        ListView listViewLenged = legendView.findViewById(R.id.widget_view_layer_manager_legend_layerListview);
//        legendListviewAdapter = new LegendListviewAdapter(context,super.mapView.getMap().getOperationalLayers());
//        listViewLenged.setAdapter(legendListviewAdapter);
//        TextView textViewNoLegend = (TextView) legendView.findViewById(R.id.widget_view_layer_manager_legend_txtNoLegend);

        /**
         * **********************************************************************************
         * 图层列表
         */
        final View layerManagerView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager_layers,null);
//        this.baseMapLayerListView = (ListView)layerManagerView.findViewById(R.id.widget_view_layer_manager_layers_basemapLayerListview);
        this.featureLayerListView = (ListView)layerManagerView.findViewById(R.id.widget_view_layer_manager_layers_featureLayerListview);

//        basemapLayerListviewAdapter = new LayerListviewAdapter(context,super.mapView.getMap().getBasemap().getBaseLayers());
//        this.baseMapLayerListView.setAdapter(basemapLayerListviewAdapter);
        featureLayerListviewAdapter = new LayerListviewAdapter(context,super.mapView.getMap().getOperationalLayers());
        this.featureLayerListView.setAdapter(featureLayerListviewAdapter);

        //业务图层操作
        Button operationBtnMore = (Button)layerManagerView.findViewById(R.id.widget_view_layer_managet_layers_operationlayer_btnMore);
        operationBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pmp = new PopupMenu(context, v);
                pmp.getMenuInflater().inflate(R.menu.menu_layer_handle_tools, pmp.getMenu());
                pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_layer_handle_tools_openAllLayer:
                                for (int i=0;i<mapView.getMap().getOperationalLayers().size();i++){
                                    Layer layer = mapView.getMap().getOperationalLayers().get(i);
                                    Log.d(TAG, "onMenuItemClick: " + layer.getName());
                                    layer.setVisible(true);
                                }
                                featureLayerListviewAdapter.refreshData();
                                break;
                            case R.id.menu_layer_handle_tools_closedAllLayer:
                                for (int i=0;i<mapView.getMap().getOperationalLayers().size();i++){
                                    Layer layer = mapView.getMap().getOperationalLayers().get(i);
                                    layer.setVisible(false);
                                }
                                featureLayerListviewAdapter.refreshData();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pmp.show();
            }
        });

        //底图图层
//        Button basemapnBtnMore = (Button)layerManagerView.findViewById(R.id.widget_view_layer_managet_layers_basemap_btnMore);
//        basemapnBtnMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu pmp = new PopupMenu(context, v);
//                pmp.getMenuInflater().inflate(R.menu.menu_layer_handle_tools, pmp.getMenu());
//                pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.menu_layer_handle_tools_openAllLayer:
//                                for (int i=0;i<mapView.getMap().getBasemap().getBaseLayers().size();i++){
//                                    Layer layer = mapView.getMap().getBasemap().getBaseLayers().get(i);
//                                    layer.setVisible(true);
//                                }
//                                basemapLayerListviewAdapter.refreshData();
//                                break;
//                            case R.id.menu_layer_handle_tools_closedAllLayer:
//                                for (int i=0;i<mapView.getMap().getBasemap().getBaseLayers().size();i++){
//                                    Layer layer = mapView.getMap().getBasemap().getBaseLayers().get(i);
//                                    layer.setVisible(false);
//                                }
//                                basemapLayerListviewAdapter.refreshData();
//                                break;
//                            default:
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                pmp.show();
//            }
//        });

        /**
         * **********************************************************************************
         * 布局容器事件
         */
        viewContent.addView(layerManagerView);//默认显示图层列表
//        txtLayerListBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewContent.getChildAt(0)!=layerManagerView){
//                    viewContent.removeAllViews();
//                    viewContent.addView(layerManagerView);
////                    txtLayerListBtn.setTextColor();
//                    viewLayerListSelect.setVisibility(View.VISIBLE);
////                    viewLegendSelect.setVisibility(View.GONE);
//                }
//            }
//        });

//        txtLegendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewContent.getChildAt(0)!=legendView){
//                    viewContent.removeAllViews();
//                    viewContent.addView(legendView);
//                    viewLayerListSelect.setVisibility(View.GONE);
//                    viewLegendSelect.setVisibility(View.VISIBLE);
//
//                    //打开窗口前先刷新数据面板
//                    legendListviewAdapter.refreshData();
//                }
//            }
//        });


//        final View mapQueryView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query_mapquery_1,null);
//        View viewBtnSelectFeature = mapQueryView.findViewById(R.id.widget_view_query_mapquery_linerBtnFeatureSelect);//要素选择
//        TextView txtLayerName = (TextView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_1_txtLayerName);
//        ListView listViewField = (ListView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_1_fieldListview);
//        mapQueryOnTouchListener = new MapQueryOnTouchListener(context, mapView, mapQueryView);
    }

    @Override
    public void inactive(){
        super.inactive();
        returnDefault();
    }

    private void returnDefault() {

//        if (mapQueryOnTouchListener!=null){
//            super.mapView.setOnTouchListener(defauleOnTouchListener);//窗口关闭恢复默认点击状态
//        }
//        mapQueryOnTouchListener.clear();//清空当前选择
//        mapView.setMagnifierEnabled(false);//放大镜
    }

    /**
     * 根据json文件
     */
    private void initOperationalLayersWeb(){
        String configPath = getOperationalLayersJsonPath("operationlayers.json");
        OperationMapManager oprationMapManager = new OperationMapManager(context ,super.mapView, configPath);
        List<OperationLayerInfo>  operationLayerInfoList= oprationMapManager.getOperationLayerInfos();
        //Log.d(TAG,"operationLayerInfoList:"+operationLayerInfoList);
        List<OperationLayerInfo2> OperationLayerInfo2List=getWebOperationLayerInfo2s();
        if (OperationLayerInfo2List==null) return;
        List<OperationLayerInfo2> list =SortingByLayerIndex(OperationLayerInfo2List);
        //Log.d(TAG, "initOperationalLayersWeb: " + operationLayerInfoList);
        for (int i=0;i<list.size();i++){
            OperationLayerInfo2 layerInfo = list.get(i);
            String type = layerInfo.type;
            if(type.equals(OperationLayerInfo2.LYAER_TYPE_ONLINE_TILEDLAYER)){
                ArcGISTiledLayer tiledLayerBaseMap = new ArcGISTiledLayer(layerInfo.path);
                tiledLayerBaseMap.setOpacity((float) layerInfo.opacity);
                tiledLayerBaseMap.setName(layerInfo.name);
                tiledLayerBaseMap.setVisible(layerInfo.visable);
                //Log.d(TAG, "initOperationalLayersWeb: add layer start");

                mapView.getMap().getOperationalLayers().add(tiledLayerBaseMap);
                //Log.d(TAG, "initOperationalLayersWeb: add layer end");
            }
        }
    }

    private List<OperationLayerInfo2> getWebOperationLayerInfo2s(){
        List<OperationLayerInfo2> temp = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        GetOperationLayerInfo2Thread getOperationLayerInfo2Thread = new GetOperationLayerInfo2Thread(temp,configEntity.getIpAddress());
        Future<List<OperationLayerInfo2>> future  = pool.submit(getOperationLayerInfo2Thread);
        //Log.v("wwww","33333333");
        while(true){
            if(future.isDone()){
                try{
                    temp= future.get();
                }catch (Exception e){
                    Log.e(TAG, "getOperationLayerInfo2Thread: " + e.getMessage());
                }
                pool.shutdown();
                break;
            }
        }
        Log.d(TAG, "webOperationLayerInfo2s: "+ temp);
        return temp;
    }

    /**
     * 业务图层列表排序——正序
     * @param operationLayerInfoList
     * @return
     */
    private List<OperationLayerInfo2> SortingByLayerIndex(List<OperationLayerInfo2> operationLayerInfoList) {
        List<OperationLayerInfo2> result = null;
        if(operationLayerInfoList !=null){
            result = new ArrayList<>();
            /**
             * Collections.sort(list, new PriceComparator());的第二个参数返回一个int型的值，就相当于一个标志，告诉sort方法按什么顺序来对list进行排序。
             * 按照LayerIndex从小到大排序
             */
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    OperationLayerInfo2 p1 = (OperationLayerInfo2) o1;
                    OperationLayerInfo2 p2 = (OperationLayerInfo2) o2;
                    if (p1.layerIndex < p2.layerIndex)
                        return -1;
                    else if (p1.layerIndex == p2.layerIndex)
                        return 0;
                    else if (p1.layerIndex > p2.layerIndex)
                        return                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     1;
                    return 0;
                }
            };
            Collections.sort(operationLayerInfoList, comp);
            result = operationLayerInfoList;
        }
        return result;
    }
    /**
     * 初始化业务图层-shapefile
     */
    private void initOperationalLayers(){
        String path = getOperationalLayersPath();
        Log.d(TAG, "initOperationalLayers: " + path);
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(path,"shp");
        if (fileInfos==null || fileInfos.size()<=0) {

            return;
        }
        for (int i=0;i<fileInfos.size();i++) {
            FileUtils.FileInfo fileInfo = fileInfos.get(i);
            final String fileName = fileInfo.FileName.replace(".shp","");
            Log.d(TAG, "run: " + fileInfos.get(i) );
            final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(fileInfo.FilePath);
            shapefileFeatureTable.loadAsync();//异步方式读取文件
            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {

                    if(shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED){
                        //数据加载完毕后，添加到地图
                        FeatureLayer mainShapefileLayer = new FeatureLayer(shapefileFeatureTable);
                        mainShapefileLayer.setName(fileName);

                        if(mainShapefileLayer.getName().trim().equals("泾川宗地")) {
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2.0f);
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.YELLOW, lineSymbol);

                            // create the Renderer
                            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
                            mainShapefileLayer.setRenderer(renderer);

                            TextSymbol democratTextSymbol = new TextSymbol();
                            democratTextSymbol.setSize(10);
                            democratTextSymbol.setColor(Color.BLUE);
                            democratTextSymbol.setHaloColor(Color.WHITE);
                            democratTextSymbol.setHaloWidth(2);

                            JsonObject json = new JsonObject();
                            // use a custom label expression combining some of the feature's fields
                            JsonObject expressionInfo = new JsonObject();
                            expressionInfo.add("expression", new JsonPrimitive("$feature.权利人"));
                            json.add("minScale",new JsonPrimitive(10000));
                            //expressionInfo.add("expression", new JsonPrimitive("$feature.QLRMC + \"\\n \" + $feature.BDCDYH "));
                            json.add("labelExpressionInfo", expressionInfo);
                            // position the label in the center of the feature
                            json.add("labelPlacement", new JsonPrimitive("esriServerPolygonPlacementAlwaysHorizontal"));
                            json.add("where", new JsonPrimitive("1 = 1"));
                            json.add("symbol", new JsonParser().parse(democratTextSymbol.toJson()));

                            LabelDefinition democratLabelDefinition = LabelDefinition.fromJson(json.toString());

                            mainShapefileLayer.getLabelDefinitions().add(democratLabelDefinition);

                        }
                        else if(mainShapefileLayer.getName().trim().equals("泾川房屋")){
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#03a9f4"), 1.5f);
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#FFC0CB"), lineSymbol);

                            // create the Renderer
                            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
                            mainShapefileLayer.setRenderer(renderer);
                        }

                        mainShapefileLayer.setLabelsEnabled(true);
                        mapView.getMap().getOperationalLayers().add(mainShapefileLayer);
                        //mapView.setViewpointAsync(new Viewpoint(mainShapefileLayer.getFullExtent()));
                    }else{
                        Log.d(TAG, "run: failed to load");
                    }
                }
            });
        }
    }

    /**
     * 加载GDB
     */
    private void initOperationalGdbLayers(){
        String path = getOperationalLayersPath()+File.separator+"111.geodatabase";
        Log.d(TAG, "initOperationalGeoLayers: " + path);
        final Geodatabase localGdb=new Geodatabase(path);
        if(localGdb!=null) {
            localGdb.loadAsync();
            Log.d(TAG, "initOperationalGeoLayers: " + "111");
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    for (final GeodatabaseFeatureTable geodatabaseFeatureTable:localGdb.getGeodatabaseFeatureTables()){
                        FeatureLayer layer=new FeatureLayer(geodatabaseFeatureTable);
                        mapView.getMap().getOperationalLayers().add(0,layer);
                    }
                }
            });

        }
    }
    /**
     * 初始化GeoPackage图层
     */
    private void initGeoPackageLayers(){
        String path = getGeoPackagePath();
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(path,"gpkg");
        if (fileInfos==null) return;
        for (int i = 0; i < fileInfos.size(); i++) {
            FileUtils.FileInfo fileInfo = fileInfos.get(i);
            final GeoPackage geoPackage = new GeoPackage(fileInfo.FilePath);
            geoPackage.loadAsync();
            geoPackage.addDoneLoadingListener(new Runnable() {
                 @Override
                 public void run() {
                     List<GeoPackageFeatureTable> packageFeatureTables = geoPackage.getGeoPackageFeatureTables();
                     for (int j = 0; j < packageFeatureTables.size(); j++) {
                         GeoPackageFeatureTable table = packageFeatureTables.get(j);
                         FeatureLayer layer = new FeatureLayer(table);

//                         //        //标注测试
//                        StringBuilder labelDefinitionString = new StringBuilder();
//                        labelDefinitionString.append("{");
//                        labelDefinitionString.append("\"labelExpressionInfo\": {");
//                        labelDefinitionString.append("\"expression\": \"return $feature.fid;\"},");
//                        labelDefinitionString.append("\"labelPlacement\": \"esriServerPolygonPlacementAlwaysHorizontal\",");
//                        labelDefinitionString.append("\"minScale\":500000,");
//                        labelDefinitionString.append("\"symbol\": {");
//                        labelDefinitionString.append("\"color\": [0,255,50,255],");
//                        labelDefinitionString.append("\"font\": {\"size\": 14, \"weight\": \"bold\"},");
//                        labelDefinitionString.append("\"type\": \"esriTS\"}");
//                        labelDefinitionString.append("}");
//                         LabelDefinition labelDefinition = LabelDefinition.fromJson(String.valueOf(labelDefinitionString));
//                         layer.getLabelDefinitions().add(labelDefinition);
//                         layer.setLabelsEnabled(true);

//                layer.setName(table.getName()+"-gpkg");
                         mapView.getMap().getOperationalLayers().add(layer);
                     }
                 }
            });

        }
    }

    /**
     * 初始化
     */
    private void initJsonLayers(){
        String path = getJSONPath();
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(path,".json");
        if (fileInfos==null) return;
        for (int i = 0; i < fileInfos.size(); i++) {
            FileUtils.FileInfo fileInfo = fileInfos.get(i);
            String json = FileUtils.openTxt(fileInfo.FilePath,"UTF-8");

            final FeatureCollection featureCollection = FeatureCollection.fromJson(json);
            featureCollection.loadAsync();
            featureCollection.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                   List<FeatureCollectionTable> featureCollectionTable = featureCollection.getTables();
                    for (int j = 0; j < featureCollectionTable.size(); j++) {
                        FeatureCollectionTable features = featureCollectionTable.get(j);
                        FeatureLayer featureLayer = new FeatureLayer(features);
                        featureLayer.setName(features.getTableName()+"-json");
                        mapView.getMap().getOperationalLayers().add(featureLayer);
                    }
                }
            });

        }

    }

    /**
     * 获取业务图层json配置文件路径
     * @param path
     * @return
     */
    private String getOperationalLayersJsonPath(String path) {
        return projectPath+ File.separator+"OperationalLayers"+File.separator + path;
    }

    /**
     * 获取业务图层路径
     * @return
     */
    private String getOperationalLayersPath(){
        return projectPath + File.separator + "OperationalLayers" + File.separator;
    }

    /**
     * 获取Geopackage路径
     * @return
     */
    private String getGeoPackagePath(){
        return projectPath+ File.separator+"GeoPackage"+File.separator;
    }

    /**
     * 获取json路径信息
     * @return
     */
    private String getJSONPath(){
        return projectPath+ File.separator+"JSON"+File.separator;
    }

}
