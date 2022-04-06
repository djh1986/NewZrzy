package com.zrzyyzt.runtimeviewer.Widgets.QueryWidget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter.FieldSpinnerAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter.LayerSpinnerAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter.QueryResultAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import gisluq.lib.Util.ToastUtils;

/**
 * 属性查询组件-图查属性，属性查图
 * Created by gis-luq on 2018/3/10.
 */
public class QueryWidget extends BaseWidget {

    private static String TAG = "QueryWidget";
//    private View.OnTouchListener defauleOnTouchListener;//默认点击事件
//    private MapQueryOnTouchListener mapQueryOnTouchListener;//要素选择事件

    public View mWidgetView = null;//

    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {
        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示
        Log.d(TAG, "active: initMapQuery start");
//        initMapQuery();
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        this.context = context;
//        defauleOnTouchListener = super.mapView.getOnTouchListener();
        initWidgetView();//初始化UI
    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();
        returnDefault();
    }

    /**
     * 初始化UI
     */
    private void initWidgetView() {
        /**
         * **********************************************************************************
         * 布局容器
         */
        //设置widget组件显示内容
        mWidgetView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query,null);
//        final TextView txtMapQueryBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_query_txtBtnMapQuery);
//        final View viewMapQuerySelect = mWidgetView.findViewById(R.id.widget_view_query_viewMapQuery);
//        TextView txtAttributeQueryBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_query_txtBtnAttributeQuery);
//        final View viewAttributeSelect = mWidgetView.findViewById(R.id.widget_view_query_viewAttributeQuery);
        final RelativeLayout viewContent = mWidgetView.findViewById(R.id.widget_view_query_contentView);//内容区域

        /**
         * **********************************************************************************
         * 图查属性
         */
//        final View mapQueryView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query_mapquery,null);
//        View viewBtnSelectFeature = mapQueryView.findViewById(R.id.widget_view_query_mapquery_linerBtnFeatureSelect);//要素选择
//        TextView txtLayerName = (TextView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_txtLayerName);
//        ListView listViewField = (ListView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_fieldListview);
//        mapQueryOnTouchListener = new MapQueryOnTouchListener(context,mapView,txtLayerName,listViewField);

        /**
         * **********************************************************************************
         * 属性查图
         */
        final View attributeQueryView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query_attributequery,null);
        final Spinner spinnerLayerList = (Spinner)attributeQueryView.findViewById(R.id.widget_view_query_attribute_spinnerLayer);
        final Spinner spinnerFieldList = attributeQueryView.findViewById(R.id.widget_view_query_attribute_spinner_field);
        final TextView txtQueryInfo = (TextView)attributeQueryView.findViewById(R.id.widget_view_query_attributequery_txtQueryInfo);
        Button btnQuery = (Button)attributeQueryView.findViewById(R.id.widget_view_query_attribute_btnQuery);
        final ListView resultListview = (ListView)attributeQueryView.findViewById(R.id.widget_view_query_attribute_resultListview);

        LayerSpinnerAdapter layerSpinnerAdapter = new LayerSpinnerAdapter(context,mapView.getMap().getOperationalLayers());
        spinnerLayerList.setAdapter(layerSpinnerAdapter);

        final FieldSpinnerAdapter[] fieldSpinnerAdapter = new FieldSpinnerAdapter[1];
        spinnerLayerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取查询图层
                Object obj= spinnerLayerList.getSelectedItem();
                if (obj!=null){
                    Layer layer=(Layer)obj;
                    if(layer instanceof ArcGISTiledLayer){
                        final ArcGISTiledLayer arcGISTiledLayer = (ArcGISTiledLayer)obj;
                        final ServiceFeatureTable featureTable =new ServiceFeatureTable(arcGISTiledLayer.getUri() + "/0");
                        featureTable.loadAsync();
                        featureTable.addDoneLoadingListener(new Runnable() {
                            @Override
                            public void run() {
                                if(featureTable.getLoadStatus() == LoadStatus.LOADED){
                                    List<Field> list=featureTable.getFields();
                                    if(list.size()<0){
                                        return;
                                    }
                                    fieldSpinnerAdapter[0] = new FieldSpinnerAdapter(context,list);
                                    spinnerFieldList.setAdapter(fieldSpinnerAdapter[0]);
                                }
                            }
                        });

                    }else if(layer instanceof FeatureLayer){
                        final FeatureLayer featureLayer = (FeatureLayer)obj;
                        List<Field> fields = featureLayer.getFeatureTable().getFields();
                        fieldSpinnerAdapter[0] =new FieldSpinnerAdapter(context,fields );
                        spinnerFieldList.setAdapter(fieldSpinnerAdapter[0]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ToastUtils.showLong(context,"请选择图层");
            }
        });

        //属性查图
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object object=spinnerLayerList.getSelectedItem();
                if(object!=null) {
                    final Layer layer = (Layer) object;
                    final com.esri.arcgisruntime.data.Field field = (com.esri.arcgisruntime.data.Field) spinnerFieldList.getSelectedItem();
                    //获取模糊查询关键字
                    String search= txtQueryInfo.getText().toString();
                    if(layer instanceof ArcGISTiledLayer){
                        final ArcGISTiledLayer arcGISTiledLayer = (ArcGISTiledLayer)object;
                        final ServiceFeatureTable featureTable =new ServiceFeatureTable(arcGISTiledLayer.getUri() + "/0");
                        queryAttributeWeb(featureTable, field, search, resultListview);
                    }else if(layer instanceof FeatureLayer){
                        final FeatureLayer featureLayer = (FeatureLayer)object;
                        FeatureTable featureTable = featureLayer.getFeatureTable();
                        queryAttribute(featureTable, field, search, resultListview);
                    }

                }
            }
        });


        /**
         * **********************************************************************************
         * 布局容器事件
         */
        viewContent.addView(attributeQueryView);//默认显示属性查图

