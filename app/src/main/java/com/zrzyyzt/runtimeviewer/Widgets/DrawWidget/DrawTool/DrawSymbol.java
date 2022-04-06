package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

//import com.esri.core.symbol.FillSymbol;
//import com.esri.core.symbol.LineSymbol;
//import com.esri.core.symbol.MarkerSymbol;
//import com.esri.core.symbol.SimpleFillSymbol;
//import com.esri.core.symbol.SimpleLineSymbol;
//import com.esri.core.symbol.SimpleMarkerSymbol;

/**
 * 要素编辑状态符号化信息
 * Created by gis-luq on 15/5/21.
 */
public class DrawSymbol {

    private static int SIZE = 12;//节点大小

    public static MarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, SIZE);
    public static TextSymbol mTextSymbol = new TextSymbol(SIZE, "", Color.RED, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
    public static SimpleMarkerSymbol mRedMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, SIZE);
    public static SimpleMarkerSymbol mBlackMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLACK, SIZE);
    public static SimpleMarkerSymbol mGreenMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, SIZE);
    public static SimpleMarkerSymbol mYelloMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, SIZE);
    public static LineSymbol mLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH_DOT, Color.RED, 2);
    public static FillSymbol mFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, Color.RED, mLineSymbol);
    public static LineSymbol mCircleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2);
    public static FillSymbol mCircleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED, mCircleLineSymbol);

    public static PictureMarkerSymbol mPinSourceSymbol;



    //    try {
//        mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable).get();
//    } catch (InterruptedException | ExecutionException e) {
//        String error = "Error creating PictureMarkerSymbol: " + e.getMessage();
//        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
//    }

}
