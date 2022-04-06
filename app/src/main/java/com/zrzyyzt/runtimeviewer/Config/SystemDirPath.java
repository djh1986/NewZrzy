package com.zrzyyzt.runtimeviewer.Config;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;

import gisluq.lib.Util.SDCardUtils;

/**
 * 系统文件夹管理类
 * 记录、获取系统文件夹路径，管理存储位置信息
 */
public class SystemDirPath {
    private static final String TAG = "SystemDirPath";
    private static String MainWorkSpace = "/RuntimeViewer";//工作空间地址

    private static String Projects = "/Projects"; //系统工程文件夹

    private static String PdfFilePath = "/PdfFiles";
    private static String PrintScreenPath = "/PrintScreen";
    private static String CameraPath = "/Camera";
    private static String SystemConf = "/System"; //系统模板
    private static String lockViewConf = "/lockscreen.conf"; //锁屏配置文件信息

    public static String SDPath = SDCardUtils.getSDCardPath();//系统SD卡路径

    public static ConfigEntity configEntity = null;
    /**
     * 获取SD卡工具路径
     * @return
     */
    public static String getSDPath(){
        return SDPath;
    }

    /**
     * 获取系统工作空间文件夹路径（主目录）
     * 主目录以系统内部存储为主
     * @return
     * @param context
     */
    public static String getMainWorkSpacePath(Context context){
        if (configEntity==null){
            configEntity =AppConfig.getConfig(context);
        }
        String path = configEntity.getWorkspacePath();

        if(SDPath == null){
//            String dataPath = Environment.getDataDirectory().getPath();
            String dataPath = Environment.getExternalStorageDirectory().getPath();

            Log.d(TAG, "getMainWorkSpacePath: dataPath:" + dataPath);
            if (path!=null||(!path.equals(""))){
                return dataPath + path;
            }
            return  dataPath + MainWorkSpace;
        }else{
            if (path!=null||(!path.equals(""))){
                return SDPath + path;
            }
            return  SDPath + MainWorkSpace;
        }

    }

    /**
     * 获取系统配置文件夹路径（系统配置目录仅内部存储）
     * @return
     */
    public static String getSystemConfPath(Context context){
        String path =getMainWorkSpacePath(context) + SystemConf;
        FileUtils.createChildFilesDir(path);
        return path;
    }


    /**
     * 获取工程文件夹路径
     * @return
     */
    public static String getProjectPath(Context context){
        String path =getMainWorkSpacePath(context) + Projects;
        FileUtils.createChildFilesDir(path);
        return  path;
    }


    /**
     * 获取系统锁屏配置文件路径
     * @return
     */
    public static String getLockViewConfPath(Context context){
        String path =getMainWorkSpacePath(context) + SystemConf + lockViewConf;
//        FileUtils.createChildFilesDir(path);
        return  path;
    }



    public String getPdfFilePath(Context context) {
        String path =getProjectPath(context) + PdfFilePath;
        FileUtils.createChildFilesDir(path);
        return path;
    }

    /**
     * 获取截图路径
     * @param context
     * @return
     */
    public static String getPrintScreenPath(Context context){
        String path = getProjectPath(context) + PrintScreenPath;
        FileUtils.createChildFilesDir(path);
        return  path;
    }

    /**
     * 获取拍照路径
     * @param context
     * @return
     */
    public static String getCameraPath(Context context){
        String path = getProjectPath(context) + CameraPath;
        FileUtils.createChildFilesDir(path);
        return  path;
    }
}
