package com.zrzyyzt.runtimeviewer.BMOD.MapModule.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.zrzyyzt.runtimeviewer.BMOD.CameraModule.CameraActivity;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.WidgetManager;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Map.MapManager;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;
import com.zrzyyzt.runtimeviewer.BMOD.PhotoModule.PhotoListActivity;
import com.zrzyyzt.runtimeviewer.Base.BaseActivity;
import com.zrzyyzt.runtimeviewer.Config.AppConfig;
import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.Config.Entity.WidgetEntity;
import com.zrzyyzt.runtimeviewer.Config.SystemDirPath;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.DialogUtils;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;

import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.ptSym;

public class MapActivity extends BaseActivity {

    private static final String TAG = "MapActivity";

    private static final int PERMISSION_CODE = 42042;
    private static final int BAIDUPANO_CODE = 42043;
    private Context context;
    private ResourceConfig resourceConfig;//UI????????????

    private ConfigEntity mConfigEntity;//????????????????????????
    private MapManager mMapManager;//???????????????
    private WidgetManager mWidgetManager;//???????????????

    private Map<Integer,Object> mWidgetEntityMenu = new HashMap<>();//Widget Menu????????????

    private TextView titleTextView;//????????????
    private String DirName;//????????????
    private String DirPath;//????????????


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context = this;

        Intent intent = getIntent();
        DirName = intent.getStringExtra("DirName");
        DirPath = intent.getStringExtra("DirPath");
//        titleTextView.setText(mConfigEntity.getAppName());//???????????????????????????


