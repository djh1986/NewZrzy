package com.zrzyyzt.runtimeviewer.BMOD.MapModule.PartView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.Util;

public class MapQueryResultView extends LinearLayout {
    private MapView mMapView;
    private LinearLayout mapQueryResultLayout;
    private TextView mapQueryResultTextView;
    private String resultString;
    private  Context mContext;

    private int panelWidth,panelHeight;

    public MapQueryResultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MapQueryResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.map_query_result_view,this);
        mapQueryResultLayout = (LinearLayout)findViewById(R.id.map_query_result_layout);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewAttr);
        this.mContext = context;
        initView();
        initAttr(typedArray);
    }

    public void init(MapView mapView) {
        this.mMapView = mapView;
    }

    private void initView() {

        mapQueryResultLayout=(LinearLayout)findViewById(R.id.map_query_result_layout);
        mapQueryResultTextView=(TextView)findViewById(R.id.map_query_result_text) ;
//        mapQueryResultLayout.setOnClickListener(listener);
    }

    private void initAttr(TypedArray ta){
        panelWidth=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_width, Util.valueToSp(getContext(),200));
        panelHeight=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_height,Util.valueToSp(getContext(),380));
        resultString=ta.getString(R.styleable.ViewAttr_map_query_result_text);

        setDpWidth(panelWidth);
        setDpHeight(panelHeight);

    }

    private void setDpHeight(int panelHeight) {
        mapQueryResultLayout.getLayoutParams().height = panelHeight;
    }

    private void setDpWidth(int panelWidth) {
        mapQueryResultLayout.getLayoutParams().width = panelWidth;
    }

    public void setShowText(String showText) {
        mapQueryResultTextView.setText(showText);
    }
}
