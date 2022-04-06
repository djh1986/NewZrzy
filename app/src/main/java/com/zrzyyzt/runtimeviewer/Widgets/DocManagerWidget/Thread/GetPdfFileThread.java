package com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Thread;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPdfFileThread implements Callable<List<PdfFileEntity>> {

    private static final String TAG = "GetPdfFileThread";
    private List<PdfFileEntity> webPdfFileEntities = null;
        private String mIpAddress = null;

    public GetPdfFileThread(List<PdfFileEntity> webPdfFileEntities, String ipAddress) {
        this.webPdfFileEntities = webPdfFileEntities;
        this.mIpAddress = ipAddress;
    }

    @Override
    public List<PdfFileEntity> call() throws Exception {
        if(mIpAddress==null) return null;

        OkHttpClient client = new OkHttpClient();
        String url = "http://" + mIpAddress + "/pdf/pdflist.json";

        //Log.d(TAG, "call: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            String jsonBody = execute.body().string();
            Gson gson = new Gson();
            //PdfFileEntity pdfFileEntity = gson.fromJson("", PdfFileEntity.class);  单个解析
            webPdfFileEntities = gson.fromJson(jsonBody, new TypeToken<List<PdfFileEntity>>() {
            }.getType());

            /*
            for (PdfFileEntity entity:webPdfFileEntities
                 ) {
                Log.d(TAG, "call: " + entity.getName() + "," + entity.getUrl());
            }
           */
        } catch (IOException e) {
//            e.printStackTrace();
            Log.d(TAG, "call: " + e.getMessage());
        }

        return webPdfFileEntities;
    }

}
