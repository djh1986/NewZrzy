package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Manager;

import android.util.Log;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.BookmarkEntity;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Entity.TraceRecordEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TraceRecordManager {

    private static final String TAG = "TraceRecordManager";

    private String projectPath;
    public TraceRecordManager(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * TraceRecord2JsonString
     * @param traceRecordLists
     * @return
     */
    public String TraceRecordList2String(List<TraceRecordEntity> traceRecordLists){
        Gson gson = new Gson();
        String s = gson.toJson(traceRecordLists);
        return s;
    }

    public List<TraceRecordEntity> getTraceRecordList2(){
        String configString = loadTraceRecordListConfigString();
        if(configString == null){
            return null;
        }
        Log.d(TAG, "getTraceRecordList2: " + configString);
        Gson gson = new Gson();
        List<TraceRecordEntity> traceRecordEntityList= gson.fromJson(configString,new TypeToken<List<TraceRecordEntity>>() {
        }.getType());
        return traceRecordEntityList;
    }


    /*
     *加载tracerecord的json配置文件
     */
    public JSONArray loadTraceRecordListConfig() {
        String path = getJSONPath();

        String json = FileUtils.openTxt(path, "UTF-8");

        JSONArray jsonArrayTraceRecord = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArrayTraceRecord = jsonObject.getJSONArray("tracerecords");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonArrayTraceRecord;
    }

    private String loadTraceRecordListConfigString(){
        String path = getJSONPath();
        if(!FileUtils.isExist(path)){
            return null;
        }
        return FileUtils.openTxt(path, "UTF-8");
    }

    public boolean saveTraceRecordListConfig(String content){
        String path = getJSONPath();

        return FileUtils.saveTxt(path, content);
    }

    public String getJSONPath() {
        String path = projectPath+ File.separator+"Json"+File.separator + "traceRecord.json";
        Log.d(TAG, "saveTraceRecordListConfig:  project path " + projectPath);
        return path;
    }

    public List<Bookmark> getEsriBookmarkList(List<BookmarkEntity>  bookmarkEntityList) {
        if(bookmarkEntityList == null || bookmarkEntityList.size()<=0 )return null;
        List<Bookmark> bookmarkList = new ArrayList<>();
        for (BookmarkEntity entity:bookmarkEntityList
        ) {
            int wkid = entity.getExtent().getSpatialReference().getWkid();
            Envelope envelope = new Envelope(entity.getExtent().getXmin(),entity.getExtent().getYmin(),
                    entity.getExtent().getXmax(),entity.getExtent().getYmax(), SpatialReference.create(wkid));
            Viewpoint viewpoint = new Viewpoint(envelope);
            bookmarkList.add(new Bookmark(entity.getName(),viewpoint));
        }

        return bookmarkList;
    }
}
