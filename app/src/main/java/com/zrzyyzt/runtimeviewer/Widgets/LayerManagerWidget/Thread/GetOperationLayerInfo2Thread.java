package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Thread;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;
import com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Entity.OperationLayerInfo2;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetOperationLayerInfo2Thread implements Callable<List<OperationLayerInfo2>> {

    private static final String TAG = "GetOperationLayerInfo2Thread";
    private List<OperationLayerInfo2> webOperationLayerInfo2s = null;
    private String mIpAddress = null;

    public GetOperationLayerInfo2Thread(List<OperationLayerInfo2> webOperationLayerInfo2s, String ipAddress) {
        this.webOperationLayerInfo2s = webOperationLayerInfo2s;
        this.mIpAddress = ipAddress;
    }

    @Override
    public List<OperationLayerInfo2> call() throws Exception {
        if(mIpAddress==null) return null;

        OkHttpClient client = new OkHttpClient();
        String url = "http://" + mIpAddress + "/layer/operationlayers.json";
        //Log.v("",url);
        //Log.d(TAG, "call: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            String jsonBody = execute.body().string();
            //Log.v(TAG,jsonBody);
            Gson gson = new Gson();
            //PdfFileEntity pdfFileEntity = gson.fromJson("", PdfFileEntity.class);  单个解析
            webOperationLayerInfo2s = gson.fromJson(jsonBody, new TypeToken<List<OperationLayerInfo2>>() {
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

        return webOperationLayerInfo2s;
    }
}
