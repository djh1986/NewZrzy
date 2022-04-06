package com.zrzyyzt.runtimeviewer.BMOD.RootAct;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.zrzyyzt.runtimeviewer.BMOD.MapModule.View.MapActivity;
import com.zrzyyzt.runtimeviewer.BMOD.SystemModule.LockviewActivity;
import com.zrzyyzt.runtimeviewer.Config.AppWorksSpaceInit;
import com.zrzyyzt.runtimeviewer.Config.SystemDirPath;
import com.zrzyyzt.runtimeviewer.Permission.PermissionsActivity;
import com.zrzyyzt.runtimeviewer.Permission.PermissionsChecker;
import com.zrzyyzt.runtimeviewer.R;
import com.zrzyyzt.runtimeviewer.Utils.FileUtils;

import java.util.List;

import gisluq.lib.LockPatternView.LockPatternView;
import gisluq.lib.Util.AppUtils;

/**
 *  应用程序初始化页面
 */
public class InitActivity extends AppCompatActivity implements LockPatternView.OnPatternListener {

    private static String TAG = "InitActivity";
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟时间
    private Context context = null;

    private static final int REQUEST_CODE = 0; // 请求码

    //锁屏
    private LockPatternView lock_pattern = null;
    private TextView titleTxt = null;

    private static String txtTag0 = "请输入解锁手势";
    private static String txtTag1 = "创建解锁手势";
    private static String txtTag2 = "再次输入解锁手势";
    private static String txtTag3 = "解锁手势设置完成";

    private int tagIndex = 0;

    private String lockViewMD = null;

    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写入存储
            Manifest.permission.ACCESS_FINE_LOCATION,//位置信息
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA //相机
    };
    private static PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_init);

        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
            appInit();
        }

        this.lock_pattern = (LockPatternView)this.findViewById(R.id.activity_lock_view_pattern);
        this.lock_pattern.setLockDefaultColorDeep(true);
        this.lock_pattern.setOnPatternListener(this);

        this.titleTxt = (TextView)this.findViewById(R.id.activity_lock_view_title);

        //检查是否存在密码
        String SysConf = SystemDirPath.getLockViewConfPath(context);
        boolean isHave = FileUtils.isExist(SysConf);
        if(isHave){
            this.titleTxt.setText(txtTag0);//输入解锁手势
            tagIndex=0;
        }else{
            this.titleTxt.setText(txtTag1);//创建解锁手势
            tagIndex=1;
        }

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                try {
                    //判断是否为平板设备
//                    boolean ispad = SysUtils.isPad(context);
//                    if (ispad){
//                        startActivity();
//                    }else{
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage("检测到当前设备并非平板，继续安装此应用程序将会出现异常，是否任然继续安装此应用程序？");
//                        builder.setTitle("系统提示");
//                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                InitActivity.this.finish();
//                            }
//                        });
//                        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity();
//                                dialog.dismiss();
//                                ToastUtils.showShort(context,"应用程序打开失败，请使用平台后再试");
//                            }
//                        });
//                        builder.setCancelable(false);//点击外部不消失
//                        builder.create().show();
//                    }
//                }catch (Exception e){
//                    Log.e(TAG,e.toString());
//                }
//            }
//        }, SPLASH_DISPLAY_LENGHT);

        TextView textView = (TextView)this.findViewById(R.id.activity_init_versionTxt);
        String version = AppUtils.getVersionName(this);
        textView.setText("版本号:"+version);

        //获取dpi
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //textView.setText("版本号: 密度" +dm.density +",dpi "+ dm.densityDpi);
        textView.setText("版本号: "+"V-0.2");
    }

    /**
     * 跳转
     */
    private void startActivity() {
//        Intent mainIntent = new Intent(context,MainActivity.class);
//        context.startActivity(mainIntent);
//        ((Activity)context).finish();

        Intent mainIntent = new Intent(context, LockviewActivity.class);
        context.startActivity(mainIntent);
        ((Activity)context).finish();

//        String passwordStr = PreferenceUtil.getGesturePassword(InitActivity.this);
//        Intent intent;
//        if (passwordStr == "") {
//            Log.d("TAG", "true");
//            intent = new Intent(InitActivity.this, LockviewActivity.class);
//        } else {
//            intent = new Intent(InitActivity.this, LoginActivity.class);
//        }
//        context.startActivity(intent);
//        ((Activity)context).finish();


    }

    /**
     *  应用程序初始化
     */
    private void appInit() {
        boolean isOk = AppWorksSpaceInit.init(context);//初始化系统文件夹路径
    }

    /**
     * 弹出权限获取提示信息
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }



    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        String lockView = LockPatternView.patternToString(pattern);
        if (tagIndex==0){
            String SysConf = SystemDirPath.getLockViewConfPath(context);
            String Md = FileUtils.openTxt(SysConf,"GB2312");
            if(Md.equals(lockView)){
                Toast.makeText(context, "解锁验证通过", Toast.LENGTH_SHORT).show();
//                this.titleTxt.setText(txtTag1);

//                Intent mainIntent = new Intent(context, MainActivity.class);
//                context.startActivity(mainIntent);
//                ((Activity)context).finish();

                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("DirName","01-测试工程示例");
                intent.putExtra("DirPath","/storage/emulated/0/RuntimeViewer/Projects/01-测试工程示例");
                context.startActivity(intent);
                finish();
            }else{
                tagIndex=-1;
                Toast.makeText(context, "解锁验证失败", Toast.LENGTH_SHORT).show();
            }
        }

        if(tagIndex==1){
            this.lockViewMD = lockView;
            this.titleTxt.setText(txtTag2);
        }else if(tagIndex==2){
            String md = LockPatternView.patternToString(pattern);
            if(md.equals(lockViewMD)){
                this.titleTxt.setText(txtTag3);
                tagIndex=0;
                setLockViewMD(md);
            }else{
                tagIndex=0;
                Toast.makeText(context, "两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        titleTxt.setText(txtTag1);
                    }
                }, 1200);
            }
        }
        tagIndex++;
        this.lock_pattern.clearPattern();
    }

    /**
     * 设置登陆秘钥
     * @param lockViewMD
     */
    private void setLockViewMD(String lockViewMD) {
        if(lockViewMD!=null){
            //创建系统配置文件Sys.conf
            String SysConf = SystemDirPath.getLockViewConfPath(context);
            FileUtils.saveTxt(SysConf, lockViewMD);
            Toast.makeText(context, "解锁图案设置成功", Toast.LENGTH_SHORT).show();
            this.setResult(11);
        }else {
            Toast.makeText(context, "解锁图案设置失败", Toast.LENGTH_SHORT).show();
            this.setResult(10);
        }

        this.finish();
    }
}
