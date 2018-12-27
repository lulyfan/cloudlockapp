package com.ut.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.operation.MyRetrofit;
import com.ut.base.UIUtils.SystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author : chenjiajun
 * time   : 2018/12/26
 * desc   :
 */
public class VersionUpdateHelper {

    private static final String DEFAULT_DOWNLOAD_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String APP_NAME = "cloudLock.apk";


    @SuppressLint("CheckResult")
    public static void updateVersion(Context context, final String currentVersion, final UpdateCallback callback) {
        MyRetrofit.get().getCommonApiService().updateVersion()
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        VersionInfo versionInfo = JSON.parseObject(result.data, VersionInfo.class);
                        int compare = SystemUtils.compareVersions(currentVersion, versionInfo.getVersion());

                        if (callback != null) {
                            callback.needToUpdate(compare == 1);
                        }

                        if (compare == 1) {
//                            download(context, versionInfo.getFileUrl(), "", versionInfo.getDescription());
                            download(versionInfo.getFileUrl());
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

        if (!canDownloadState(context)) {
            return -1;
        }

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
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DCIM, "cloudLock" + File.separator + APP_NAME);

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


    /**
     * 检测用户是否禁用了下载服务
     *
     * @param context
     * @return
     */
    private static boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static Notification.Builder builder = null;

    public static void download(String url) {
        if (builder == null) {
            builder = new Notification.Builder(BaseApplication.getAppContext())
                    .setContentTitle("下载")
                    .setContentText("正在等待...")
                    .setSmallIcon(R.mipmap.icon_right_arrow_black)
                    .setOngoing(true)
                    .setProgress(100, 0, true);
        }
        final NotificationManager manager = (NotificationManager) BaseApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(666, builder.build());
        }
        MyRetrofit.get().getDownloadPublicService((progress, total, done) ->
                {
                    if (done) {
                        File apkFile = new File(DEFAULT_DOWNLOAD_FILE_PATH + File.separator + APP_NAME);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
                            Uri contentUri = FileProvider.getUriForFile(BaseApplication.getAppContext(),"com.ut.module_mine.fileprovider",apkFile);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(contentUri,"application/vnd.android.package-archive");

                        }else{
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
                        }
                        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
                        PendingIntent pendingIntent = PendingIntent.getActivity(BaseApplication.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);
                        builder.setOngoing(false);
                    }

                    builder.setContentText("已下载 " + String.valueOf(progress * 100 / total) + "%");
                    builder.setProgress(100, (int) (progress * 100 / total), false);
                    if (manager != null) {
                        manager.notify(666, builder.build());
                    }
                }
        )
                .downloadApk(url)
                .enqueue(new Callback<ResponseBody>() {
                             @Override
                             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                 FileOutputStream fos = null;
                                 InputStream is = null;
                                 BufferedInputStream bis = null;
                                 try {
                                     if (response.body() == null) return;
                                     is = response.body().byteStream();
                                     File file = new File(Environment.getExternalStorageDirectory(), APP_NAME);
                                     fos = new FileOutputStream(file);
                                     bis = new BufferedInputStream(is);
                                     byte[] buffer = new byte[1024];
                                     int len;
                                     while ((len = bis.read(buffer)) != -1) {
                                         Log.d("len", len + "");
                                         fos.write(buffer, 0, len);
                                         fos.flush();
                                     }

                                     Log.d("file", file.getAbsolutePath());
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 } finally {
                                     try {
                                         assert fos != null;
                                         fos.close();
                                         assert bis != null;
                                         bis.close();
                                         is.close();
                                     } catch (Exception e1) {
                                         e1.printStackTrace();
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<ResponseBody> call, Throwable t) {
                                 t.printStackTrace();
                             }


                         }
                );
    }

    public interface UpdateCallback {
        void needToUpdate(boolean result);
    }
}
