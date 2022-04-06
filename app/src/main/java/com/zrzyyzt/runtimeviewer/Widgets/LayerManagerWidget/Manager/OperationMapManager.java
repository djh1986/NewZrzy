package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Manager;

import android.content.Context;
import android.util.Log;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.OperationLayerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OperationMapManager {
    private static final String TAG = "OperationMapManager";
    private Context context;
    private MapView mapView;

    private List<OperationLayerInfo> operationLayerInfoList =null;

    public OperationMapManager(Context context, MapView mapView, String path) {
        this.context = context;
        this.mapView = mapView;

        JSONArray jsonArray = loadBaseMapConfig(path);
        if (jsonArray!=null){
            try {
                operationLayerInfoList = getOperationLayers(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取底图图层列表
     * @return
     */
    public List<OperationLayerInfo> getOperationLayerInfos(){
        return this.operationLayerInfoList;
    }

    /**
     * 加载基础底图信息
     * @param path
     * @return
     */
    private JSONArray loadBaseMapConfig(String path){
        JSONArray jsonArrBaseLayers =null;
        String JSON = FileUtils.openTxt(path,"GB2312");
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            jsonArrBaseLayers = jsonObject.getJSONArray("operationlayers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArrBaseLayers;
    }

    /**
     * 解析基础底图列表信息
     * @param jsonArrBaseLayers
     * @return
     * @throws JSONException
     */
    private List<OperationLayerInfo> getOperationLayers(JSONArray jsonArrBaseLayers) throws JSONException {
        List<OperationLayerInfo> result = null;
        int num = jsonArrBaseLayers.length();
        Log.d(TAG, "getOperationLayers: 序列化 start");
        if(num>0){
            result = new ArrayList<>();
            for (int i=0;i< num;i++){
                JSONObject obj = jsonArrBaseLayers.getJSONObject(i);
                OperationLayerInfo operationLayerInfo = new OperationLayerInfo();
                try{
                    operationLayerInfo.Name = obj.getString("name");
                }catch (Exception e){

                }
                try{
                    operationLayerInfo.Type = obj.getString("type");
                }catch (Exception e){

                }
                try{
                    operationLayerInfo.Path = obj.getString("path");
                }catch (Exception e){

                }

                try{
                    operationLayerInfo.LayerIndex = obj.getInt("layerIndex");
                }catch (Exception e){

                }
                try{
                    operationLayerInfo.Visable = obj.getBoolean("visable");
                }catch (Exception e){

                }
                try{
                    operationLayerInfo.Opacity = obj.getDouble("opacity");
                }catch (Exception e){

                }
                Log.d(TAG, "getOperationLayers: 序列化 end");
                result.add(operationLayerInfo);
            }
        }
        result = SortingByLayerIndex(result);//通过读取的LayerIndex排序
        return  result;
    }

    /**
     * 业务图层列表排序——正序
     * @param operationLayerInfoList
     * @return
     */
    private List<OperationLayerInfo> SortingByLayerIndex(List<OperationLayerInfo> operationLayerInfoList) {
        List<OperationLayerInfo> result = null;
        if(operationLayerInfoList !=null){
            result = new ArrayList<>();
            /**
             * Collections.sort(list, new PriceComparator());的第二个参数返回一个int型的值，就相当于一个标志，告诉sort方法按什么顺序来对list进行排序。
             * 按照LayerIndex从小到大排序
             */
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    OperationLayerInfo p1 = (OperationLayerInfo) o1;
                    OperationLayerInfo p2 = (OperationLayerInfo) o2;
                    if (p1.LayerIndex < p2.LayerIndex)
                        return -1;
                    else if (p1.LayerIndex == p2.LayerIndex)
                        return 0;
                    else if (p1.LayerIndex > p2.LayerIndex)
                        return                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     1;
                    return 0;
                }
            };
            Collections.sort(operationLayerInfoList, comp);
            result = operationLayerInfoList;
        }
        return result;
    }
}
