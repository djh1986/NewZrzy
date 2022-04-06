package com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.StatisticDefinition;
import com.esri.arcgisruntime.data.StatisticRecord;
import com.esri.arcgisruntime.data.StatisticType;
import com.esri.arcgisruntime.data.StatisticsQueryParameters;
import com.esri.arcgisruntime.data.StatisticsQueryResult;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter.StFieldSpinnerAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter.StLayerSpinnerAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter.StTypeSpinnerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class StatisticsWidget extends BaseWidget {

    public View mWidgetView = null;//

    public RelativeLayout viewContent;

    private View chartView=null;

    private View tableView1=null;

    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {

        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示

        //super.showMessageBox(super.name);//显示组件名称

        super.mapView.getMap().getBasemap().getBaseLayers();
        super.mapView.getMap().getOperationalLayers();

        //super.showCenterView();
        //super.showCollectPointBtn();
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        //LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //设置widget组件显示内容
        //mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_statistics,null);
        this.context=context;
        initWidgetView();
    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();
        returnDefault();
        //super.hideCenterView();
        //super.hideCollectPointBtn();
    }


    private void  initWidgetView() {
        mWidgetView= LayoutInflater.from(super.context).inflate(R.layout.widget_view_statistics,null);
        viewContent=mWidgetView.findViewById(R.id.widget_view_statistics_result);
        chartView=mWidgetView.findViewById(R.id.widget_view_statistics_result_chart);
        tableView1=mWidgetView.findViewById(R.id.widget_view_statistics_result_table);
        final TextView txtBtnChart=mWidgetView.findViewById(R.id.widget_view_statistics_txtBtnChart);
        final TextView txtBtnTable=mWidgetView.findViewById(R.id.widget_view_statistics_txtBtnTable);

        final Spinner spinnerLayerList =mWidgetView.findViewById(R.id.widget_view_statistics_spinnerLayer);
        final Spinner spinnerFieldList=mWidgetView.findViewById(R.id.widget_view_statistics_spinnerfield);
        final Spinner spinnerTypeList=mWidgetView.findViewById(R.id.widget_view_statistics_spinnertype);
        //final TableView resultTable=mWidgetView.findViewById(R.id.widget_view_statistics_result_table);


        StLayerSpinnerAdapter stLayerSpinnerAdapter=new StLayerSpinnerAdapter(context,mapView.getMap().getOperationalLayers());
        spinnerLayerList.setAdapter(stLayerSpinnerAdapter);
        spinnerLayerList.setSelection(0);

        List<String>  typeList=new ArrayList<>();
        typeList.add("数量");
        typeList.add("面积");
        typeList.add("金额");
        StTypeSpinnerAdapter stTypeSpinnerAdapter=new StTypeSpinnerAdapter(context,typeList);
        spinnerTypeList.setAdapter(stTypeSpinnerAdapter);
        spinnerTypeList.setSelection(0);

        final View resultView=LayoutInflater.from(super.context).inflate(R.layout.widget_view_statistics_result,null);
        final PieChartView pieChartView=resultView.findViewById(R.id.widget_view_statistics_piechartview);

        final View listView=LayoutInflater.from(super.context).inflate(R.layout.widget_view_statistics_table,null);
        final SortableTableView<String[]> tableView=(SortableTableView<String[]>)listView.findViewById(R.id.widget_view_statistics_resulttableView);
        tableView.setHeaderBackgroundColor(Color.parseColor("#008577"));

        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(context, 3, 200);
        columnModel.setColumnWidth(0,60);
        columnModel.setColumnWidth(1,200);
        columnModel.setColumnWidth(2,300);
        tableView.setColumnModel(columnModel);

        final Button btnStatistics=mWidgetView.findViewById(R.id.widget_view_statistics_btnStatistics);


        final StFieldSpinnerAdapter[] stFieldSpinnerAdapter = new StFieldSpinnerAdapter[1];

        spinnerLayerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object obj=spinnerLayerList.getSelectedItem();
                if(obj!=null){
                    Layer layer=(Layer)obj;
                    if(layer instanceof ArcGISTiledLayer){
                        final ArcGISTiledLayer arcGISTiledLayer = (ArcGISTiledLayer)obj;
                        ServiceFeatureTable featureTable =new ServiceFeatureTable(arcGISTiledLayer.getUri()+"/0" );
                        setStFields(featureTable,spinnerFieldList);
                    }else if(layer instanceof FeatureLayer) {
                        final FeatureLayer featureLayer = (FeatureLayer)obj;
                        stFieldSpinnerAdapter[0] =new StFieldSpinnerAdapter(context,featureLayer.getFeatureTable().getFields());
                        spinnerFieldList.setAdapter(stFieldSpinnerAdapter[0]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewContent.getChildAt(0)!=resultView){
                    viewContent.removeAllViews();
                    viewContent.addView(resultView);
                }
                Object object=spinnerLayerList.getSelectedItem();
                if(object!=null){
                    final Layer layer=(Layer)object;

                    final com.esri.arcgisruntime.data.Field field=(com.esri.arcgisruntime.data.Field)spinnerFieldList.getSelectedItem();
                    final String sataType=(String)spinnerTypeList.getSelectedItem();
                    //textView.setText(field.getName());
                    List<StatisticDefinition> statisticDefinitions=new ArrayList<>();

                    if(sataType=="数量"){
                        statisticDefinitions.add(new StatisticDefinition(field.getName(), StatisticType.COUNT,sataType));
                    }
                    else if(sataType=="面积"){

                        statisticDefinitions.add(new StatisticDefinition("SJ", StatisticType.SUM,sataType));
                    }
                    else {
                        statisticDefinitions.add(new StatisticDefinition("CRJK", StatisticType.SUM,sataType));
                    }

                    String[]header={"序号",field.getAlias(),sataType};
                    SimpleTableHeaderAdapter simpleTableHeaderAdapter=new SimpleTableHeaderAdapter(context, header);
                    simpleTableHeaderAdapter.setTextColor(Color.WHITE);
                    simpleTableHeaderAdapter.setTextSize(12);
                    tableView.setHeaderAdapter(simpleTableHeaderAdapter);

                    StatisticsQueryParameters queryParameters=new StatisticsQueryParameters(statisticDefinitions);
                    queryParameters.getGroupByFieldNames().add(field.getName());
                    final ListenableFuture<StatisticsQueryResult> queryResultListenableFuture;
                    if(layer instanceof ArcGISTiledLayer){
                        final ArcGISTiledLayer arcGISTiledLayer=(ArcGISTiledLayer)object;
                        ServiceFeatureTable featureTable=new ServiceFeatureTable(arcGISTiledLayer.getUri()+"/0");
                        queryResultListenableFuture=featureTable.queryStatisticsAsync(queryParameters);
                    }else {
                        final FeatureLayer featureLayer=(FeatureLayer)object;
                        queryResultListenableFuture=featureLayer.getFeatureTable().queryStatisticsAsync(queryParameters);
                    }

                    queryResultListenableFuture.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StatisticsQueryResult statisticsQueryResult = queryResultListenableFuture.get();
                                Iterator<StatisticRecord> statisticRecordIterator=statisticsQueryResult.iterator();
                                List<String[]> data=new ArrayList<>();
                                List<SliceValue> values=new ArrayList<>();
                                int index=1;
                                while (statisticRecordIterator.hasNext()){
                                    StatisticRecord statisticRecord=statisticRecordIterator.next();
                                    if(statisticRecord.getGroup().isEmpty()){
                                        for (Map.Entry<String,Object> stat:statisticRecord.getStatistics().entrySet()){
                                            String strValue=stat.getKey()+":"+String.format(Locale.CHINESE,"%,.0f",(Double)stat.getValue());
                                        }
                                    }else {

                                        for (Map.Entry<String, Object> group : statisticRecord.getGroup().entrySet()) {
                                            for (Map.Entry<String, Object> stat : statisticRecord.getStatistics().entrySet()) {
                                                double value= (Double) stat.getValue();
                                                values.add(new SliceValue((float)value,randomColor()).setLabel(group.getValue().toString()));
                                                if(sataType=="数量"){
                                                    data.add(new String[]{String.valueOf(index),group.getValue().toString(),String.valueOf((int)value)});
                                                }
                                                else {
                                                    data.add(new String[]{String.valueOf(index),group.getValue().toString(),String.format("%.2f",value)});
                                                }
                                                index=index+1;
                                            }
                                        }
                                    }
                                }
                                initPieChart(pieChartView,values);
                                if(data.size()>0 ){
                                    SimpleTableDataAdapter simpleTableDataAdapter=new SimpleTableDataAdapter(context,data);
                                    simpleTableDataAdapter.setTextSize(12);
                                    tableView.setDataAdapter(simpleTableDataAdapter);
                                }

                            }
                            catch (Exception e) {
                                Log.e("统计发生错误","错误原因："+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        txtBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewContent.getChildAt(0)!=resultView)
                {
                    viewContent.removeAllViews();
                    viewContent.addView(resultView);
                    tableView1.setVisibility(View.GONE);
                    chartView.setVisibility(View.VISIBLE);
                }
            }
        });

        txtBtnTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewContent.getChildAt(0)!=listView)
                {
                    viewContent.removeAllViews();
                    viewContent.addView(listView);
                    chartView.setVisibility(View.GONE);
                    tableView1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setStFields(final ServiceFeatureTable serviceFeatureTable,final Spinner fieldList) {
        serviceFeatureTable.loadAsync();
        serviceFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                List<Field> list=serviceFeatureTable.getFields();
                if(list.size()<0){
                    return;
                }

                List<Field>fields=new ArrayList<>();
                for (Field field:list) {
                    if(!field.getName().contains("OBJECTID")){
                        fields.add(field);
                    }
                }

                final StFieldSpinnerAdapter[] stFieldSpinnerAdapter = new StFieldSpinnerAdapter[1];
                stFieldSpinnerAdapter[0] =new StFieldSpinnerAdapter(context,fields);
                fieldList.setAdapter(stFieldSpinnerAdapter[0]);
            }
        });
    }

    private int randomColor(){

        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return Color.rgb(r,g,b);
    }

    private void initPieChart(PieChartView pieChartView,List<SliceValue>values){

        PieChartData pieChartData=new PieChartData(values);
        /*****************************饼中文字设置************************************/
        //是否显示文本内容(默认为false)
        pieChartData.setHasLabels(true);
        //是否点击饼模块才显示文本（默认为false,为true时，setHasLabels(true)无效）
//		pieChartData.setHasLabelsOnlyForSelected(true);
        //文本内容是否显示在饼图外侧(默认为false)
        pieChartData.setHasLabelsOutside(false);
        //文本字体大小
        pieChartData.setValueLabelTextSize(10);
        //文本文字颜色
        pieChartData.setValueLabelsTextColor(Color.WHITE);
        //设置文本背景颜色
        pieChartData.setValueLabelBackgroundColor(Color.RED);
        //设置文本背景颜色时，必须设置自动背景为false
        pieChartData.setValueLabelBackgroundAuto(false);
        //设置是否有文字背景
        pieChartData.setValueLabelBackgroundEnabled(false);

        /*****************************中心圆设置************************************/
        //饼图是空心圆环还是实心饼状（默认false,饼状）
        pieChartData.setHasCenterCircle(true);
        //中心圆的颜色（需setHasCenterCircle(true)，因为只有圆环才能看到中心圆）
        pieChartData.setCenterCircleColor(Color.WHITE);
        //中心圆所占饼图比例（0-1）
        pieChartData.setCenterCircleScale(0.2f);

        //饼图各模块的间隔(默认为0)
        pieChartData.setSlicesSpacing(3);

        pieChartView.setPieChartData(pieChartData);
        //整个饼图所占视图比例（0-1）
        pieChartView.setCircleFillRatio(1.0f);
        //饼图是否可以转动（默认为true）
        pieChartView.setChartRotationEnabled(true);

    }

    private void returnDefault(){
        viewContent.removeAllViews();
        viewContent.refreshDrawableState();
        mWidgetView.refreshDrawableState();
    }

}
