package com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.Config.AppConfig;
import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Adapter.PdfFileAdapter;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Manager.PdfManager;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Thread.GetPdfFileThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocManagerWidget extends BaseWidget {

    private static final String TAG = "DocManagerWidget";
    public View docManagerView;
    private final static int REQUEST_CODE = 42;
//    public static final int PERMISSION_CODE = 42042;
    PdfManager pdfManager;
    private Context context;

    public ListView pdfFileListView = null;
    private List<PdfFileEntity> webPdfFileEntities= null;

    PdfFileAdapter pdfFileAdapter = null;
    public static ConfigEntity configEntity = null;

    @Override
    public void active() {
        super.active();
        super.showWidget(docManagerView);
    }

    @Override
    public void inactive() {
        super.inactive();
    }

    @Override
    public void create() {
        context = super.context;
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        docManagerView = mLayoutInflater.inflate(R.layout.widget_view_pdf_file_list1,null);

        if (configEntity==null){
            configEntity = AppConfig.getConfig(context);
        }

        //获取网络pdf文件
        webPdfFileEntities = getWebPdfFileEntities();
        if(webPdfFileEntities == null){
            Log.d(TAG, "onCreate: 获取网络pdf文件列表失败");
            return;
        }

        //判断是否有权限
//        int permissionCheck = ContextCompat.checkSelfPermission(context,
//                "android.permission.WRITE_EXTERNAL_STORAGE");
//        Log.d(TAG, "onCreate: " + permissionCheck);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    context,
//                    new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
//                    PERMISSION_CODE
//            );
//            return;
//        }

        //获取本地pdf文件
        pdfManager  = new PdfManager(context);
        pdfManager.init();
        List<PdfFileEntity> pdfFiles = pdfManager.getPdfFiles();

        for (PdfFileEntity webEntity: webPdfFileEntities
        ) {
            if(pdfFiles==null){
                webEntity.setExist(false);
                continue;
            }
            for (PdfFileEntity entity:pdfFiles
                 ) {
                if(entity.getName().equalsIgnoreCase(webEntity.getName())){
                    webEntity.setExist(true);
                    webEntity.setFilePath(entity.getFilePath());
                    break;
                }
            }
        }
        initView();
    }
    private void initView() {
        //TODO 网络pdf列表未空处理
        Log.i(TAG, "initView: " + webPdfFileEntities);
        this.pdfFileListView = (ListView)docManagerView.findViewById(R.id.widget_view_pdf_pdfListView);
        pdfFileAdapter  = new PdfFileAdapter(context, webPdfFileEntities);
        this.pdfFileListView.setAdapter(pdfFileAdapter);
    }

    private List<PdfFileEntity> getWebPdfFileEntities(){
        List<PdfFileEntity> temp = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        GetPdfFileThread getPdfFileThread = new GetPdfFileThread(temp,configEntity.getIpAddress());
        Future<List<PdfFileEntity>> future  = pool.submit(getPdfFileThread);
        while(true){
            if(future.isDone()){
                try{
                    temp= future.get();
                }catch (Exception e){
                    Log.e(TAG, "getPdfFileEntities: " + e.getMessage());
                }
                pool.shutdown();
                break;
            }
        }
        Log.d(TAG, "getWebPdfFileEntities: "+ temp);
        return temp;
    }


}
