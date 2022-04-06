package com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseMap;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.arcgisruntime.layers.Layer;
import com.zrzyyzt.runtimeviewer.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 图表列表
 * Created by gis-luq on 2018/4/25.
 */
public class BaseMapLayerImageViewAdapter extends BaseAdapter {

    private static final String TAG = "BaseMapLayerImageViewAdapter";
    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
//        public TextView textView;

    }

    private List<Layer> layerList =null;
    private Context context;
    private List<BasemapLayerInfo> mBasemapLayerInfoList = null;
    public BaseMapLayerImageViewAdapter(Context c, List<Layer> list, List<BasemapLayerInfo> basemapLayerInfoList) {
        this.layerList = list;
        this.context = c;
        this.mBasemapLayerInfoList = basemapLayerInfoList;
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        return layerList.size();
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

        final int index = layerList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.basemap_layer_image_item, null);
        holder.imageView = convertView.findViewById(R.id.basemap_image);
//        holder.textView = convertView.findViewById(R.id.basemap_name);
        String layerName =layerList.get(index).getName();
//        holder.textView.setText(layerName);

        holder.imageView.setTag(layerName);
        String iconName = getIconName(layerName);
        if (iconName!=null){
            InputStream is = null;
            try {
                is = context.getAssets().open(iconName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if ( holder.imageView !=null){
                holder.imageView.setImageBitmap(bitmap);
            }
        }
        final Layer layer= layerList.get(index);//按照倒序

        holder.imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Layer layer:layerList
                     ) {
                    layer.setVisible(false);
                }
                for (Layer layer:layerList
                     ) {
                    if(layer.getName().equalsIgnoreCase(v.getTag().toString())){
                        layer.setVisible(true);
                        break;
                    }
                }
            }
        });
//        holder.cbxLayer.setChecked(layer.isVisible());//设置是否显示
//
//        holder.cbxLayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    layer.setVisible(true);
//                    Log.d(TAG, layer.getName()+","+layer.isVisible());
//                }else{
//                    layer.setVisible(false);
//                    Log.d(TAG, layer.getName()+","+layer.isVisible());
//                }
//            }
//        });
//        holder.btnMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu pm = new PopupMenu(context, v);
//                pm.getMenuInflater().inflate(R.menu.menu_layer_tools, pm.getMenu());
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.menu_layer_tools_opacity://图层透明度
//                                ShowOpacityUtilView(layer);
//                                break;
//                            default:
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                pm.show();
//            }
//        });

        return convertView;
    }

    private String getIconName(String name) {
        String iconName = null;
        for (BasemapLayerInfo info :mBasemapLayerInfoList
             ) {
            if(info.Name == name){
                iconName = info.Icon;
                break;
            }
        }
        return iconName;
    }


    /**
     * 显示透明度操作View
     */
    private void ShowOpacityUtilView(final Layer layer){

        AlertDialog opacityDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate( R.layout.widget_alert_opacity, null);
        TextView txtTitle = (TextView)view.findViewById(R.id.opactiy_element_layout_layerName);

        txtTitle.setText(layer.getName());
        final TextView txtOp = (TextView)view.findViewById(R.id.opactiy_element_layout_layerOpacity);
        float op = layer.getOpacity();
        txtOp.setText(String.valueOf(op));
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.opactiy_element_layout_layerOpactiySeekBar);
        seekBar.setMax(100);
        seekBar.setProgress((int) (op*100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float op = (float)progress/100;
                txtOp.setText(String.valueOf(op));
                layer.setOpacity(op);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        opacityDialog.setView(view);
        opacityDialog.show();
    }
}
