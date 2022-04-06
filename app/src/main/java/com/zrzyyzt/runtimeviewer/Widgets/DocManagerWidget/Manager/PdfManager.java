package com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.zrzyyzt.runtimeviewer.Config.SystemDirPath;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity.PdfFileEntity;
import com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Thread.DownloadPdfFileThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PdfManager {

    private static final String TAG = "PdfManager";
    private  static final  String PdfDirName = "PdfFiles";

    Context context;
    SystemDirPath systemDirPath;
    public PdfManager(Context context) {
        this.context = context;
        systemDirPath = new SystemDirPath();
    }

    public boolean init() {

        String pdfPath =systemDirPath .getPdfFilePath(context);
        Log.d(TAG, "init: pdfPath" + pdfPath);
        if (!FileUtils.isExist(pdfPath)) {
            boolean isCreateWorkSpacePath = FileUtils.createChildFilesDir(pdfPath); //创建主目录文件夹
            if (isCreateWorkSpacePath) {
                Log.d(TAG, "目录：" + pdfPath + " 创建成功");
            } else {
                Log.d(TAG, "目录：" + pdfPath + " 创建失败,请检查APP是否具备写入权限");
            }
        }
        return false;
    }

    public List<PdfFileEntity> getPdfFiles() {
        List<PdfFileEntity> pdfFileEntities = new ArrayList<PdfFileEntity>();
        String pdfFilePath = systemDirPath.getPdfFilePath(context);
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(pdfFilePath, "pdf");
        if(fileInfos == null || fileInfos.size()<=0) {
            Log.d(TAG, "getPdfFiles: 未找到本地文件");
            return null;
        }
        for (FileUtils.FileInfo fileInfo : fileInfos) {
            pdfFileEntities.add(new PdfFileEntity(0, fileInfo.FileName, fileInfo.FilePath,"",true));
        }
        return pdfFileEntities;
    }

    public String downloadWithOkHttp(String url){
        String pdfFileName =  getFileName(Uri.parse(url));
        String pdfFilePath = systemDirPath.getPdfFilePath(context) + File.separator + pdfFileName;
        final File file = new File(pdfFilePath);
        if(file.exists()){
            Log.d(TAG, "downloadWithOkHttp: 文件已存在");
            return pdfFilePath;
        }

        ExecutorService pool = Executors.newCachedThreadPool();
        DownloadPdfFileThread downloadPdfFileThread = new DownloadPdfFileThread(url, pdfFilePath);
        Future<Boolean> future  = pool.submit(downloadPdfFileThread);

        Boolean temp = false;
        while(true){
            if(future.isDone()){
                try{
                    temp= future.get();
                }catch (Exception e){
                    pdfFileName = "";
                    Log.e(TAG, "getPdfFileEntities: " + e.getMessage());
                }
                pool.shutdown();
                break;
            }
        }

        return pdfFilePath;
    }

    public  boolean download(String url){
        Log.i(TAG, "download: start，url=" + url);
        String pdfFileName =  getFileName(Uri.parse(url));
        Log.d(TAG, "download: fileName -- " + pdfFileName);
        String pdfFilePath = systemDirPath.getPdfFilePath(context) + File.separator + pdfFileName;

        File pdfFile = null;
        URL myFileUrl;

        OutputStream os = null;
        try {

            pdfFile= new File(pdfFilePath);
            pdfFile.createNewFile();
            os = new FileOutputStream(pdfFile);

            myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            byte buffer[]=new byte[4*1024];
            int temp;
            while((temp=is.read(buffer))!=-1){
                os.write(buffer,0,temp);
            }

            os.flush();

        }catch (MalformedURLException ex){

        }catch (IOException ex){

        }finally {
            try {
                os.close();
            }catch (IOException e){

            }

        }
        if(pdfFile==null) return false;

        return true;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
