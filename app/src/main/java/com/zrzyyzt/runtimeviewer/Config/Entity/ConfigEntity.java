////////////////////////////////////////////////////////////////////////////////
//
//Copyright (c) 2011-2012 Esri
//
//All rights reserved under the copyright laws of the United States.
//You may freely redistribute and use this software, with or
//without modification, provided you include the original copyright
//and use restrictions.  See use restrictions in the file:
//<install location>/License.txt
//
////////////////////////////////////////////////////////////////////////////////

package com.zrzyyzt.runtimeviewer.Config.Entity;


import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.Extent;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件实体类
 */
public class ConfigEntity 
{
	private String appName = null; //应用名称
	private String runtimrKey = null;//许可信息
	private String ipAddress = null; //ip地址
	private String workspacePath = null;//工作空间路径
	private Extent extent = null;//初始化范围



	private List<WidgetEntity> mListWidget = new ArrayList<WidgetEntity>();

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getRuntimrKey() {
		return runtimrKey;
	}

	public void setRuntimrKey(String runtimrKey) {
		this.runtimrKey = runtimrKey;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Extent getExtent() {
		return extent;
	}

	public void setExtent(Extent extent) {
		this.extent = extent;
	}

	public void setListWidget(List<WidgetEntity> list)
	{
		mListWidget = list;
	}
	public List<WidgetEntity> getListWidget()
	{
		return mListWidget;
	}
	
	
}
