package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Manager;

import android.util.Log;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.BookmarkEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    private static final String TAG = "BookmarkManager";

    private String projectPath;
    public BookmarkManager(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * Bookmark2JsonString
     * @param bookmarkList
     * @return
     */
    public String BookmarkList2String(List<BookmarkEntity> bookmarkList){
        Gson gson = new Gson();
        String s = gson.toJson(bookmarkList);
        return s;
    }

    public List<BookmarkEntity> getBookmarkList2(){
        String configString = loadBookmarkListConfigString();
        if(configString == null){
            return null;
        }
        Gson gson = new Gson();
        List<BookmarkEntity> bookmarkEntityList= gson.fromJson(configString,new TypeToken<List<BookmarkEntity>>() {
        }.getType());
        return bookmarkEntityList;
    }

    /*
     *Json2Bookmark
     */
    public List<Bookmark> getBookmarkList(JSONArray jsonArray) throws JSONException {
        List<Bookmark> result = null;
        int num = jsonArray.length();
        if(num>0) {
            result = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String title = "";
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                int wkid=0;

                //name
                try {
                    title = obj.getString("name");
                } catch (Exception e) {

                }

                //extent
                try{
                    JSONObject extent = obj.getJSONObject("extent");
                    try {
                        x1 = extent.getDouble("xmin");
                    } catch (Exception e) {

                    }
                    try {
                        y1 = extent.getDouble("ymin");
                    } catch (Exception e) {

                    }

                    try {
                        x2 = extent.getDouble("xmax");
                    } catch (Exception e) {

                    }
                    try {
                        y2 = extent.getDouble("ymax");
                    } catch (Exception e) {

                    }
                    try{
                        JSONObject sr = extent.getJSONObject("spatialReference");
                        try {
                            wkid = sr.getInt("wkid");
                        }catch (Exception e){

                        }
                    }catch (Exception ex){

                    }

                }catch (Exception e){

                }
//                Log.d(TAG, "getBookmarkList: "+ title+","+x1+","+y1+","+x2+","+y2+","+wkid);
                Envelope envelope = new Envelope(x1, y1, x2, y2, SpatialReference.create(wkid));
                Viewpoint viewpoint = new Viewpoint(envelope);
//                bookmark.setViewpoint(viewpoint);
//                bookmark.setName(tilte);
                result.add(new Bookmark(title,viewpoint));
            }
        }
        return result;
    }

    /*
     *加载bookmark的json配置文件
     */
    public JSONArray loadBookmarkListConfig() {
        String path = getJSONPath();

        String json = FileUtils.openTxt(path, "UTF-8");

        JSONArray jsonArrayBookmark = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArrayBookmark = jsonObject.getJSONArray("bookmarks");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonArrayBookmark;
    }

    private String loadBookmarkListConfigString(){
        String path = getJSONPath();
        if(!FileUtils.isExist(path)){
            return null;
        }
        return FileUtils.openTxt(path, "UTF-8");
    }

    public boolean saveBookmarkListConfig(String content){
        String path = getJSONPath();

        return FileUtils.saveTxt(path, content);
    }

    public String getJSONPath() {
        String path = projectPath+ File.separator+"Json"+File.separator + "bookmarks.json";
        Log.d(TAG, "saveBookmarkListConfig:  project path " + projectPath);
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
