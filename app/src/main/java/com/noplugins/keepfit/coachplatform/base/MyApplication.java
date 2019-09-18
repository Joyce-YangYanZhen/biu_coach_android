package com.noplugins.keepfit.coachplatform.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import cn.jpush.android.api.JPushInterface;
import com.noplugins.keepfit.coachplatform.R;
import com.noplugins.keepfit.coachplatform.util.net.callback.ErrorCallback;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.ql0571.loadmanager.core.LoadManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.*;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by shiyujia02 on 2017/8/3.
 */

public class MyApplication extends MultiDexApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    public static List<Integer> list = new ArrayList<>();

    private static MyApplication _application;//本类的实例
    public static OkHttpClient okHttpClient;

    //qcl用来在主线程中刷新ui
    private static Handler mHandler;
    public static String registrationId;
    public static boolean is_first = true;

    public static String[] lvjings = null;

    //保存List集合
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private static Map<String, Activity> destoryMap = new HashMap<>();

    public static UploadManager uploadManager;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.transparent, android.R.color.darker_gray);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;


        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
// 初始化Bugly
//        CrashReport.initCrashReport(context, "注册时申请的APPID", false, strategy);
        CrashReport.initCrashReport(getApplicationContext(), "321595b398", false);
        //初始化handler
        mHandler = new Handler();

        //注册okhttp
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //网络监听（重连）
        LoadManager.beginBuilder()
                .addCallback(new ErrorCallback())
                .commit();

        //初始化友盟分享
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5d2d6979570df3fe04000f97");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo("597832238", "1c6785dbf569cc74c60e24c223741593", "http://sns.whalecloud.com");//微博
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");

        //日志初始化
        Logger.addLogAdapter(new AndroidLogAdapter());

        //初始化七牛云
        Recorder recorder = null;
        String dirPath = "/storage/emulated/0/Download";
        try {
            File f = File.createTempFile("qiniu_xxxx", ".tmp");
            Log.d("qiniu", f.getAbsolutePath().toString());
            dirPath = f.getParent();
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
                //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        uploadManager = new UploadManager(config);// 重用uploadManager。一般地，只需要创建一个uploadManager对象

        /**极光*/
        JPushInterface.init(this);            // 初始化 JPush
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        String rid = JPushInterface.getRegistrationID(getApplicationContext());
        if (!rid.isEmpty()) {
            registrationId = rid;
            Log.e("极光registrationId", registrationId);

            //如果没有缓存的别名，重新获取
            /*if ("".equals(SharedPreferencesHelper.get(this, AppConstants.IS_SET_ALIAS, ""))) {
                //设置别名
                TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                sequence++;
                //设置用户编号为别名
                if ("".equals(SpUtils.getString(getApplicationContext(), AppConstants.USER_NAME))) {
                    tagAliasBean.alias = "null_user_id";
                } else {
                    String user_id = SpUtils.getString(getApplicationContext(), AppConstants.USER_NAME);
                    tagAliasBean.alias = user_id;
                }
                Log.e("设置的alisa",tagAliasBean.alias);
                tagAliasBean.isAliasAction = true;
                tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
                TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
            } else {
                Log.e("已缓存alias", "" + SharedPreferencesHelper.get(this, AppConstants.IS_SET_ALIAS, "").toString());

            }*/

        } else {
            Log.e("极光报错", "Get registration fail, JPush init failed!");
            //Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
        }

        /**方法负载过多解决*/
        MultiDex.install(this);

    }


    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            destoryMap.get(key).finish();
        }
    }

    @Override
    public void onTerminate() {


        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }

    /**
     * 在主线程中刷新UI的方法
     **/
    public static void runOnUIThread(Runnable r) {
        MyApplication.getMainHandler().post(r);
    }

    public static Handler getMainHandler() {
        return mHandler;
    }

    public static MyApplication getApplication() {
        return _application;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
