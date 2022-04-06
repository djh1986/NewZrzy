package com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Thread;

import android.util.Log;

import com.google.gson.Gson;
import com.zrzyyzt.runtimeviewer.Widgets.PeripheralQueryWidget.Bean.PoiBean;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPoiThread implements Callable<PoiBean> {

    private static final String TAG = "GetPoiThread";
    private PoiBean poiBean = null;
    private String mIpAddress = null;

    public GetPoiThread(PoiBean poiBean, String ipAddress) {
        this.poiBean = poiBean;
        this.mIpAddress = ipAddress;
    }
    @Override
    public PoiBean call() throws Exception {
        if(mIpAddress==null) return null;

        OkHttpClient client = new OkHttpClient();
        String url = mIpAddress ;

        //Log.d(TAG, "call: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            String jsonBody = execute.body().string();
            if(jsonBody.contains("pois")) {
                Log.v(TAG, jsonBody);
                Gson gson = new Gson();
                poiBean = gson.fromJson(jsonBody, PoiBean.class);
            }
        } catch (IOException e) {
            Log.d(TAG, "call: " + e.getMessage());
        }
        return poiBean;
    }
}
