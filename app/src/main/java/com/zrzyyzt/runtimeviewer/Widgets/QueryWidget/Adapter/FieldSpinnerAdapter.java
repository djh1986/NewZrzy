package com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Field;
import com.zrzyyzt.runtimeviewer.R;

import java.util.List;

public class FieldSpinnerAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//字段
    }

    private List<Field> fieldList =null;
    private Context context;

    public FieldSpinnerAdapter(Context context, List<Field> fieldList) {
        this.fieldList = fieldList;
        this.context = context;
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

        FieldSpinnerAdapter.AdapterHolder holder = new FieldSpinnerAdapter.AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_query_attribute_field_spinner_item, null);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_query_attribute_field_spinner_item_txtName);

        if(fieldList.get(position).getAlias().equalsIgnoreCase("")){
            holder.textView.setText(fieldList.get(position).getName());
        }else{
            holder.textView.setText(fieldList.get(position).getAlias());
        }


        return convertView;
    }
}
