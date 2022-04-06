package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Adapter.BookmarkListviewAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.BookmarkEntity;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.Extent;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity.SpatialReference;
import com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Manager.BookmarkManager;

import java.util.List;


public class BookmarkWidget extends BaseWidget {

    private static final String TAG = "BookmarkWidget";
    public View bookmarkView;

    public ListView bookmarkListView = null;
    private List<Bookmark> bookmarkList = null;
    private List<BookmarkEntity>  bookmarkEntityList = null;
    private Context context;
    private BookmarkListviewAdapter bookmarkviewAdapter = null;

    private BookmarkManager bookmarkManager = null;

    @Override
    public void active() {
        super.active();
        super.showWidget(bookmarkView);
    }

    @Override
    public void inactive() {
        super.inactive();
    }

    @Override
    public void create() {
        context = super.context;
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        bookmarkView = mLayoutInflater.inflate(R.layout.widget_view_bookmark,null);

        bookmarkManager = new BookmarkManager(projectPath);
        bookmarkEntityList = bookmarkManager.getBookmarkList2();

        if(bookmarkEntityList!=null){
            bookmarkList = bookmarkManager.getEsriBookmarkList(bookmarkEntityList);
        }

        initView();
    }

    private void initView() {
        Log.i(TAG, "initView: " + bookmarkList);
        this.bookmarkListView = (ListView)bookmarkView.findViewById(R.id.widget_view_bookmark_listview);
        bookmarkviewAdapter = new BookmarkListviewAdapter(context, bookmarkEntityList, super.mapView, projectPath);
        this.bookmarkListView.setAdapter(bookmarkviewAdapter);

        TextView btnAdd = (TextView) bookmarkView.findViewById(R.id.widget_view_bookmark_btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("输入收藏书签名称");
                builder.setView(editText);
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Viewpoint currentViewpoint = mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY);
                        //mapview添加书签
//                        bookmarkList.add(new Bookmark(editText.getText().toString(),currentViewpoint));
                        Log.d(TAG, "onClick: extent sp " + currentViewpoint.getTargetGeometry().getSpatialReference().getWkid());
                        //将书签保存到存储
//                        double[] webMercatorCoords = CoordinateConversion.lonLat2WebMercator(currentViewpoint.getTargetGeometry().getExtent().getXMin(),
//                                currentViewpoint.getTargetGeometry().getExtent().getYMin());
//                        double[] webMercatorCoords2 = CoordinateConversion.lonLat2WebMercator(currentViewpoint.getTargetGeometry().getExtent().getXMax(),
//                                currentViewpoint.getTargetGeometry().getExtent().getYMax());
//
//                        int wkid = currentViewpoint.getTargetGeometry().getExtent().getSpatialReference().getWkid();
//                        Extent extent = new Extent(webMercatorCoords[0],
//                                webMercatorCoords[1],
//                                webMercatorCoords2[0],
//                                webMercatorCoords2[1],
//                                new SpatialReference(102100));
                        Envelope envelope = currentViewpoint.getTargetGeometry().getExtent();
                        Extent extent = new Extent(envelope.getXMin(),
                                envelope.getYMin(),
                                envelope.getXMax(),
                                envelope.getYMax(),
                                new SpatialReference(envelope.getSpatialReference().getWkid()));

                        bookmarkEntityList.add(new BookmarkEntity(editText.getText().toString(),extent));
                        String s = bookmarkManager.BookmarkList2String(bookmarkEntityList);
                        boolean flag = bookmarkManager.saveBookmarkListConfig(s);
                        Log.d(TAG, "bookmark2string: save file is" + flag);
                        bookmarkviewAdapter.refreshData();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

//        TextView btnClear = (TextView) bookmarkView.findViewById(R.id.widget_view_bookmark_btn_clear);

//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showShort(context,"clear");
//            }
//        });
    }
}
