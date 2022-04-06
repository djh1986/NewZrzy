package com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class StLayerSpinnerAdapter extends BaseAdapter {


    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Layer> layerList =null;
    private Context context;

    public StLayerSpinnerAdapter(Context c,List<Layer> list) {
        this.layerList =list ;
        this.context = c;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    private List<Layer> getList(List<Layer> listLayer) {
        List<Layer> list=new ArrayList<>();
        for(int i=0;i<listLayer.size();i++) {
            Layer layer=listLayer.get(i);
            if(layer.isVisible()){
                list.add(layer);
            }
        }
        return list;
    }

    @Override
    public int getCount() {
//        int num=0;
//        for (int i=0;i<layerList.size();i++){
//            Layer layer = layerList.get(i);
//            if (layer.isVisible()){
//                num++;
//            }
//
//        }
//        return num;
        return  layerList.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_ststistics_layer_spinner_item, null);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_statistics_layer_spinner_item_txtName);

        //仅获取当前显示的layer
        //FeatureLayer layer =null;
//        Layer layer = null;
//        int indexPositon=0;//计数
//        for (int i=0;i<layerList.size();i++){
//            Layer layerTpl = layerList.get(i);
//            if (layerTpl.isVisible()){
//                if (indexPositon==position){
//                    layer = layerTpl;
//                }
//                indexPositon++;
//            }
//        }
//        holder.textView.setText(layer.getName());
        holder.textView.setText(layerList.get(position).getName());

        return convertView;
    }
}
