package com.zrzyyzt.runtimeviewer.BMOD.PhotoModule;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zrzyyzt.runtimeviewer.Entity.PhotoEntity;
import com.zrzyyzt.runtimeviewer.GloabApp.MPApplication;
import com.zrzyyzt.runtimeviewer.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<PhotoEntity> photoList = new ArrayList<>();

    private PhotoAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;


    PhotoEntityDao photoDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        DaoSession daoSession = ((MPApplication) getApplication()).getDaoSession();
        photoDao = daoSession.getPhotoEntityDao();

        initPhoto();
//        initFruits();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoAdapter(photoList);
        recyclerView.setAdapter(adapter);
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshPhotos();
//            }
//        });
    }

    private void refreshPhotos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initPhoto();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    private void initPhoto(){
        photoList.clear();
        Log.d(TAG, "initPhoto: photo count is " + photoDao.count());

        photoList = photoDao.queryBuilder()
                .list();
        Log.d(TAG, "initPhoto: photoList size is " + photoList.size()) ;
    }
}
