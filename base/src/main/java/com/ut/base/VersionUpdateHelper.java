package com.ut.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import com.alibaba.fastjson.JSON;
import com.example.operation.MyRetrofit;
import com.ut.base.UIUtils.SystemUtils;

import java.io.File;

import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/26
 * desc   :
 */
public class VersionUpdateHelper {

    private static final String DEFAULT_DOWNLOAD_FILE_PATH = Environment.getDownloadCacheDirectory().getAbsolutePath();
    private static final String APP_NAME = "cloudLock.apk";


    @SuppressLint("CheckResult")
    public static void updateVersion(Context context, final String currentVersion, final UpdateCallback callback) {
        MyRetrofit.get().getCommonApiService().updateVersion()
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        VersionInfo versionInfo = JSON.parseObject(result.data, VersionInfo.class);
                        int compare = SystemUtils.compareVersions(currentVersion, versionInfo.getVersion());

                        if(callback != null) {
                            callback.needToUpdate(compare == 1);
                        }

                        if ( compare == 1) {
                            download(context, versionInfo.getFileUrl(), "", versionInfo.getDescription());
                        }

                    }
                }, new ErrorHandler());
    }


    /**
     * 比较实用的升级版下载功能
     *
     * @param url   下载地址
     * @param title 文件名字
     * @param desc  文件路径
     */
    public static long download(Context context, String url, String title, String desc) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long ID = -1;

        //以下两行代码可以让下载的apk文件被直接安装而不用使用Fileprovider,系统7.0或者以上才启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(localBuilder.build());
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 仅允许在WIFI连接情况下下载
//        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        // 通知栏中将出现的内容
        request.setTitle(title);
        request.setDescription(desc);

        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }

        //制定下载的文件类型为APK
        request.setMimeType("application/vnd.android.package-archive");

        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 指定下载文件地址，使用这个指定地址可不需要WRITE_EXTERNAL_STORAGE权限。
        request.setDestinationUri(Uri.fromFile(new File(DEFAULT_DOWNLOAD_FILE_PATH + File.separator + APP_NAME)));

        //大于11版本手机允许扫描
        //表示允许MediaScanner扫描到这个文件，默认不允许。
        request.allowScanningByMediaScanner();

        Activity activity = AppManager.getAppManager().currentActivity();
        if (activity != null) {
            activity.startActivity(new android.content.Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));//启动系统下载界面
        }

        if (downloadManager != null) {
            ID = downloadManager.enqueue(request);
        }
        return ID;
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(Context context, long downloadId) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public interface UpdateCallback{
        void needToUpdate(boolean result);
    }
}
