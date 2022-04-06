package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.BookmarkEntity;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Manager.BookmarkManager;

import java.util.List;

public class BookmarkListviewAdapter extends BaseAdapter {

    private static final String TAG = "BookmarkviewAdapter";


    public class AdapterHolderBookemark{//列表绑定项
        public View itemView;
        public Button btnMore;
        public TextView title;
//        public CheckBox cbxLayer;//图层是否选中
    }

    private Context context;
    private MapView mapView;
    private List<BookmarkEntity> bookmarkList = null;
    private BookmarkManager bookmarkManager = null;

    public BookmarkListviewAdapter(Context context,List<BookmarkEntity> bookmarkList, MapView mapView,String projectPath){
        this.context = context;
        this.mapView = mapView;
        this.bookmarkList = bookmarkList;
        this.bookmarkManager = new BookmarkManager(projectPath);
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        return bookmarkList.size();
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

        if(bookmarkList == null){
            return convertView;
        }

        final int index = bookmarkList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolderBookemark holder = new AdapterHolderBookemark();

        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_bookmark_item, null);
        holder.itemView = convertView.findViewById(R.id.widget_view_bookmark_item_view);
        holder.title = convertView.findViewById(R.id.widget_view_bookmark_item_title);
        holder.btnMore = convertView.findViewById(R.id.widget_view_bookmark_btnmore);

        String title = bookmarkList.get(index).getName();

        holder.title.setText(title);

        final BookmarkEntity bookmark = bookmarkList.get(index);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if(i==R.id.widget_view_bookmark_item_title){
//                    Log.d(TAG, "onClick: minx" + bookmark.getExtent().getXmin());
                    Envelope envelope = new Envelope(bookmark.getExtent().getXmin(),
                            bookmark.getExtent().getYmin(),
                            bookmark.getExtent().getXmax(),
                            bookmark.getExtent().getYmax(),
                            SpatialReference.create(bookmark.getExtent().getSpatialReference().getWkid()));

                    Viewpoint vp = new Viewpoint(envelope);
                    mapView.setViewpointAsync(vp);
                }
            }
        });

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if(i==R.id.widget_view_bookmark_btnmore){
//                    Log.d(TAG, "onClick: minx" + bookmark.getExtent().getXmin());
                    PopupMenu pm = new PopupMenu(context, v);
                    pm.getMenuInflater().inflate(R.menu.menu_bookmark_tools, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_bookmark_tools_delete://删除
                                    bookmarkList.remove(index);
                                    String content = bookmarkManager.BookmarkList2String(bookmarkList);
                                    bookmarkManager.saveBookmarkListConfig(content);
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
