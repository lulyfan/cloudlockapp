package com.ut.base;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.example.operation.MyRetrofit;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DownloadUtil;
import com.ut.base.Utils.UTLog;
import com.ut.commoncomponent.CLToast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
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

    private static final String APP_NAME = "cloudLock.apk";
    private static Notification.Builder builder = null;

    @SuppressLint("CheckResult")
    public static void updateVersion(Context context, long currentVersion, final UpdateCallback callback) {
        MyRetrofit.get().getCommonApiService().updateVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        VersionInfo versionInfo = JSON.parseObject(result.data.toString(), VersionInfo.class);

                        int remoteVersion = Integer.parseInt(versionInfo.getExtProps());
                        boolean isNeedUpdate = currentVersion < remoteVersion ? true : false;
                        if (callback != null) {
                            callback.needToUpdate(isNeedUpdate, versionInfo.getVersion(), versionInfo.getFileUrl());
                        }

                        if (isNeedUpdate) {
                            //todo
                            new AlertDialog.Builder(AppManager.getAppManager().currentActivity())
                                    .setMessage(R.string.mine_version_update_tips)
                                    .setPositiveButton(R.string.mine_version_update_comfirm, ((dialog, which) -> {
                                        download(context, versionInfo.getFileUrl(), versionInfo.getDescription());
                                        CLToast.showAtBottom(BaseApplication.getAppContext(), BaseApplication.getAppContext().getString(R.string.mine_version_update_toast));
                                    }))
                                    .setNegativeButton(R.string.mine_version_update_dont, null)
                                    .show();

                        } else {
                            CLToast.showAtBottom(BaseApplication.getAppContext(), BaseApplication.getAppContext().getString(R.string.mine_version_update_is_newest_version));
                        }

                    }
                }, new ErrorHandler());
    }

    public static void download(Context context, String url, String appName) {
        DownloadUtil downloadUtil = new DownloadUtil(context);
        downloadUtil.downloadAPK(url, appName);
    }

    public static void download(String url) {
        if (builder == null) {
            //todo
            builder = new Notification.Builder(BaseApplication.getAppContext())
                    .setContentTitle(BaseApplication.getAppContext().getString(R.string.mine_version_update_title))
                    .setContentText(BaseApplication.getAppContext().getString(R.string.mine_version_update_waiting))
                    .setSmallIcon(R.mipmap.loading_icon)
                    .setOngoing(true)
                    .setProgress(100, 0, true);
        }
        final NotificationManager manager = (NotificationManager) BaseApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(666, builder.build());
        }
        MyRetrofit.get().getDownloadPublicService((progress, total, done) ->
                {
                    builder.setContentText(BaseApplication.getAppContext().getString(R.string.mine_version_update_has_downloaded) + String.valueOf(progress * 100 / total) + "%");

                    if (done) {
                        File apkFile = createApkFile();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri contentUri = FileProvider.getUriForFile(BaseApplication.getAppContext(), "com.ut.module_mine.fileprovider", apkFile);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

                        } else {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        PendingIntent pendingIntent = PendingIntent.getActivity(BaseApplication.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);
                        builder.setOngoing(false);
                        builder.setContentText(BaseApplication.getAppContext().getString(R.string.mine_version_update_install));
                    }
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

                                     File file = createApkFile();

                                     fos = new FileOutputStream(file);
                                     bis = new BufferedInputStream(is);
                                     byte[] buffer = new byte[1024];
                                     int len;
                                     while ((len = bis.read(buffer)) != -1) {
                                         fos.write(buffer, 0, len);
                                         fos.flush();
                                     }
                                     UTLog.d("download success !!!", file.getAbsolutePath());
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

    public static File createApkFile() {
        String path = BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator + "Download";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File apkFile = new File(file.getPath(), APP_NAME);
        if (!apkFile.exists()) {
            try {
                apkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return apkFile;
    }

    public interface UpdateCallback {
        void needToUpdate(boolean isNeedToUpdate, String serviceVersion, String fileUrl);
    }
}
