package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Manager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.LabelDefinition1;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.LabelExpressionInfo;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.ShapefileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShapefilesManager {
    private static final String TAG = "ShapefilesManager";
    private Context context;
    private MapView mapView;

    private List<ShapefileInfo> shapefileInfoList =null;

    public ShapefilesManager(Context context, MapView mapView, String path) {
        this.context = context;
        this.mapView = mapView;

        JSONArray jsonArray = loadShapefilesConfig(path);
        if (jsonArray!=null){
            try {
                shapefileInfoList = getShapefiles(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取底图图层列表
     * @return
     */
    public List<ShapefileInfo> getShapefileInfos(){
        return this.shapefileInfoList;
    }

    /**
     * 加载基础底图信息
     * @param path
     * @return
     */
    private JSONArray loadShapefilesConfig(String path){
        JSONArray jsonArrBaseLayers =null;
        String JSON = FileUtils.openTxt(path,"UTF-8");
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            jsonArrBaseLayers = jsonObject.getJSONArray("shapefiles");
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
    private List<ShapefileInfo> getShapefiles(JSONArray jsonArrBaseLayers) throws JSONException {
        List<ShapefileInfo> result = null;
        int num = jsonArrBaseLayers.length();
        Log.d(TAG, "getShapefiles: 序列化 start");
        if(num>0){
            result = new ArrayList<>();
            for (int i=0;i< num;i++){
                JSONObject obj = jsonArrBaseLayers.getJSONObject(i);
                ShapefileInfo shapefileInfo = new ShapefileInfo();
                try{
                    shapefileInfo.Name = obj.getString("name");
                }catch (Exception e){

                }
                try{
                    shapefileInfo.FilePath = obj.getString("filepath");
                }catch (Exception e){

                }

                try{
                    shapefileInfo.Visible = obj.getBoolean("visible");
                }catch (Exception e){

                }

                //几何类型
                try{
                    shapefileInfo.GeometryType = obj.getString("geometrytype");
                }catch (Exception e){

                }

                //透明度
                try{
                    shapefileInfo.Opacity = obj.getDouble("opacity");
                }catch (Exception e){

                }

                //顺序号
                try{
                    shapefileInfo.LayerIndex = obj.getInt("layerindex");
                }catch (Exception e){

                }

                //符号化
                switch (shapefileInfo.GeometryType){
                    case "POLYLINE":
                        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol();
                        try{
                            JSONObject lineSymbol = obj.getJSONObject("linesymbol");

                            try {
                                //线形状
                                try {
                                    String lineStyle = lineSymbol.getString("linestyle");
                                    simpleLineSymbol.setStyle(SimpleLineSymbol.Style.valueOf(lineStyle));
                                }catch (Exception e){

                                }

                                //线颜色
                                try {
                                    JSONObject colorObject = lineSymbol.getJSONObject("color");
                                    try {
                                        int red = 0, green =0, blue =0;
                                        try {
                                            red = colorObject.getInt("red");
                                        }catch (Exception e){

                                        }

                                        try {
                                            green = colorObject.getInt("green");
                                        }catch (Exception e){

                                        }

                                        try {
                                            blue = colorObject.getInt("blue");
                                        }catch (Exception e){

                                        }
                                        simpleLineSymbol.setColor(Color.rgb(red,green,blue));
                                    }
                                    catch (Exception e){

                                    }
                                }catch (Exception e){

                                }

                                //线宽
                                try {
                                    int width = lineSymbol.getInt("width");
                                    simpleLineSymbol.setWidth(width);

                                }catch (Exception e){

                                }

                            }catch (Exception e){

                            }

                            shapefileInfo.lineSymbol = simpleLineSymbol;
                        }catch (Exception e){

                        }
                        break;
                    case "POLYGON":
                        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol();
                        try{
                            JSONObject fillSymbol = obj.getJSONObject("fillsymbol");

                            try {
                                //面填充类型
                                try {
                                    String fillstyle = fillSymbol.getString("fillstyle");
                                    simpleFillSymbol.setStyle(SimpleFillSymbol.Style.valueOf(fillstyle));
                                }catch (Exception e){

                                }

                                //面填充颜色
                                try {
                                    JSONObject fillcolor = fillSymbol.getJSONObject("fillcolor");
                                    try {
                                        int red = 0, green =0, blue =0;
                                        try {
                                            red = fillcolor.getInt("red");
                                        }catch (Exception e){

                                        }

                                        try {
                                            green = fillcolor.getInt("green");
                                        }catch (Exception e){

                                        }

                                        try {
                                            blue = fillcolor.getInt("blue");
                                        }catch (Exception e){

                                        }
                                        simpleFillSymbol.setColor(Color.rgb(red, green, blue));
                                    }
                                    catch (Exception e){

                                    }
                                }catch (Exception e){

                                }

                                //面边框
                                try {
                                    SimpleLineSymbol simpleLineSymbol1 = new SimpleLineSymbol();
                                    JSONObject outline = fillSymbol.getJSONObject("outline");

                                    //线形状
                                    try {
                                        String lineStyle = outline.getString("linestyle");
                                        simpleLineSymbol1.setStyle(SimpleLineSymbol.Style.valueOf(lineStyle));
                                    }catch (Exception e){

                                    }

                                    //线颜色
                                    try {
                                        JSONObject colorObject = outline.getJSONObject("color");
                                        try {
                                            int red = 0, green =0, blue =0;
                                            try {
                                                red = colorObject.getInt("red");
                                            }catch (Exception e){

                                            }

                                            try {
                                                green = colorObject.getInt("green");
                                            }catch (Exception e){

                                            }

                                            try {
                                                blue = colorObject.getInt("blue");
                                            }catch (Exception e){

                                            }
                                            simpleLineSymbol1.setColor(Color.rgb(red,green,blue));
                                        }
                                        catch (Exception e){

                                        }
                                    }catch (Exception e){

                                    }

                                    //线宽
                                    try {
                                        int width = outline.getInt("width");
                                        simpleLineSymbol1.setWidth(width);

                                    }catch (Exception e){

                                    }

                                    simpleFillSymbol.setOutline(simpleLineSymbol1);
                                }catch (Exception e){

                                }

                            }catch (Exception e){

                            }

                            shapefileInfo.fillSymbol = simpleFillSymbol;
                        }catch (Exception e){

                        }
                        break;
                    case "point":

                        break;
                }


                //标注
                LabelDefinition1 labelDefinition1 = new LabelDefinition1();
                try{
                    JSONObject labelDefinition = obj.getJSONObject("labelDefinition");
                    try{

                        //labelExpressionInfo
                        JSONObject labelExpressionInfoJson = labelDefinition.getJSONObject("labelExpressionInfo");
                        try{
                            String expression = labelExpressionInfoJson.getString("expression");

                            LabelExpressionInfo labelExpressionInfo1 = new LabelExpressionInfo();
                            labelExpressionInfo1.setExpression(expression);

                            labelDefinition1.setLabelExpressionInfo(labelExpressionInfo1);

                        }catch (Exception e){

                        }

                        //labelPlacement
                        try{
                            labelDefinition1.setLabelPlacement(labelDefinition.getString("labelPlacement"));
                        }catch (Exception e){

                        }

                        //minScale
                        try{
                            labelDefinition1.setMinScale(labelDefinition.getInt("minScale"));
                        }catch (Exception e){

                        }

                        //maxScale
                        try{
                            labelDefinition1.setMaxScale(labelDefinition.getInt("maxScale"));
                        }catch (Exception e){

                        }

                        //where
                        try{
                            labelDefinition1.setWhere(labelDefinition.getString("where"));
                        }catch (Exception e){

                        }

                        //symbol
                        TextSymbol textSymbol = new TextSymbol();
                        try{
                            JSONObject symbol = labelDefinition.getJSONObject("symbol");

                            //size
                            try{
                                textSymbol.setSize(symbol.getInt("size"));
                            }catch (Exception e){

                            }

                            //color
                            try{

                                JSONObject textcolor = symbol.getJSONObject("color");
                                try {
                                    int red = 0, green =0, blue =0;
                                    try {
                                        red = textcolor.getInt("red");
                                    }catch (Exception e){

                                    }

                                    try {
                                        green = textcolor.getInt("green");
                                    }catch (Exception e){

                                    }

                                    try {
                                        blue = textcolor.getInt("blue");
                                    }catch (Exception e){

                                    }
                                    textSymbol.setColor(Color.rgb(red, green, blue));
                                }
                                catch (Exception e){

                                }

                                labelDefinition1.setSymbol(textSymbol);
                            }catch (Exception e){

                            }

                        }catch (Exception e){

                        }


                    }catch (Exception e){

                    }
                }catch (Exception e){

                }

                Log.d(TAG, "getOperationLayers: 序列化 end");
                result.add(shapefileInfo);
            }
        }
        result = SortingByLayerIndex(result);//通过读取的LayerIndex排序
        return  result;
    }

    /**
     * 业务图层列表排序——正序
     * @param shapefileInfoList
     * @return
     */
    private List<ShapefileInfo> SortingByLayerIndex(List<ShapefileInfo> shapefileInfoList) {
        List<ShapefileInfo> result = null;
        if(shapefileInfoList !=null){
            result = new ArrayList<>();
            /**
             * Collections.sort(list, new PriceComparator());的第二个参数返回一个int型的值，就相当于一个标志，告诉sort方法按什么顺序来对list进行排序。
             * 按照LayerIndex从小到大排序
             */
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    ShapefileInfo p1 = (ShapefileInfo) o1;
                    ShapefileInfo p2 = (ShapefileInfo) o2;
                    if (p1.LayerIndex < p2.LayerIndex)
                        return -1;
                    else if (p1.LayerIndex == p2.LayerIndex)
                        return 0;
                    else if (p1.LayerIndex > p2.LayerIndex)
                        return                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     1;
                    return 0;
                }
            };
            Collections.sort(shapefileInfoList, comp);
            result = shapefileInfoList;
        }
        return result;
    }

}