//        txtMapQueryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewContent.getChildAt(0)!=mapQueryView){
//                    viewContent.removeAllViews();
//                    viewContent.addView(mapQueryView);
//                    viewMapQuerySelect.setVisibility(View.VISIBLE);
//                    viewAttributeSelect.setVisibility(View.GONE);
//                    initMapQuery();//初始化属性查图
//                }
//            }
//        });
//        txtAttributeQueryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewContent.getChildAt(0)!=attributeQueryView){
//                    viewContent.removeAllViews();
//                    viewContent.addView(attributeQueryView);
//                    viewMapQuerySelect.setVisibility(View.GONE);
//                    viewAttributeSelect.setVisibility(View.VISIBLE);
//                    returnDefault();//还原默认状态
//                }
//            }
//        });

    }

    /**
     *  属性查询
     * @param featureTable
     * @param search
     * @param resultListview
     */
    private void queryAttributeWeb(final ServiceFeatureTable featureTable,final Field field, final String search, final ListView resultListview) {
//        final FeatureLayer mainFeatureLayer = featureLayer;
//        mainFeatureLayer.setSelectionWidth(15);
//        mainFeatureLayer.setSelectionColor(Color.YELLOW);
        final StringBuilder stringBuilder = new StringBuilder();
        final QueryParameters query = new QueryParameters();

        boolean isNumber = isNumberFunction(search);

        switch (field.getFieldType()) {
            case TEXT:
                stringBuilder.append(" upper(");
                stringBuilder.append(field.getName());
                stringBuilder.append(") LIKE '%");
                stringBuilder.append(search.toUpperCase());
                stringBuilder.append("%'");
                break;
            case SHORT:
            case INTEGER:
            case FLOAT:
            case DOUBLE:
            case OID:
                if (isNumber == true) {
                    stringBuilder.append(field.getName());
                    stringBuilder.append(" = ");
                    stringBuilder.append(search);
                }
                break;
            case UNKNOWN:
            case GLOBALID:
            case BLOB:
            case GEOMETRY:
            case RASTER:
            case XML:
            case GUID:
            case DATE:
                break;
        }

        String whereStr = stringBuilder.toString();
        query.setWhereClause(whereStr);
        final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = featureTable.queryFeaturesAsync(query);
        featureQueryResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {

                    List<Feature> mapQueryResult = new ArrayList<>();//查询统计结果

                    FeatureQueryResult result = featureQueryResult.get();
                    Iterator<Feature> iterator = result.iterator();
                    Feature feature;
                    while (iterator.hasNext()) {
                        feature = iterator.next();
                        mapQueryResult.add(feature);
                    }

                    ToastUtils.showShort(context,"查询出"+mapQueryResult.size()+"个符合要求的结果");
                    QueryResultAdapter queryResultAdapter = new QueryResultAdapter(context,mapQueryResult,mapView);
                    resultListview.setAdapter(queryResultAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        String whereStr = GetWhereStrFunction(featureTable,search);
//        featureTable.loadAsync();
//        featureTable.addDoneLoadingListener(new Runnable() {
//            @Override
//            public void run() {
//
//                List<Field> fields = featureTable.getFields();
////                Log.d(TAG, "GetWhereStrFunction: field size" + fields.size());
//                if(fields.size()<=0) {
//                    return;
//                }
//                boolean isNumber = isNumberFunction(search);
//                for (Field field : fields) {
//                    switch (field.getFieldType()) {
//                        case TEXT:
//                            stringBuilder.append(" upper(");
//                            stringBuilder.append(field.getName());
//                            stringBuilder.append(") LIKE '%");
//                            stringBuilder.append(search.toUpperCase());
//                            stringBuilder.append("%' or");
//                            break;
//                        case SHORT:
//                        case INTEGER:
//                        case FLOAT:
//                        case DOUBLE:
//                        case OID:
//                            if(isNumber == true)
//                            {
//                                stringBuilder.append(" upper(");
//                                stringBuilder.append(field.getName());
//                                stringBuilder.append(") = ");
//                                stringBuilder.append(search);
//                                stringBuilder.append(" or");
//                            }
//                            break;
//                        case UNKNOWN:
//                        case GLOBALID:
//                        case BLOB:
//                        case GEOMETRY:
//                        case RASTER:
//                        case XML:
//                        case GUID:
//                        case DATE:
//                            break;
//                    }
//                }
//                //删除最后一个or
//                String whereStr = stringBuilder.toString();
//                int indexOf = whereStr.lastIndexOf("or");
//                whereStr = whereStr.substring(0,indexOf);
//                query.setWhereClause(whereStr);
////                Log.d(TAG, "queryAttribute: " + whereStr);
//                final ListenableFuture<FeatureQueryResult> featureQueryResult
//                        = featureTable.queryFeaturesAsync(query);
//                featureQueryResult.addDoneListener(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//
//                            List<Feature> mapQueryResult = new ArrayList<>();//查询统计结果
//
//                            FeatureQueryResult result = featureQueryResult.get();
//                            Iterator<Feature> iterator = result.iterator();
//                            Feature feature;
//                            while (iterator.hasNext()) {
//                                feature = iterator.next();
//                                mapQueryResult.add(feature);
//                            }
//
//                            ToastUtils.showShort(context,"查询出"+mapQueryResult.size()+"个符合要求的结果");
//                            QueryResultAdapter queryResultAdapter = new QueryResultAdapter(context,mapQueryResult,mapView);
//                            resultListview.setAdapter(queryResultAdapter);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });


    }

    private void queryAttribute(final FeatureTable featureTable, final Field field, final String search, final ListView resultListview) {
//        final FeatureLayer mainFeatureLayer = featureLayer;
//        mainFeatureLayer.setSelectionWidth(15);
//        mainFeatureLayer.setSelectionColor(Color.YELLOW);
        final StringBuilder stringBuilder = new StringBuilder();
        final QueryParameters query = new QueryParameters();

        boolean isNumber = isNumberFunction(search);

        switch (field.getFieldType()) {
            case TEXT:
                stringBuilder.append(" upper(");
                stringBuilder.append(field.getName());
                stringBuilder.append(") LIKE '%");
                stringBuilder.append(search.toUpperCase());
                stringBuilder.append("%'");
                break;
            case SHORT:
            case INTEGER:
            case FLOAT:
            case DOUBLE:
            case OID:
                if (isNumber == true) {
                    stringBuilder.append(field.getName());
                    stringBuilder.append(" = ");
                    stringBuilder.append(search);
                }
                break;
            case UNKNOWN:
            case GLOBALID:
            case BLOB:
            case GEOMETRY:
            case RASTER:
            case XML:
            case GUID:
            case DATE:
                break;
        }

        String whereStr = stringBuilder.toString();
        query.setWhereClause(whereStr);
        final ListenableFuture<FeatureQueryResult> featureQueryResult
                = featureTable.queryFeaturesAsync(query);
        featureQueryResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {

                    List<Feature> mapQueryResult = new ArrayList<>();//查询统计结果

                    FeatureQueryResult result = featureQueryResult.get();
                    Iterator<Feature> iterator = result.iterator();
                    Feature feature;
                    while (iterator.hasNext()) {
                        feature = iterator.next();
                        mapQueryResult.add(feature);
                    }

                    ToastUtils.showShort(context,"查询出"+mapQueryResult.size()+"个符合要求的结果");
                    QueryResultAdapter queryResultAdapter = new QueryResultAdapter(context,mapQueryResult,mapView);
                    resultListview.setAdapter(queryResultAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        String whereStr = GetWhereStrFunction(featureTable,search);
//        featureTable.loadAsync();
//        featureTable.addDoneLoadingListener(new Runnable() {
//            @Override
//            public void run() {
//
//                List<Field> fields = featureTable.getFields();
////                Log.d(TAG, "GetWhereStrFunction: field size" + fields.size());
//                if(fields.size()<=0) {
//                    return;
//                }
//                boolean isNumber = isNumberFunction(search);
//                for (Field field : fields) {
//                    switch (field.getFieldType()) {
//                        case TEXT:
//                            stringBuilder.append(" upper(");
//                            stringBuilder.append(field.getName());
//                            stringBuilder.append(") LIKE '%");
//                            stringBuilder.append(search.toUpperCase());
//                            stringBuilder.append("%' or");
//                            break;
//                        case SHORT:
//                        case INTEGER:
//                        case FLOAT:
//                        case DOUBLE:
//                        case OID:
//                            if(isNumber == true)
//                            {
//                                stringBuilder.append(field.getName());
//                                stringBuilder.append(" = ");
//                                stringBuilder.append(search);
//                            }
//                            break;
//                        case UNKNOWN:
//                        case GLOBALID:
//                        case BLOB:
//                        case GEOMETRY:
//                        case RASTER:
//                        case XML:
//                        case GUID:
//                        case DATE:
//                            break;
//                    }
//                }
//                //删除最后一个or
//                String whereStr = stringBuilder.toString();
//                int indexOf = whereStr.lastIndexOf("or");
//                whereStr = whereStr.substring(0,indexOf);
//                query.setWhereClause(whereStr);
////                Log.d(TAG, "queryAttribute: " + whereStr);
//                final ListenableFuture<FeatureQueryResult> featureQueryResult
//                        = featureTable.queryFeaturesAsync(query);
//                featureQueryResult.addDoneListener(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//
//                            List<Feature> mapQueryResult = new ArrayList<>();//查询统计结果
//
//                            FeatureQueryResult result = featureQueryResult.get();
//                            Iterator<Feature> iterator = result.iterator();
//                            Feature feature;
//                            while (iterator.hasNext()) {
//                                feature = iterator.next();
//                                mapQueryResult.add(feature);
//                            }
//
//                            ToastUtils.showShort(context,"查询出"+mapQueryResult.size()+"个符合要求的结果");
//                            QueryResultAdapter queryResultAdapter = new QueryResultAdapter(context,mapQueryResult,mapView);
//                            resultListview.setAdapter(queryResultAdapter);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });


    }


    /**
     * 判断是否为数字
     * @param string
     * @return
     */
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

    /**
     * 初始化图查属性
     */
    private void initMapQuery() {
//        mapView.setMagnifierEnabled(true);//放大镜
//        if (mapQueryOnTouchListener!=null){
//            super.mapView.setOnTouchListener(mapQueryOnTouchListener);
//        }
//        mapQueryOnTouchListener.clear();//清空当前选择
    }

    /**
     * 恢复默认状态
     */
    private void returnDefault() {
//        if (mapQueryOnTouchListener!=null){
//            super.mapView.setOnTouchListener(defauleOnTouchListener);//窗口关闭恢复默认点击状态
//        }
//        mapQueryOnTouchListener.clear();//清空当前选择
//        mapView.setMagnifierEnabled(false);//放大镜
    }

}

