package com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.Listener.MapLocationClickListener;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.Util;

public class MapLocationView extends LinearLayout {
    private MapView mMapView;
    private MapLocationClickListener mMapLocationClickListener;

    private LinearLayout mapLocationLayout;

    private ImageView mapLocationImageView;
    private TextView mapLocationTextView;

    private int zoomWidth,zoomHeight;
    private String mapLocationText;
    private boolean showText=false;

    private int requestCode = 2;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    private LocationDisplay mLocationDisplay;

    private  Context mContext;

    public MapLocationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapLocationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.map_location_view,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewAttr);
        this.mContext = context;
        initView();
        initAttr(typedArray);
    }

    public void init(MapView mapView){
        this.mMapView = mapView;
        initLocationDisplay();
    }

    public void init(MapView mapView, MapLocationClickListener mapLocationClickListener){
        this.mMapView = mapView;
        this.mMapLocationClickListener = mapLocationClickListener;
        initLocationDisplay();
    }

    /**
     * 初始化LocationDisplay
     */
    private void initLocationDisplay(){
        this.mLocationDisplay = mMapView.getLocationDisplay();
        this.mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        this.mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
                if (dataSourceStatusChangedEvent.isStarted())
                    return;

                if (dataSourceStatusChangedEvent.getError() == null)
                    return;

                boolean permissionCheck1 = ContextCompat.checkSelfPermission(mContext, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(mContext, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    Toast.makeText(mContext, "获取定位权限失败", Toast.LENGTH_LONG).show();
                }
//
//                if (!(permissionCheck1 && permissionCheck2)) {
//                    ActivityCompat.requestPermissions(MapActivity.this, reqPermissions, requestCode);
//                } else {
//                    // Report other unknown failure types to the user - for example, location services may not
//                    // be enabled on the device.
//                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
//                            .getSource().getLocationDataSource().getError().getMessage());
//                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
//
//                    // Update UI to reflect that the location display did not actually start
//                    mSpinner.setSelection(0, true);
//                }
            }
        });
    }

    /**
     * 初始化属性
     * @param ta
     */
    private void initAttr(TypedArray ta) {
        zoomWidth=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_width, Util.valueToSp(getContext(),30));
        zoomHeight=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_height,Util.valueToSp(getContext(),30));
        showText=ta.getBoolean(R.styleable.ViewAttr_show_text,false);
        mapLocationText=ta.getString(R.styleable.ViewAttr_map_north_text);

        setDpWidth(zoomWidth);
        setDpHeight(zoomHeight);

        setLocationText(mapLocationText);
        setShowText(showText);
    }

    private void setShowText(boolean showText) {
        this.showText = showText;
        int padding=Util.valueToSp(getContext(),10);
        if(showText){
            mapLocationTextView.setVisibility(View.VISIBLE);
            mapLocationImageView.setPadding(padding,padding,padding,0);
        }else{
            mapLocationTextView.setVisibility(View.GONE);
            mapLocationImageView.setPadding(padding,padding,padding,padding);
        }
    }

    private void setLocationText(String mapLocationText) {
        this.mapLocationText = mapLocationText;
        mapLocationTextView.setText(mapLocationText);
    }

    private void setDpHeight(int zoomHeight) {
        mapLocationImageView.getLayoutParams().height = zoomHeight;
    }

    private void setDpWidth(int zoomWidth) {
        mapLocationImageView.getLayoutParams().width = zoomWidth;
    }

    private void setMapLocationLayoutColor(int color){
        mapLocationLayout.setBackgroundColor(color);
    }
    private void initView() {
        mapLocationLayout=(LinearLayout)findViewById(R.id.map_location_layout);
        mapLocationImageView=(ImageView) findViewById(R.id.map_location_image);
        mapLocationTextView=(TextView)findViewById(R.id.map_location_text) ;
        mapLocationLayout.setOnClickListener(listener);
    }

    private OnClickListener listener =  new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if(i==R.id.map_location_layout){
                if(mLocationDisplay.isStarted()){
                    mLocationDisplay.stop();
                    setMapLocationLayoutColor(Color.WHITE);
                }else if(!mLocationDisplay.isStarted()){
                    mLocationDisplay.startAsync();
                    setMapLocationLayoutColor(Color.LTGRAY);
                }
                if(mMapLocationClickListener!=null){
                    mMapLocationClickListener.MapLocationClick(v);
                }
            }
        }
    };


}
