package com.zrzyyzt.runtimeviewer.BMOD.MapModule.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;


/**
 * 全景Demo主Activity
 */
public class PanoMainActivity extends Activity {

    private static final String LTAG = "BaiduPanoSDKDemo";

    private PanoramaView mPanoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 先初始化BMapManager
        initBMapManager();

        Log.i(LTAG, "onCreate:  start setContentView");
        setContentView(R.layout.activity_pano_main);

        initView();

        Intent intent = getIntent();
        if (intent != null) {
            testPanoByType(intent.getDoubleExtra("lat", -1),intent.getDoubleExtra("lon", -1));
        }
    }

    private void initBMapManager() {
        MPApplication app = (MPApplication) this.getApplication();

        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new MPApplication.MyGeneralListener());
        }
    }

    private void initView() {
        Log.i(LTAG, "start initView ");
        mPanoView = (PanoramaView) findViewById(R.id.panorama);
    }

    private void testPanoByType(double lat,double lon) {
        mPanoView.setShowTopoLink(true);

        // 测试回调函数,需要注意的是回调函数要在setPanorama()之前调用，否则回调函数可能执行异常
        mPanoView.setPanoramaViewListener(new PanoramaViewListener() {

            @Override
            public void onLoadPanoramaBegin() {
                Log.i(LTAG, "onLoadPanoramaStart...");
            }

            @Override
            public void onLoadPanoramaEnd(String json) {
                Log.i(LTAG, "onLoadPanoramaEnd : " + json);
            }

            @Override
            public void onLoadPanoramaError(String error) {
                Log.i(LTAG, "onLoadPanoramaError : " + error);
            }

            @Override
            public void onDescriptionLoadEnd(String json) {

            }

            @Override
            public void onMessage(String msgName, int msgType) {

            }

            @Override
            public void onCustomMarkerClick(String key) {

            }

            @Override
            public void onMoveStart() {

            }

            @Override
            public void onMoveEnd() {

            }
        });


            mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionMiddle);
        //35.334055, 107.361940
//            double lat = 35.334055;
//            double lon = 107.361940;
            mPanoView.setPanorama(lon, lat, PanoramaView.COORDTYPE_WGS84);

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }

}
