package com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseMap;

/**
 * 图层信息列表
 * Created by gis-luq on 2015/4/2.
 */
public class BasemapLayerInfo {

    /**
     * 支持离线底图类型
     */
    public static String LYAER_TYPE_TPK="LocalTiledPackage";//tpk
    public static String LYAER_TYPE_TIFF="LocalGeoTIFF";//tiff
    public static String LYAER_TYPE_SERVERCACHE="LocalServerCache";//server cache
    public static String LYAER_TYPE_ONLINE_TILEDLAYER="OnlineTiledMapServiceLayer";//在线切片
    public static String LYAER_TYPE_ONLINE_DYNAMICLAYER="OnlineDynamicMapServiceLayer";//在线动态图层
    public static String LYAER_TYPE_VTPK="LocalVectorTilePackage";//vtpk

    public static String LYAER_TYPE_TIANDITU_MAP="TianDiTuLayerMap";//天地图底图
    public static String LYAER_TYPE_TIANDITU_MAP_LABEL="TianDiTuLayerMapLabel";//天地图矢量注记图层
    public static String LYAER_TYPE_TIANDITU_IMAGE="TianDiTuLayerImage";//天地图影像
    public static String LYAER_TYPE_TIANDITU_IMAGE_LABEL="TianDiTuLayerImageLabel";//天地图影像标注图层

    public static String LYAER_TYPE_GOOGLE_VECTOR="GoogleMapLayerVector";//谷歌矢量
    public static String LYAER_TYPE_GOOGLE_IMAGE="GoogleMapLayerImage";//谷歌影像
    public static String LYAER_TYPE_GOOGLE_TERRAIN="GoogleMapLayerTerrain";//谷歌地形
    public static String LYAER_TYPE_GOOGLE_ROAD="GoogleMapLayerRoad";//谷歌道路图层


    public String Name;//名称
    public String Type;//类型
    public String Path;//本地路径
    public int LayerIndex;//图层顺序
    public boolean Visable;//是否可以显示
    public double Opacity;//图层透明度
    public String Render;
    public String Icon;//图标
    public String Select_Icon;//选择后的图标


}
