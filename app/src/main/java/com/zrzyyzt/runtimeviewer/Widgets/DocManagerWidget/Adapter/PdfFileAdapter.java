package com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Manager.PdfManager;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.PdfViewerActivity;

import java.util.List;

public class PdfFileAdapter extends BaseAdapter {

    private static final String TAG = "PdfFileAdapter";
    private Context context;
    private List<PdfFileEntity> pdfFileList = null;
    private List<PdfFileEntity> webPdfFileList = null;

    public PdfFileAdapter(Context context, List<PdfFileEntity> webPdfFileEntities) {
        this.context = context;
        this.pdfFileList = pdfFileList;
        this.webPdfFileList = webPdfFileEntities;
    }

    //刷新数据
    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.webPdfFileList.size();
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

        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_pdf_file_item, null);
        TextView textView = convertView.findViewById(R.id.pdfFileName);
        Button btnDownload = convertView.findViewById(R.id.btnPdfDownload);
        Button btnView = convertView.findViewById(R.id.btnPdfView);

        PdfFileEntity webEntity = webPdfFileList.get(position);
        String curPdfFileName = webEntity.getName();

        //设置pdf名称
        textView.setText(curPdfFileName);

        //设置下载路径
        btnDownload.setTag(webEntity.getUrl());

        //设置本地文件路径
        btnView.setTag(webEntity.getFilePath());

        //设置下载按钮是否可用
        btnDownload.setEnabled(!webEntity.isExist());

        //设置查看按钮是否可用
        btnView.setEnabled(webEntity.isExist());
//        if (pdfFileList == null || pdfFileList.size() <= 0) {
//            Log.d(TAG, "getView: 本地未找到文件 " + curPdfFileName);
//            btnDownload.setEnabled(true);
//            btnView.setEnabled(false);
//        } else {
//            PdfFileEntity entity = stream(pdfFileList).firstOrDefault(t -> t.getName().equalsIgnoreCase(curPdfFileName), null);
//
//            if (entity != null) {
//                btnView.setTag(entity.getFilePath());
//                btnDownload.setEnabled(false);
//                btnView.setEnabled(true);
//            } else {
//                Log.d(TAG, "getView: 本地未找到文件 " + curPdfFileName);
//                btnDownload.setEnabled(true);
//                btnView.setEnabled(false);
//            }
//        }
       final  PdfFileEntity pdfFileEntity = webPdfFileList.get(position);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btnPdfDownload) {
                    String url = v.getTag().toString();
                    Log.d(TAG, "onClick: Download " + url);
                    PdfManager pdfManager = new PdfManager(context);
                    String path = pdfManager.downloadWithOkHttp(url);
                    if (!path.isEmpty()) {
                        pdfFileEntity.setExist(true);
                        pdfFileEntity.setFilePath(path);
                        refreshData();
                        Toast.makeText(context, "下载成功", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                String filePath = v.getTag().toString();
                if (id == R.id.btnPdfView) {
                    Intent intent = new Intent(context, PdfViewerActivity.class);
                    intent.putExtra("url", filePath);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

}
