package com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Entity.PoiBean;

import java.util.List;

public class PoiResultAdapter extends BaseAdapter {

    private static final String TAG = "PoiResultAdapter";
    private Context context;
    private List<PoiBean.PoisBean>poisBeanList;

    public PoiResultAdapter(Context context,List<PoiBean.PoisBean> poisBeanList) {
        this.context=context;
        this.poisBeanList=poisBeanList;
    }
    @Override
    public int getCount() {
        return poisBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return poisBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(R.layout.widget_view_placenamesearch_poi_item,null);
        TextView textView = convertView.findViewById(R.id.widget_view_placenamesearch_poi_item_txtName);
        PoiBean.PoisBean poisBean=poisBeanList.get(position);
        textView.setText(poisBean.getName());
       return convertView;
    }
}
