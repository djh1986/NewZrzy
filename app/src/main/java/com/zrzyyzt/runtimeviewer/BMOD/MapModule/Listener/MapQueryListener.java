package com.zrzyyzt.runtimeviewer.BMOD.MapModule.Listener;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.StringUtils;
import com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter.AlertLayerListAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Bean.KeyAndValueBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import gisluq.lib.Util.StringUtil;
import gisluq.lib.Util.ToastUtils;

/**
 * 属性查图点击事件
 * Created by gis-luq on 2018/4/19.
 */
public class MapQueryListener extends DefaultMapViewOnTouchListener{
    private static final String TAG = "MapQueryListener";

    private Context context;

    private View contextView;
    private TextView txtLayerName;
//    private ListView listViewField;//字段列表
    private TableView<String[]> tableView;
    private TextView closeTextView;

    private GraphicsOverlay identityGraphicOverlay;
    private boolean isOnLongpress=false;

    private MapView mapView;
    private Callout callout;
    private com.esri.arcgisruntime.geometry.Point geoClickPoint;

    private String layerName;
    public MapQueryListener(Context context, MapView mapView, View contextView) {
        super(context, mapView);
        this.context = context;
        this.contextView = contextView;
        this.txtLayerName = contextView.findViewById(R.id.map_query_result_view_1_txtLayerName);
//        this.listViewField = contextView.findViewById(R.id.widget_view_query_mapquery_1_fieldListview);
        this.tableView = contextView.findViewById(R.id.map_query_result_view_1_tableView);
        this.closeTextView = contextView.findViewById(R.id.map_query_result_view_1_closebtn);

        this.mapView = mapView;
        this.callout  = this.mapView.getCallout();

        this.identityGraphicOverlay = new GraphicsOverlay();
        this.mapView.getGraphicsOverlays().add(identityGraphicOverlay);

        this.closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    @Override
    public void onLongPress(MotionEvent e) {
        isOnLongpress =true;
        super.onLongPress(e);
    }

    @Override
    public boolean onUp(MotionEvent e) {

        if (isOnLongpress){
            identifyMapLayers(e);
        }
        isOnLongpress=false;
        return super.onUp(e);
    }

    /**
     * 地图点击查询
     * @param e
     */
    private void identifyMapLayers(MotionEvent e) {
        Point clickPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
        geoClickPoint = mapView.screenToLocation(clickPoint);
        int tolerance = 5;
        final ListenableFuture<List<IdentifyLayerResult>> identifyFuture = mMapView.identifyLayersAsync(clickPoint,tolerance,false);
        identifyFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Feature> selectFeatureList = new ArrayList<>();
                    List<IdentifyLayerResult> identifyLayersResults = identifyFuture.get();
                    for (IdentifyLayerResult identifyLayerResult : identifyLayersResults) {
                        for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                            identifyLayerResult.getLayerContent();
                            if (identifiedElement instanceof Feature) {
                                Feature identifiedFeature = (Feature) identifiedElement;
                                selectFeatureList.add(identifiedFeature);
                            }
                        }
                        if(identifyLayerResult.getSublayerResults().size()>0){
                            List<IdentifyLayerResult> sublayerResults = identifyLayerResult.getSublayerResults();
                            for (IdentifyLayerResult subResult:sublayerResults
                            ) {
                                for (GeoElement element: subResult.getElements()
                                ) {
                                    Feature identifiedFeature = (Feature) element;
                                    selectFeatureList.add(identifiedFeature);
                                }
                            }
                        }

                    }
                    selectFeature(selectFeatureList);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 用户选择要素
     * @param selectFeatureList
     */
    private void selectFeature(final List<Feature> selectFeatureList) {
        clearAllFeatureSelect();//清空选择

        int num = selectFeatureList.size();
        if (num==0){
            ToastUtils.showShort(context,"当前没有选中任何要素");
        }else if(num==1){
            FeatureTable featureTable = selectFeatureList.get(0).getFeatureTable();
//            FeatureLayer layer = selectFeatureList.get(0).getFeatureTable().getFeatureLayer();
//            String layerName = layer.getName();
            layerName = featureTable.getDisplayName();
            //Toast.makeText(context, "选择的图层为：" +layerName , Toast.LENGTH_SHORT).show();
            txtLayerName.setText(layerName);
            setFeatureSelect(selectFeatureList.get(0));
        }else{
            //当前选中要素大于1个图层
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("选择哪个图层要素？");
            //指定下拉列表的显示数据
            AlertLayerListAdapter layerListAdapter = new AlertLayerListAdapter(context,selectFeatureList);
            //设置一个下拉的列表选择项
            builder.setAdapter(layerListAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    FeatureLayer layer = selectFeatureList.get(which).getFeatureTable().getFeatureLayer();
//                    String layerName = layer.getName();
                    FeatureTable featureTable = selectFeatureList.get(which).getFeatureTable();
                    layerName = featureTable.getDisplayName();
                    //Toast.makeText(context, "当前选择图层：" +layerName , Toast.LENGTH_SHORT).show();
                    txtLayerName.setText(layerName);

                    setFeatureSelect(selectFeatureList.get(which));
                }
            });
            builder.show();
        }
    }

    /**
     * 设置要素选中
     * @param feature
     */
    public void setFeatureSelect(Feature feature) {
        //设置要素选中
//        FeatureTable featureTable = feature.getFeatureTable();
//        FeatureLayer identifiedidLayer=new FeatureLayer(featureTable);
//        identifiedidLayer.setSelectionColor(Color.YELLOW);
//        identifiedidLayer.setSelectionWidth(20);
//        identifiedidLayer.selectFeature(feature);

        identityGraphicOverlay.getGraphics().clear();
        Graphic graphic = new Graphic(feature.getGeometry(), feature.getAttributes());
        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ContextCompat.getColor(context,R.color.cyan),(float)2);
        SimpleRenderer simpleRenderer = new SimpleRenderer(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ContextCompat.getColor(context,R.color.lightblue), simpleLineSymbol));
        identityGraphicOverlay.setRenderer(simpleRenderer);
        identityGraphicOverlay.getGraphics().add(graphic);

        //设置要素属性结果
        final List<KeyAndValueBean> keyAndValueBeans = new ArrayList<>();
        final Map<String,Object> attributes= feature.getAttributes();
        final FeatureTable featureTable = feature.getFeatureTable();
        featureTable.loadAsync();
        featureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {

                List<Field> fields = featureTable.getFields();

                TextView calloutContent = new TextView(MPApplication.getContext());
//                calloutContent.setTextColor(Color.BLACK);
                calloutContent.setSingleLine(false);
                calloutContent.setVerticalScrollBarEnabled(true);
                calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                calloutContent.setMovementMethod(new ScrollingMovementMethod());
                calloutContent.setLines(5);


                for (Map.Entry<String, Object> entry:attributes.entrySet()){
                    String key=entry.getKey();
                    String alias=entry.getKey();


                    for (Field field:fields
                         ) {
                        if(field.getName().equals(key)){
                            alias = field.getAlias();
                            break;
                        }
                    }
                    Object object = entry.getValue();

                    String value ="";
                    if (object!=null){
                        if(object instanceof Double) {
                            Double d=(Double)object;
                            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                            nf.setGroupingUsed(false);
                            value=String.valueOf(nf.format(d));
                        }else {
                            value = String.valueOf(object);
                        }
                        //value = String.valueOf(object);
                    }
                    KeyAndValueBean keyAndValueBean = new KeyAndValueBean();
                    keyAndValueBean.setKey(key);
                    keyAndValueBean.setValue(value);
                    keyAndValueBean.setAlias(alias);
                    keyAndValueBeans.add(keyAndValueBean);
                }

//                String temp="";
//                //获取alias最长的entity
//                for (KeyAndValueBean entity:keyAndValueBeans
//                     ) {
//                   if(entity.getAlias().length()>temp.length()){
//                       temp = entity.getAlias();
//                   }
//                }
//                for (KeyAndValueBean entity:keyAndValueBeans
//                ) {
//                    String temp1 = entity.getAlias();
//                    int len = temp.length()-temp1.length();
//                    temp1 = String.format("%15s",temp1);
//                    calloutContent.append(entity.getAlias()+entity.getValue() + "\n");
//                }

//                AttributeAdapter attributeAdapter = new AttributeAdapter(context, keyAndValueBeans);
//                listViewField.setAdapter(attributeAdapter);

                String[] table_header = { "名称", "内容" };
                tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(context,table_header));
                tableView.setHeaderVisible(false);

                String[][] contentData =new String[keyAndValueBeans.size()][2];
                int i=0;
                for (KeyAndValueBean keyAndValueBean:keyAndValueBeans
                     ) {
                    if(keyAndValueBean.toStringArray()[0].toUpperCase().contains("OBJECTID")){

                    } else {
                        contentData[i] = keyAndValueBean.toStringArray();
                        i++;
                    }
                }

                SimpleTableDataAdapter simpleTableDataAdapter=new SimpleTableDataAdapter(context,contentData);
                simpleTableDataAdapter.setTextSize(12);
                tableView.setDataAdapter(simpleTableDataAdapter);

                callout.setLocation(geoClickPoint);
                callout.setContent(contextView);
                Callout.Style style = callout.getStyle();
                style.setBorderColor(Color.WHITE);
                style.setBorderWidth(0);
                style.setLeaderLength(100);
                style.setLeaderPosition(Callout.Style.LeaderPosition.UPPER_RIGHT_CORNER);
                callout.setStyle(style);
                callout.show();

            }
        });

    }

    /**
     * 清空所有要素选择
     */
    public void clearAllFeatureSelect(){
//        List<Layer> layers = mMapView.getMap().getOperationalLayers();
//        for (int i=0;i<layers.size();i++){
//            ArcGISTiledLayer arcGISTiledLayer = (ArcGISTiledLayer)layers.get(i);
//            FeatureTable featureTable =new ServiceFeatureTable(arcGISTiledLayer.getUri() + "/0");
//            FeatureLayer featureLayer = new FeatureLayer(featureTable);
//            featureLayer.clearSelection();
//        }
        this.identityGraphicOverlay.getGraphics().clear();
    }

    /**
     * 恢复默认状态
     */
    public void clear(){
        clearAllFeatureSelect();
//        listViewField.setAdapter(null);
//        txtLayerName.setText("未选中图层");
        if(callout!=null){
            if(callout.isShowing()){
                callout.dismiss();
            }
        }
    }

}