        resourceConfig = new ResourceConfig(context);//?????????????????????????????????
        init();//?????????????????????


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ??????????????????--?????????????????????????????????
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    protected void onResume() {
        /** ???????????????????????????*/
        boolean ispad = SysUtils.isPad(context);
        if (ispad==false){
            if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onResume();
    }

    /**
     * ???????????????????????????????????????
     */
    private void init() {
        //??????????????????????????????
        mConfigEntity = AppConfig.getConfig(context);

        titleTextView.setText(mConfigEntity.getAppName());//???????????????????????????

        //???????????????????????????-??????????????????
        mMapManager = new MapManager(context, resourceConfig, mConfigEntity, DirPath);

        //???????????????????????????
        mWidgetManager = new WidgetManager(context, resourceConfig, mMapManager, mConfigEntity, DirPath);
        //?????????Widget????????????
        mWidgetManager.instanceAllClass();

    }

    /***
     * ????????????????????????????????????
     * @param toolbar
     */
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
//        toolbar.setNavigationIcon(null);//???????????????????????????
        getLayoutInflater().inflate(R.layout.activity_toobar_view, toolbar);
        titleTextView = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        titleTextView.setTextSize(12);
        titleTextView.setPadding(0, 0, 0, 0);
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        //????????????
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * ????????????????????????????????????????????????
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        boolean isPad = SysUtils.isPad(context);

        if (isPad){
            /***
             * ????????????????????????????????????
             */

            //????????????????????????????????????????????????
            if (mConfigEntity != null) {
                final List<WidgetEntity> mListWidget = mConfigEntity.getListWidget();

                for (int i = 0; i < mListWidget.size(); i++) {
                    final WidgetEntity widgetEntity = mListWidget.get(i);

                    //widget?????????????????????
                    View view = LayoutInflater.from(context).inflate(R.layout.base_widget_view_tools_widget_btn, null);
                    final LinearLayout ltbtn = view.findViewById(R.id.base_widget_view_tools_widget_btn_lnbtnWidget);
                    TextView textViewName = (TextView) view.findViewById(R.id.base_widget_view_tools_widget_btn_txtWidgetToolName);
                    ImageView imageView = (ImageView) view.findViewById(R.id.base_widget_view_tools_widget_btn_imgWidgetToolIcon);

                    Log.i(TAG, "onCreateOptionsMenu: " + widgetEntity.getLabel() + widgetEntity.getId());

                    //??????????????????UI
                    mWidgetManager.setWidgetBtnView(widgetEntity.getId(), textViewName, imageView);

                    ltbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = widgetEntity.getId();

                            //???????????????????????????????????????
//                            if(widgetEntity.getmPermission()!=null){
//                                //?????????????????????
//                                int permissionCheck = ContextCompat.checkSelfPermission(context,
//                                        "android.permission.WRITE_EXTERNAL_STORAGE");
//                                Log.d(TAG, "onCreate: " + permissionCheck);
//
//                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(
//                                            ,
//                                            new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
//                                            PERMISSION_CODE
//                                    );
//                                }
//                            }

                            BaseWidget widget = mWidgetManager.getSelectWidget();//????????????

                            if (widget != null) {
                                //??????????????????widget
                                if (id == widget.id) {
                                    //??????????????????????????????
                                    if (mWidgetManager.getSelectWidget().isActiveView()) {
                                        mWidgetManager.startWidgetByID(id);//??????widget
                                    } else {
                                        mWidgetManager.hideSelectWidget();
                                    }

                                } else {
                                    mWidgetManager.startWidgetByID(id);//??????widget
                                }
                            } else {
                                //???????????????widget
                                mWidgetManager.startWidgetByID(id);//??????widget
                            }

                            //????????????????????????????????????600
//                            Log.d(TAG, "onClick: classname???" + widgetEntity.getClassname());
//                            if (widgetEntity.getClassname().contains("Hello")) {
//                                Log.d(TAG, "onClick: classname contain hello");
//
//                            }

                            mWidgetManager.setWidth(widgetEntity.getWidth());
                        }
                    });
                    textViewName.setText(widgetEntity.getLabel());

                    try {
                        String name = widgetEntity.getIconName();
                        if (name != null) {
//                            InputStream is = getAssets().open(name);
//                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            int identifier = getResources().getIdentifier(name.replace(".png",""),"mipmap",getPackageName());
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),identifier);
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    resourceConfig.baseWidgetToolsView.addView(view);

                }
            }
        }else {
            /***
             * ????????????????????????????????????????????????--????????????
             */
            //????????????????????????????????????????????????
            if (mConfigEntity != null) {
                List<WidgetEntity> mListWidget = mConfigEntity.getListWidget();

                String widgetGroup = "";
                List<WidgetEntity> mGroupListWidget = new ArrayList<>();

                for (int i = 0; i < mListWidget.size(); i++) {
                    WidgetEntity widgetEntity = mListWidget.get(i);
                    if(widgetEntity.getGroup()==""){
                        MenuItem menuItem= menu.add(Menu.NONE, Menu.FIRST + i, 0, widgetEntity.getLabel());
                        //????????????ID???WidgetEntity???????????????
                        mWidgetEntityMenu.put(menuItem.getItemId(),widgetEntity);
                        if (widgetEntity.getIsShowing()) {
                            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//??????????????????
                        }
                    }else {
                        if (widgetGroup == "") {
                            widgetGroup = widgetEntity.getGroup();//???????????????
                            //????????????
                            MenuItem menuItem = menu.add(Menu.NONE, Menu.FIRST + i, 0, widgetGroup);
                            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//??????????????????
                            //????????????ID???WidgetEntity???????????????
                            mWidgetEntityMenu.put(menuItem.getItemId(),mGroupListWidget);
                        }
                        if (widgetEntity.getGroup().equals(widgetGroup)) {//????????????
                            mGroupListWidget.add(widgetEntity);//??????widget?????????
                        } else {
                            //?????????
                        }
                    }

                }
            }

        }
        return true;
    }

    /**
     * ????????????????????????????????????
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId()==android.R.id.home) {
//            exitActivity();
//            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//            return true;
//        }

        //???????????????????????????????????????
        if (item.getItemId() == android.R.id.home){

//            ConfigEntity config = AppConfig.getConfig(this);
            Envelope envelope = new Envelope(mConfigEntity.getExtent().getXmin(),
                    mConfigEntity.getExtent().getYmin(),
                    mConfigEntity.getExtent().getXmax(),
                    mConfigEntity.getExtent().getYmax(),
                    SpatialReference.create(mConfigEntity.getExtent().getSpatialReference().getWkid()));

            Viewpoint vp = new Viewpoint(envelope);
            resourceConfig.mapView.setViewpointAsync(vp);
            return true ;
        }else if(item.getItemId() == R.id.cross){
            mapLocation();
        } else if(item.getItemId() == R.id.printScreen){
            Bitmap bitmap = mMapManager.getMapViewBitmap();
            saveImageToGallery(bitmap);
        }else if(item.getItemId() == R.id.camera){
            Intent cameraIntent = new Intent(MapActivity.this, CameraActivity.class);
            this.startActivity(cameraIntent);
        }else if(item.getItemId() == R.id.photo){
            Intent photoIntent = new Intent(MapActivity.this, PhotoListActivity.class);
            this.startActivity(photoIntent);
        }else if(item.getItemId() == R.id.measure){
            resourceConfig.setMeasureToolViewVisibility();
        }else if(item.getItemId() == R.id.panorama){
            ToastUtils.showLong(this,"??????????????????????????????????????????");
            resourceConfig.mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context,resourceConfig.mapView){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Point point = resourceConfig.mapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
                    Intent intent = new Intent(MapActivity.this, PanoMainActivity.class);
                    intent.putExtra("lat",point.getY());
                    intent.putExtra("lon",point.getX());
                    MapActivity.this.startActivityForResult(intent,BAIDUPANO_CODE);
                    return super.onSingleTapUp(e);
                }
            });
        }else if(item.getItemId() == R.id.exit){
            exitActivity();
        }

        super.onOptionsItemSelected(item);

        Object object =  mWidgetEntityMenu.get(item.getItemId());
        if (object!=null){
            if (object.getClass().equals(WidgetEntity.class)){
                WidgetEntity widgetEntity = (WidgetEntity)object;
                if (widgetEntity!=null){
                    mWidgetManager.startWidgetByID(widgetEntity.getId());//??????widget
                }else {
                    ToastUtils.showShort(context,"WidgetEntity??????");
                }
            }else if (object.getClass().equals(ArrayList.class)){
                final List<WidgetEntity> list = (List<WidgetEntity>)object;
                if (list!=null){
                    PopupMenu pmp = new PopupMenu(context,findViewById(item.getItemId()));
                    for (int i=0;i<list.size();i++){
                        WidgetEntity entity = list.get(i);
                        pmp.getMenu().add(entity.getLabel());
                    }
                    pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //?????????????????????
                            for (int i=0;i<list.size();i++){
                                WidgetEntity entity = list.get(i);
                                if (entity.getLabel().equals(item.getTitle())){
                                    mWidgetManager.startWidgetByID(entity.getId());//??????widget
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    pmp.show();
                }
            }
        }

        return true;
    }

    /**
     * ????????????????????????
     */
    private void requestReadExternalStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new DialogUtils.ReadExternalStoragePermissionDialog().show(getSupportFragmentManager(), "dialog");
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    private void mapLocation(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LocationProvider lProvider = lm.getProvider(LocationManager.GPS_PROVIDER);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, "????????????????????????", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,0);
            return;
        }
        LocationDisplay locationDisplay=resourceConfig.mapView.getLocationDisplay();
        locationDisplay.startAsync();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);

    }
    /**
     * ??????bitmap?????????
     * @param bitmap
     */
    private int saveImageToGallery(Bitmap bitmap) {
        //????????????
//        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String dirName = "erweima16";
//        File fileDir = new File(root , dirName);
//        if (!fileDir.exists()) {
//            fileDir.mkdirs();
//        }

        String fileDir =  SystemDirPath.getPrintScreenPath(context);
        FileUtils.createChildFilesDir(fileDir);

        //??????????????????
//        long timeStamp = System.currentTimeMillis();
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String sd = sdf.format(new Date(timeStamp));
        String sd =  TimeUtils.getCurrentTime();
        String fileName = sd + ".jpg";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"????????????????????????????????????",Toast.LENGTH_LONG).show();
            requestReadExternalStoragePermission();
            return  -1;
        }

        //????????????
        File file = new File(fileDir, fileName);
        FileOutputStream fos = null;


        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //???????????????????????????
//            try{
//                MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                        file.getAbsolutePath(), fileName, null);
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
            //????????????????????????
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(file.getPath()))));
            ToastUtils.showShort(context,"???????????? " + fileDir + "/"+ fileName);
            return 2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ????????????
     */
    private void exitActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("???????????????????????????");
        builder.setTitle("????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        });
        builder.create().show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case BAIDUPANO_CODE:
                resourceConfig.mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context,resourceConfig.mapView));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
		//?????????????????????????????????
        //EventBus.getDefault().post(new MessageEvent("camera-"+requestCode,0));
    }
}
