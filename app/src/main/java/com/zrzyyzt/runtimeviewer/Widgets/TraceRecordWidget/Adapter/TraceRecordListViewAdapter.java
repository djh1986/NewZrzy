package com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Entity.TraceRecordEntity;
import com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Manager.TraceRecordManager;

import java.util.List;

import gisluq.lib.Util.ToastUtils;

import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.TraceRecordStatusStop;
import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.lineSymbol;
import static com.zrzyyzt.runtimeviewer.Widgets.TraceRecordWidget.Common.Common.ptSym;

public class TraceRecordListViewAdapter extends BaseAdapter {

    private static final String TAG = "TraceRecordListViewAdap";


    public class AdapterHolderTraceRecord{//列表绑定项
        public View itemView;
        public Button btnMore;
        public TextView title;
//        public CheckBox cbxLayer;//图层是否选中
    }

    private Context context;
    private MapView mapView;
    private List<TraceRecordEntity> traceRecordEntityList = null;
    private TraceRecordManager traceRecordManager = null;
    private GraphicsOverlay graphicsOverlay = null;
    private String status;

    public TraceRecordListViewAdapter(Context context, List<TraceRecordEntity> traceRecordEntityList,
                                      MapView mapView, String projectPath, GraphicsOverlay graphicsOverlay,
                                      String status){
        this.context = context;
        this.mapView = mapView;
        this.traceRecordEntityList = traceRecordEntityList;
        this.traceRecordManager = new TraceRecordManager(projectPath);
        this.graphicsOverlay = graphicsOverlay;
        this.status = status;
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        return traceRecordEntityList.size();
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

        if(traceRecordEntityList == null){
            return convertView;
        }

        final int index = traceRecordEntityList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolderTraceRecord holder = new AdapterHolderTraceRecord();

        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_trace_record_item, null);
        holder.itemView = convertView.findViewById(R.id.widget_view_trace_record_item_view);
        holder.title = convertView.findViewById(R.id.widget_view_trace_record_item_title);
        holder.btnMore = convertView.findViewById(R.id.widget_view_trace_record_btnmore);

        String title = traceRecordEntityList.get(index).getName();

        holder.title.setText(title);

        final TraceRecordEntity traceRecordEntity = traceRecordEntityList.get(index);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + status);
                if (!status.equalsIgnoreCase(TraceRecordStatusStop)) {
                    ToastUtils.showLong(context, "轨迹记录已开启，无法查看已有轨迹记录");
                    return;
                }
                graphicsOverlay.getGraphics().clear();
                int i = v.getId();
                if (i == R.id.widget_view_trace_record_item_title) {


                    //                    Log.d(TAG, "onClick: minx" + bookmark.getExtent().getXmin());
                    Envelope envelope = new Envelope(
                            traceRecordEntity.getExtent().getXmin(),
                            traceRecordEntity.getExtent().getYmin(),
                            traceRecordEntity.getExtent().getXmax(),
                            traceRecordEntity.getExtent().getYmax(),
                            SpatialReference.create(traceRecordEntity.getExtent().getSpatialReference().getWkid()));

                    Viewpoint vp = new Viewpoint(envelope);
                    mapView.setViewpointAsync(vp);

                    String coordStrs = traceRecordEntity.getCoords();
                    String[] pointStrs = coordStrs.split(",");
                    PointCollection points = new PointCollection(SpatialReference.create(4490));
                    for (String pointStr : pointStrs
                    ) {
                        String[] xyCoord = pointStr.split(" ");
                        Point point = new Point(Double.parseDouble(xyCoord[0]), Double.parseDouble(xyCoord[1]));
                        points.add(point);
                        Graphic graphic = new Graphic(point, ptSym);
                        graphicsOverlay.getGraphics().add(graphic);
                    }
                    Polyline polyline = new Polyline(points);
                    graphicsOverlay.getGraphics().add(new Graphic(polyline, lineSymbol));


                }
            }
        });

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if(i==R.id.widget_view_trace_record_btnmore){
//                    Log.d(TAG, "onClick: minx" + bookmark.getExtent().getXmin());
                    PopupMenu pm = new PopupMenu(context, v);
                    pm.getMenuInflater().inflate(R.menu.menu_bookmark_tools, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_bookmark_tools_delete://删除
                                    traceRecordEntityList.remove(index);
                                    String content = traceRecordManager.TraceRecordList2String(traceRecordEntityList);
                                    traceRecordManager.saveTraceRecordListConfig(content);
                                    refreshData();
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    pm.show();
                }
            }
        });

        return convertView;
    }
}
