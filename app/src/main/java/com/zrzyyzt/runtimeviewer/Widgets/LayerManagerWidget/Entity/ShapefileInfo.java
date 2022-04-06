package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity;

import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class ShapefileInfo {

    public static String GEOMETRY_TYPE_POLYLINE = "POLYLINE";
    public static String GEOMETRY_TYPE_POLYGON = "POLYGON";
    public static String GEOMETRY_TYPE_POINT = "POINT";

    public String Name;//名称
    public String FilePath;//本地路径
    public boolean Visible;//是否可见
    public String GeometryType;//类型
    public Integer LayerIndex;//图层顺序
    public double Opacity;//图层透明度
    public SimpleLineSymbol lineSymbol; //线符号化
    public SimpleFillSymbol fillSymbol; //面符号
    public SimpleMarkerSymbol markerSymbol; //点符号
    public LabelDefinition1 labelDefinition1; //标注
}
