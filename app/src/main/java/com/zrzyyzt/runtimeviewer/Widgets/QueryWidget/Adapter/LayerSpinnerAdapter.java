package com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.zrzyyzt.runtimeviewer.R;

import java.util.List;

/**
 * 图例
 * Created by gis-luq on 2017/5/5.
 */

public class LayerSpinnerAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Layer> layerList =null;
    private Context context;

    public static final int FEATURELAYER = 1;//矢量地图
    public static final int ARCGISTILEDLAYER = 2;//切片地图

    private int curLayerType;
    private String curLayerName;


    public LayerSpinnerAdapter(Context c, List<Layer> list) {
        this.layerList = list;
        this.context = c;
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        int num=0;
//        for (int i=0;i<layerList.size();i++){
//            Layer layer = layerList.get(i);
//            if (layer.isVisible()){
//                num++;
//            }
//        }
        return layerList.size();
    }

    @Override
    public Object getItem(int position) {
        return layerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int index = layerList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_query_attributequery_spinner_item, null);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_query_attributequery_spinner_item_txtName);

        //仅获取当前显示的layer
//        FeatureLayer featureLayer =null;
//        ArcGISTiledLayer arcGISTiledLayer = null;
//        int indexPositon=0;//计数
//        for (int i=0;i<layerList.size();i++){
//            Layer layerTpl = layerList.get(i);
//            if (layerTpl.isVisible()){
//                if (indexPositon==position){
//                    if(layerTpl instanceof ArcGISTiledLayer){
//                        arcGISTiledLayer = (ArcGISTiledLayer) layerTpl;
//                        curLayerType = ARCGISTILEDLAYER;
//                        curLayerName = arcGISTiledLayer.getName();
//                    }else if(layerTpl instanceof FeatureLayer){
//                        featureLayer = (FeatureLayer) layerTpl;
//                        curLayerType = FEATURELAYER;
//                        curLayerName = featureLayer.getName();
//                    }
//
//                }
//                indexPositon++;
//            }
//        }
        holder.textView.setText(layerList.get(position).getName());

        return convertView;
    }

}
