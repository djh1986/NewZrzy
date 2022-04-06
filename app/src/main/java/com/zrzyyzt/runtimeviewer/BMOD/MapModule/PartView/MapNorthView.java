package com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.Listener.MapNorthClickListener;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.Util;


public class MapNorthView extends LinearLayout {
    private MapView mMapView;
    private MapNorthClickListener mapNorthClickListener;
    private LinearLayout mapNorthLayout;

    private ImageView mapNorthImageView;
    private TextView mapNorthTextView;

    private int zoomWidth,zoomHeight;
    private String mapNorthText;
    private boolean showText=false;

    private  Context mContext;

    public MapNorthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapNorthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.map_north_view,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewAttr);
        this.mContext = context;
        initView();
        initAttr(typedArray);
    }

    public void init(MapView mapView) {
        this.mMapView = mapView;
    }

    public  void init(MapView mapView, MapNorthClickListener mapNorthClickListener){
        this.mMapView = mapView;
        this.mapNorthClickListener = mapNorthClickListener;
    }


    private void initView() {

        mapNorthLayout=(LinearLayout)findViewById(R.id.map_north_layout);
        mapNorthImageView=(ImageView) findViewById(R.id.map_north_image);
        mapNorthTextView=(TextView)findViewById(R.id.map_north_text) ;
        mapNorthLayout.setOnClickListener(listener);
    }

    private OnClickListener listener =  new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if(i==R.id.map_north_layout){
                double rotate = mMapView.getMapRotation();
//                Toast.makeText(mContext, Double.toString(rotate), Toast.LENGTH_LONG).show();
                mMapView.setViewpointRotationAsync(0.0);
                if(mapNorthClickListener!=null){
                    mapNorthClickListener.MapNorthClick(v);
                }
            }
        }
    };

    private void initAttr(TypedArray ta){
        zoomWidth=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_width, Util.valueToSp(getContext(),30));
        zoomHeight=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_height,Util.valueToSp(getContext(),30));
        showText=ta.getBoolean(R.styleable.ViewAttr_show_text,false);
        mapNorthText=ta.getString(R.styleable.ViewAttr_map_location_text);

        setDpWidth(zoomWidth);
        setDpHeight(zoomHeight);

        setNorthText(mapNorthText);
        setShowText(showText);

    }

    private void setDpWidth(int w) {
        mapNorthImageView.getLayoutParams().width = w;
    }

    private void setDpHeight(int h){
        mapNorthImageView.getLayoutParams().height=h;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
        int padding=Util.valueToSp(getContext(),8);
        if(showText){
            mapNorthTextView.setVisibility(View.VISIBLE);
            mapNorthImageView.setPadding(padding,padding,padding,0);
        }else{
            mapNorthTextView.setVisibility(View.GONE);
            mapNorthImageView.setPadding(padding,padding,padding,padding);
        }
    }

    public void setNorthText(String zoomRotateText) {
        this.mapNorthText = zoomRotateText;
        mapNorthTextView.setText(zoomRotateText);
    }


}
