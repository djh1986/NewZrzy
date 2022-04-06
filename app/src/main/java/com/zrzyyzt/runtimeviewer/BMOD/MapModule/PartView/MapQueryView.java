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

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.Listener.MapQueryListener;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.Util;

public class MapQueryView extends LinearLayout {
    private MapView mMapView;
    private Context context;

    private DefaultMapViewOnTouchListener mapDefualtListener;
    private MapQueryListener mapQueryListener;

    private LinearLayout mapQueryLayout,mapQueryBg;

    private ImageView mapQueryImageView;
    private TextView mapQueryTextView;

    private int bgColor,fontColor,mapQueryImage;

    private int buttonWidth, buttonHeight, fontSize;
    private String mapQueryText;
    private boolean showText=false;

    private boolean isQueryStart = false;

    public MapQueryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapQueryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.map_query_view,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewAttr);
        this.context = context;
        initView();
        initAttr(typedArray);
    }

    public void init(MapView mapView, DefaultMapViewOnTouchListener mapDefualtListener){
        init(mapView,mapDefualtListener,null);
    }

    public void init(MapView mapView, DefaultMapViewOnTouchListener mapDefualtListener, MapQueryListener mapQueryListener){
        this.mMapView = mapView;
        this.mapQueryListener = mapQueryListener;
        this.mapDefualtListener = mapDefualtListener;

    }

    private void initView() {
        mapQueryBg=(LinearLayout)findViewById(R.id.map_query_bg);
        mapQueryLayout =(LinearLayout)findViewById(R.id.map_query_layout);
        mapQueryImageView =(ImageView) findViewById(R.id.map_query_image);
        mapQueryTextView =(TextView)findViewById(R.id.map_query_text) ;

        mapQueryLayout.setOnClickListener(listener);
    }

    /**
     * 初始化属性
     * @param ta
     */
    private void initAttr(TypedArray ta) {
        bgColor=ta.getResourceId(R.styleable.ViewAttr_view_background,R.drawable.map_shadow_bg);
        buttonWidth =ta.getDimensionPixelSize(R.styleable.ViewAttr_button_width, Util.valueToSp(getContext(),30));
        buttonHeight =ta.getDimensionPixelSize(R.styleable.ViewAttr_button_height,Util.valueToSp(getContext(),30));
        showText=ta.getBoolean(R.styleable.ViewAttr_show_text,false);
        mapQueryText=ta.getString(R.styleable.ViewAttr_map_north_text);
        fontColor=ta.getResourceId(R.styleable.ViewAttr_font_color,R.color.gray);
        fontSize=ta.getInt(R.styleable.ViewAttr_font_size,12);

        mapQueryImage=ta.getResourceId(R.styleable.ViewAttr_map_query_image,R.drawable.ic_search_black_24dp);

        setBackground(bgColor);

        setDpWidth(buttonWidth);
        setDpHeight(buttonHeight);
        setShowText(showText);
        setQueryText(mapQueryText);

        setQueryImage(mapQueryImage);
    }

    private void setQueryImage(int mapQueryImage) {
        this.mapQueryImage = mapQueryImage;
        mapQueryImageView.setImageDrawable(getResources().getDrawable(mapQueryImage));
    }


    private void setBackground(int bgColor) {
        this.bgColor=bgColor;
        mapQueryBg.setBackground(getResources().getDrawable(bgColor));
    }

    private void setShowText(boolean showText) {
        this.showText = showText;
        int padding=Util.valueToSp(getContext(),7);
        if(showText){
            mapQueryTextView.setVisibility(View.VISIBLE);
            mapQueryImageView.setPadding(padding,padding,padding,0);
        }else{
            mapQueryTextView.setVisibility(View.GONE);
            mapQueryImageView.setPadding(padding,padding,padding,padding);
        }
    }

    private void setQueryText(String mapQueryText) {
        this.mapQueryText = mapQueryText;
        mapQueryTextView.setText(mapQueryText);
    }

    private void setDpHeight(int zoomHeight) {
        mapQueryImageView.getLayoutParams().height = zoomHeight;
    }

    private void setDpWidth(int zoomWidth) {
        mapQueryImageView.getLayoutParams().width = zoomWidth;
    }

    private void setMapQueryLayoutColor(int color){
        mapQueryImageView.setBackgroundColor(color);
    }


    private OnClickListener listener =  new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if(i==R.id.map_query_layout){
                if(!isQueryStart){
                    isQueryStart = true;
                    mMapView.setOnTouchListener(mapQueryListener);
                    mapQueryBg.setBackground(context.getDrawable(R.drawable.map_shadow_bg_gray));
                    mMapView.setMagnifierEnabled(true);
                }else{
                    isQueryStart = false;
                    mMapView.setOnTouchListener(mapDefualtListener);
                    mapQueryBg.setBackground(context.getDrawable(R.drawable.map_shadow_bg));
                    mMapView.setMagnifierEnabled(false);
                    mapQueryListener.clear();
                }
            }
        }
    };


}
