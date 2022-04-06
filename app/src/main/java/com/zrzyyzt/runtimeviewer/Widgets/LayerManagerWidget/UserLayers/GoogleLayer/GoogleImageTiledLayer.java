package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.UserLayers.GoogleLayer;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ImageTiledLayer;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoogleImageTiledLayer extends ImageTiledLayer {
    // 枚举
    public enum MapType{
        VECTOR,        //矢量标注地图
        IMAGE,         //影像地图
        TERRAIN,       //地形
        ROAD           //道路标注图层
    }

    public static Envelope FullExtent = new Envelope(-22041257.773878,
            -32673939.6727517, 22041257.773878, 20851350.0432886, SpatialReference.create(102113));

    private static GoogleImageTiledLayer googleMapLayer;

    private static TileInfo mTileInfo;
    private MapType mMapType;
    public static double[] iScale =
            {
                    591657527.591555,
                    295828763.795778,
                    147914381.897889,
                    73957190.9489444,
                    36978595.4744722,
                    18489297.7372361,
                    9244648.86861805,
                    4622324.43430902,
                    2311162.21715451,
                    1155581.10857726,
                    577790.554288628,
                    288895.277144314,
                    144447.638572157,
                    72223.8192860785,
                    36111.9096430392,
                    18055.9548215196,
                    9027.97741075981,
                    4513.98870537991,
                    2256.99435268995,
                    1128.49717634498,
                    564.24858817249,
                    282.124294086245,
                    141.0621470431225
            };
    public static double[] iRes =
            {
                    156543.033928023,
                    78271.5169640117,
                    39135.7584820059,
                    19567.8792410029,
                    9783.93962050147,
                    4891.96981025073,
                    2445.98490512537,
                    1222.99245256268,
                    611.496226281342,
                    305.748113140671,
                    152.874056570335,
                    76.4370282851677,
                    38.2185141425838,
                    19.1092570712919,
                    9.55462853564596,
                    4.77731426782298,
                    2.38865713391149,
                    1.19432856695575,
                    0.597164283477873,
                    0.298582141738936,
                    0.1492910708694683,
                    0.07464553543473415,
                    0.037322767717367075
            };


    public GoogleImageTiledLayer(MapType mapType, TileInfo tileInfo, Envelope fullExtent) {
        super(tileInfo, fullExtent);
        this.mMapType = mapType;

//        setBufferSize(BufferSize.MEDIUM);

    }

    public static GoogleImageTiledLayer getInstance(MapType mapType, TileInfo tileInfo, Envelope fullExtent){

        if (googleMapLayer==null){
            googleMapLayer=new GoogleImageTiledLayer(mapType,tileInfo,fullExtent);
        }
        return googleMapLayer;
    }
    private void initLayer() {

    }

    @Override
    protected byte[] getTile(TileKey tileKey) {
        byte[] iResult = null;
        String tielPath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/CollectorMap/TitlCache/"+mMapType+"/";

        File file=new File(tielPath);
        if(!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        try {
            if(FileUtils.isExist(tielPath)){
                iResult=getOfflineCacheFile(tielPath,tileKey.getLevel(),tileKey.getColumn(),tileKey.getRow());
            }
            if(iResult == null) {
                URL iURL = null;
                byte[] iBuffer = new byte[1024];
                HttpURLConnection iHttpURLConnection = null;
                BufferedInputStream iBufferedInputStream = null;
                ByteArrayOutputStream iByteArrayOutputStream = null;

                iURL = new URL(this.getMapUrl(tileKey));
                iHttpURLConnection = (HttpURLConnection) iURL.openConnection();
                iHttpURLConnection.connect();
                iBufferedInputStream = new BufferedInputStream(iHttpURLConnection.getInputStream());
                iByteArrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int iLength = iBufferedInputStream.read(iBuffer);
                    if (iLength > 0) {
                        iByteArrayOutputStream.write(iBuffer, 0, iLength);
                    } else {
                        break;
                    }
                }
                iBufferedInputStream.close();
                iHttpURLConnection.disconnect();

                iResult = iByteArrayOutputStream.toByteArray();

                if(FileUtils.isExist(tielPath)){
                    //AddOfflineCacheFile(tielPath, tileKey.getLevel(), tileKey.getColumn(), tileKey.getRow(), iResult);
                }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iResult;
    }

    private String getMapUrl(TileKey tileKey) {
        String iResult = null;
        Random iRandom = null;
        int level=tileKey.getLevel();
        int col=tileKey.getColumn();
        int row=tileKey.getRow();
        iResult = "http://mt";
        iRandom = new Random();
        iResult = iResult + iRandom.nextInt(4);
        switch (this.mMapType) {
            case VECTOR:
                iResult = iResult + ".google.cn/vt/lyrs=m@212000000&hl=zh-CN&gl=CN&src=app&x=" + col + "&y=" + row + "&z=" + level + "&s==Galil";
                break;
            case IMAGE:
                iResult = iResult + ".google.cn/vt/lyrs=s@126&hl=zh-CN&gl=CN&src=app&x=" + col + "&y=" + row + "&z=" + level + "&s==Galil";
                break;
            case TERRAIN:
                iResult = iResult + ".google.cn/vt/lyrs=t@131,r@227000000&hl=zh-CN&gl=CN&src=app&x=" + col + "&y=" + row + "&z=" + level + "&s==Galil";
                break;
            case ROAD:
                iResult = iResult + ".google.cn/vt/imgtp=png32&lyrs=h@212000000&hl=zh-CN&gl=CN&src=app&x=" + col + "&y=" + row + "&z=" + level + "&s==Galil";
                break;
            default:
                return null;
        }
        return iResult;
    }

    @Override
    public TileInfo getTileInfo() {
        return mTileInfo;
    }

    public static TileInfo buildTileInfo() {

        Point iPoint = new Point(-20037508.342787, 20037508.342787, SpatialReference.create(102113));
        List<LevelOfDetail> levelOfDetails=new ArrayList<>();
        for (int i=0;i<iRes.length;i++){
            LevelOfDetail levelOfDetail=new LevelOfDetail(i,iRes[i],iScale[i]);
            levelOfDetails.add(levelOfDetail);
        }

        mTileInfo = new TileInfo(96, TileInfo.ImageFormat.PNG, levelOfDetails, iPoint, SpatialReference.create(102113), 256, 256);
        return mTileInfo;
    }

    // 将图片保存到本地 目录结构可以随便定义 只要你找得到对应的图片
    private byte[] AddOfflineCacheFile(String cachePath, int level, int col, int row, byte[] bytes) {

        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File levelfile = new File(cachePath + "/" + level);
        if (!levelfile.exists()) {
            levelfile.mkdirs();
        }
        File rowfile = new File(cachePath + "/" + level + "/" + col + "x" + row
                + ".png");
        if (!rowfile.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(rowfile);
                out.write(bytes);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bytes;

    }

    // 从本地获取图片
    private byte[] getOfflineCacheFile(String cachePath, int level, int col, int row) {
        byte[] bytes = null;
        File rowfile = new File(cachePath + "/" + level + "/" + col + "x" + row
                + ".png");
        if (rowfile.exists()) {
            try {
                bytes = CopySdcardbytes(rowfile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            bytes = null;
        }
        return bytes;
    }

    // 读取本地图片流
    private byte[] CopySdcardbytes(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] bytes = out.toByteArray();
        return bytes;
    }

}
