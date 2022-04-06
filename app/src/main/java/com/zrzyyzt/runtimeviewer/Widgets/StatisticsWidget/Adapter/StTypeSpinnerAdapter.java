package com.zrzyyzt.runtimeviewer.Widgets.StatisticsWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zrzyyzt.runtimeviewer.R;

import java.util.List;

public class StTypeSpinnerAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//字段
    }

    private List<String> typeList =null;
    private Context context;

    public StTypeSpinnerAdapter(Context c,List<String>list){
        context=c;
        typeList=list;
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public Object getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = typeList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_ststistics_type_spinner_item, null);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_statistics_type_spinner_item_txtName);

        holder.textView.setText(typeList.get(position));

        return convertView;
    }
}
