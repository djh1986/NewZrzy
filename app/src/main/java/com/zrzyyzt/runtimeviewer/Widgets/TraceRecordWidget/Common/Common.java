package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class Common {
  public  static SimpleMarkerSymbol ptSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 10);
  public  static SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 5);

  public static String TraceRecordStatusStart = "Start";
  public static String TraceRecordStatusStop = "Stop";
  public static String TraceRecordStatusPause = "Pause";

}
