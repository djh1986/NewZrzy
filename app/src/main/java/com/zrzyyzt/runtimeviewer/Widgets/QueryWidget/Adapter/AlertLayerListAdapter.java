package com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.zrzyyzt.runtimeviewer.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *  要素选择弹框
 * Created by gis-luq on 2017/5/5.
 */

public class AlertLayerListAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Feature> featureList =null;
    private Context context;

    public AlertLayerListAdapter(Context c, List<Feature> list) {
        this.featureList = list;
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
        if (featureList !=null){
            return  featureList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int index = featureList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_query_mapquery_alertlayers_item, null);
        holder.imageView =(ImageView) convertView.findViewById(R.id.widget_view_query_mapquery_alertlayers_item_img);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_query_mapquery_alertlayers_item_txtName);

        //仅获取当前显示的layer
//        FeatureLayer layer = featureList.get(position).getFeatureTable().getFeatureLayer();
//        holder.textView.setText(layer.getName());
        FeatureTable featureTable = featureList.get(position).getFeatureTable();
        holder.textView.setText(featureTable.getDisplayName());

        //要素模板
//        Feature feature = featureTable.createFeature();
//        Symbol symbol = layer.getRenderer().getSymbol(feature);
        SimpleFillSymbol symbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x88FF0000, null);
        ListenableFuture<Bitmap> bitmap =symbol.createSwatchAsync(context, Color.WHITE);
        try {
            holder.imageView.setImageBitmap(bitmap.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        holder.imageView.setBackground(null);//layout中要素模板背景置为空
        return convertView;
    }

}
