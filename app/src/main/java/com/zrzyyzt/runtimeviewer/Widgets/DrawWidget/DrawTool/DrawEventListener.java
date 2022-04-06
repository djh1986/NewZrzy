package com.zrzyyzt.runtimeviewer.Widgets.DrawWidget.DrawTool;


import java.io.FileNotFoundException;
import java.util.EventListener;


/**
 * @author ropp gispace@yeah.net
 * update by gis-luq
 *	定义画图事件监听接口
 */
public interface DrawEventListener extends EventListener {

	void handleDrawEvent(DrawEvent event) throws FileNotFoundException;
}
