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
     * ???????????????????????????????????????
     * ?????????widget?????????, WidgetManager?????????????????????????????????????????????????????????.
     * ???????????????????????? "inactive" ??????
     */
    @Override
    public void active() {

        super.active();//?????????????????????????????????????????????widget?????????widget??????????????????inactive()???????????????
        super.showWidget(mWidgetView);//??????UI?????????

        //super.showMessageBox(super.name);//??????????????????

        super.mapView.getMap().getBasemap().getBaseLayers();
        super.mapView.getMap().getOperationalLayers();

        //super.showCenterView();
        //super.showCollectPointBtn();
    }

    /**
     * widget???????????????????????????????????????view??????????????????
     * ?????????????????????????????????????????????
     */
    @Override
    public void create() {
        //LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //??????widget??????????????????
        //mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_statistics,null);
        this.context=context;
        initWidgetView();
    }

    /**
     * ???????????????????????????????????????
     * ???????????????????????? "inactive" ??????
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
        typeList.add("??????");
        typeList.add("??????");
        typeList.add("??????");
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

                    if(sataType=="??????"){
                        statisticDefinitions.add(new StatisticDefinition(field.getName(), StatisticType.COUNT,sataType));
                    }
                    else if(sataType=="??????"){

                        statisticDefinitions.add(new StatisticDefinition("SJ", StatisticType.SUM,sataType));
                    }
                    else {
                        statisticDefinitions.add(new StatisticDefinition("CRJK", StatisticType.SUM,sataType));
                    }

                    String[]header={"??????",field.getAlias(),sataType};
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
                                                if(sataType=="??????"){
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
                                Log.e("??????????????????","???????????????"+e.getMessage());
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
        /*****************************??????????????????************************************/
        //????????????????????????(?????????false)
        pieChartData.setHasLabels(true);
        //????????????????????????????????????????????????false,???true??????setHasLabels(true)?????????
//		pieChartData.setHasLabelsOnlyForSelected(true);
        //???????????????????????????????????????(?????????false)
        pieChartData.setHasLabelsOutside(false);
        //??????????????????
        pieChartData.setValueLabelTextSize(10);
        //??????????????????
        pieChartData.setValueLabelsTextColor(Color.WHITE);
        //????????????????????????
        pieChartData.setValueLabelBackgroundColor(Color.RED);
        //?????????????????????????????????????????????????????????false
        pieChartData.setValueLabelBackgroundAuto(false);
        //???????????????????????????
        pieChartData.setValueLabelBackgroundEnabled(false);

        /*****************************???????????????************************************/
        //????????????????????????????????????????????????false,?????????
        pieChartData.setHasCenterCircle(true);
        //????????????????????????setHasCenterCircle(true)?????????????????????????????????????????????
        pieChartData.setCenterCircleColor(Color.WHITE);
        //??????????????????????????????0-1???
        pieChartData.setCenterCircleScale(0.2f);

        //????????????????????????(?????????0)
        pieChartData.setSlicesSpacing(3);

        pieChartView.setPieChartData(pieChartData);
        //?????????????????????????????????0-1???
        pieChartView.setCircleFillRatio(1.0f);
        //????????????????????????????????????true???
        pieChartView.setChartRotationEnabled(true);

    }

    private void returnDefault(){
        viewContent.removeAllViews();
        viewContent.refreshDrawableState();
        mWidgetView.refreshDrawableState();
    }

}
