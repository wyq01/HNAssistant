/**
 * Summary: js脚本所能执行的函数空间
 */
package com.movitech.HNAInfrastructure.assistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.movitech.HNAInfrastructure.assistant.util.FileCacheUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SafeWebViewBridge.JsCallback;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

//HostJsScope中需要被JS调用的函数，必须定义成public static，且必须包含WebView这个参数
public class HostJsScope {

    /**
     * @param webView
     * @param version       版本号
     * @param url           下载地址
     * @param isForceUpdate 是否强制升级：0为否，1为是
     * @param title         对话框标题
     * @param message       对话框信息
     */
//    public static void updateApp(WebView webView, String version, final String url, int isForceUpdate, String
// title, String message) {
//        final Context context = webView.getContext();
//        if (!getAppVersionName(context).equals(version)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle(title)
//                    .setMessage(message)
//                    .setCancelable(!(isForceUpdate == 1))
//                    .setNeutralButton(R.string.download_now, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            context.startActivity(intent);
//                            ((Activity) context).finish();
//                        }
//                    }).create().show();
//        }
//    }
    public static void showTaskCalendarConfirm(WebView webView, String name, String url) {
        Context context = webView.getContext();
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void clearCache(WebView webView) {
        Log.i("msg", "clearCache");
        Context context = webView.getContext();
        FileCacheUtils.cleanInternalCache(context);
        FileCacheUtils.cleanExternalCache(context);
        FileCacheUtils.cleanDatabases(context);
        FileCacheUtils.cleanSharedPreference(context);
        FileCacheUtils.cleanFiles(context);
        FileCacheUtils.cleanDatabaseByName(context, "webview.db");
        FileCacheUtils.cleanDatabaseByName(context, "webview.db-shm");
        FileCacheUtils.cleanDatabaseByName(context, "webview.db-wal");
        FileCacheUtils.cleanDatabaseByName(context, "webviewCache.db");
        FileCacheUtils.cleanDatabaseByName(context, "webviewCache.db-shm");
        FileCacheUtils.cleanDatabaseByName(context, "webviewCache.db-wal");

        webView.clearHistory();
        webView.clearFormData();
        webView.clearMatches();
        webView.clearSslPreferences();
        webView.clearCache(true);
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().removeExpiredCookie();
        webView.getContext().getCacheDir().delete();
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    private static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * 可选择时间长短的气泡提醒
     *
     * @param webView    浏览器
     * @param message    提示信息
     * @param isShowLong 提醒时间方式
     */
    public static void toast(WebView webView, String message, int isShowLong) {
        Toast.makeText(webView.getContext(), message, isShowLong).show();
    }

    /**
     * 弹出记录的测试JS层到Java层代码执行损耗时间差
     *
     * @param webView   浏览器
     * @param timeStamp js层执行时的时间戳
     */
    public static void testLossTime(WebView webView, long timeStamp) {
        timeStamp = System.currentTimeMillis() - timeStamp;
        alert(webView, String.valueOf(timeStamp));
    }

    /**
     * 系统弹出提示框
     *
     * @param webView 浏览器
     * @param message 提示信息
     */
    public static void alert(WebView webView, String message) {
        Log.i("msg", "msg " + message);
        // 构建一个Builder来显示网页中的alert对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(webView.getContext());
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    public static void alert(WebView webView, int msg) {
        alert(webView, String.valueOf(msg));
    }

    public static void alert(WebView webView, boolean msg) {
        alert(webView, String.valueOf(msg));
    }

    /**
     * 获取设备IMSI
     *
     * @param webView 浏览器
     * @return 设备IMSI
     */
    public static String getIMSI(WebView webView) {
        return ((TelephonyManager) webView.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    /**
     * 获取用户系统版本大小
     *
     * @param webView 浏览器
     * @return 安卓SDK版本
     */
    public static int getOsSdk(WebView webView) {
        return Build.VERSION.SDK_INT;
    }

    //---------------- 界面切换类 ------------------

    /**
     * 结束当前窗口
     *
     * @param view 浏览器
     */
    public static void goBack(WebView view) {
        if (view.getContext() instanceof Activity) {
            ((Activity) view.getContext()).finish();
        }
    }

    /**
     * 传入Json对象
     *
     * @param view 浏览器
     * @param jo   传入的JSON对象
     * @return 返回对象的第一个键值对
     */
    public static String passJson2Java(WebView view, JSONObject jo) {
        Iterator iterator = jo.keys();
        String res = null;
        if (iterator.hasNext()) {
            try {
                String keyW = (String) iterator.next();
                res = keyW + ": " + jo.getString(keyW);
            } catch (JSONException je) {

            }
        }
        return res;
    }

    /**
     * 将传入Json对象直接返回
     *
     * @param view 浏览器
     * @param jo   传入的JSON对象
     * @return 返回对象的第一个键值对
     */
    public static JSONObject retBackPassJson(WebView view, JSONObject jo) {
        return jo;
    }

    public static int overloadMethod(WebView view, int val) {
        return val;
    }

    public static String overloadMethod(WebView view, String val) {
        return val;
    }

    public static class RetJavaObj {
        public int intField;
        public String strField;
        public boolean boolField;
    }

    public static List<RetJavaObj> retJavaObject(WebView view) {
        RetJavaObj obj = new RetJavaObj();
        obj.intField = 1;
        obj.strField = "mine str";
        obj.boolField = true;
        List<RetJavaObj> rets = new ArrayList<RetJavaObj>();
        rets.add(obj);
        return rets;
    }

    public static void delayJsCallBack(WebView view, int ms, final String backMsg, final JsCallback jsCallback) {
        TaskExecutor.scheduleTaskOnUiThread(ms * 1000, new Runnable() {
            @Override
            public void run() {
                try {
                    jsCallback.apply(backMsg);
                } catch (JsCallback.JsCallbackException je) {
                    je.printStackTrace();
                }
            }
        });
    }

    public static long passLongType(WebView view, long i) {
        return i;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 清除app缓存
     */
    private static void clearAppCache(Activity activity) {
        // 清除webview缓存
        @SuppressWarnings("deprecation")
        File file = activity.getCacheDir();
        // 先删除WebViewCache目录下的文件
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        File file2 = activity.getExternalCacheDir();
        if (file2 != null && file2.exists() && file2.isDirectory()) {
            for (File item : file2.listFiles()) {
                item.delete();
            }
            file2.delete();
        }
        activity.deleteDatabase("webview.db");
        activity.deleteDatabase("webview.db-shm");
        activity.deleteDatabase("webview.db-wal");
        activity.deleteDatabase("webviewCache.db");
        activity.deleteDatabase("webviewCache.db-shm");
        activity.deleteDatabase("webviewCache.db-wal");
        //清除数据缓存
        clearCacheFolder(activity.getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(activity.getCacheDir(), System.currentTimeMillis());
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    private static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
}