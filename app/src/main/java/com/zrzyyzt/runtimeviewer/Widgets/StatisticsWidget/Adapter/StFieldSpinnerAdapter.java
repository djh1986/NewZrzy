package com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.layers.Layer;
import com.zrzyyzt.runtimeviewer.R;

import java.util.List;

public class StFieldSpinnerAdapter extends BaseAdapter {


    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//字段
    }

    private List<Field> fieldList =null;
    private Context context;

    public StFieldSpinnerAdapter(Context c,List<Field>list){

        fieldList=list;
        context=c;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
       return fieldList.size();
    }

    @Override
    public Object getItem(int position) {
        return fieldList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = fieldList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_ststistics_field_spinner_item, null);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_statistics_field_spinner_item_txtName);

//        Field field = null;
//        int indexPositon=0;//计数
//        for (int i=0;i<fieldList.size();i++){
//            Field fieldTpl = fieldList.get(i);
//            if (!fieldTpl.getName().toUpperCase().equals("FID")){
//                if (indexPositon==position){
//                    field = fieldTpl;
//                }
//                indexPositon++;
//            }
//        }
        if(fieldList.get(position).getAlias().equalsIgnoreCase("")){
            holder.textView.setText(fieldList.get(position).getName());
        }else{
            holder.textView.setText(fieldList.get(position).getAlias());
        }
        //holder.textView.setText(fieldList.get(position).getAlias());
        return convertView;
    }
}
