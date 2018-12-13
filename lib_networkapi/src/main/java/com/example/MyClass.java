package com.example;

import android.util.Log;

import com.example.download.ProgressListener;
import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.example.entity.data.AuthData;
import com.example.entity.data.LockInfo;
import com.example.entity.data.LoginEntity;
import com.example.entity.data.VersionInfo;
import com.example.operation.CommonApi;
import com.example.operation.Download;
import com.example.utils.DES;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyClass {
    private static String url;
    private static String token;
//    private static Firmware firmware = null;

    public static void main(String[] args) {
//        SecureRandom secureRandom = new SecureRandom();
//        System.out.println("random integer:"+secureRandom.nextInt(10));
//        System.out.println("integer:" + Integer.valueOf("1234567890"));
//        try {
//            System.out.println(CommonApi.ping().execute().body().toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.print(System.currentTimeMillis());
        byte[] temp = new byte[]{(byte) 0xff, 0x11};
        List<byte[]> list = new ArrayList<>();
        list.add(temp);
        if (list.get(0)[0] == (byte) 0xff) {
            System.out.println("true" + (byte)0xff);
        } else {
            System.out.println("false");
        }
//        try {
//            String result = DES.encryptDES("123456", DES.mypassword());
//            System.out.println(result);
//
//            CommonApi.updatePwdByOldPwd(result, result, result, result, "13534673710").enqueue(new Callback<Result<Object>>() {
//                @Override
//                public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
//                    System.out.println(response.body().toString());
//                }
//
//                @Override
//                public void onFailure(Call<Result<Object>> call, Throwable t) {
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        CommonApi.getVersionInfo().enqueue(new Callback<Result<VersionInfo>>() {
//            @Override
//            public void onResponse(Call<Result<VersionInfo>> call, Response<Result<VersionInfo>> response) {
//                System.out.println(response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result<VersionInfo>> call, Throwable t) {
//
//            }
//        });
//        Download.downloadApk(new ProgressListener() {
//            @Override
//            public void onProgress(long progress, long total, boolean done) {
//                System.out.println("progress:" + progress + " total:" + total + " done:" + done);
//            }
//        }, "http://47.106.151.55:8556/appFile/android/download").enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

//        login();
//        getVerify();
//        regist();
//        resetPwd();
//        fetchLocks();
//        test();


//        CommonApi.getScanResult("ut3362404021").enqueue(new Callback<Result<ScanResult>>() {
//            @Override
//            public void onResponse(Call<Result<ScanResult>> call, Response<Result<ScanResult>> response) {
//                System.out.println(response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result<ScanResult>> call, Throwable t) {
//
//            }
//        });
//        MyRetrofit.get().createPrivateApiService("https://api.douban.com/v2/movie/");
////        Call<Movie> call = PrivateApi.getMovie1("top250", 0, 10);
////        try {
////            Response<Movie> response = call.execute();
////            System.out.println(response.body().toString());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        PrivateApi.getMovie("top250", new Subscriber<Movie>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(Movie o) {
//                System.out.println(o);
//                System.out.println("thread id:" + Thread.currentThread().getId());
//            }
//        }, 0, 10);
////        Call<Result<LoginResult>> call = CommonApi.login("123456","jhs1","123");
////        call.enqueue(new Callback<Result<LoginResult>>() {
////            @Override
////            public void onResponse(Call<Result<LoginResult>> call, Response<Result<LoginResult>> response) {
////                Result<LoginResult> result = response.body();
////                System.out.println(result.toString());
////            }
////
////            @Override
////            public void onFailure(Call<Result<LoginResult>> call, Throwable t) {
////
////            }
////        });
////        Call<Result<LoginResult>> call1 = call.clone();
////        call1.enqueue(new Callback<Result<LoginResult>>() {
////            @Override
////            public void onResponse(Call<Result<LoginResult>> call, Response<Result<LoginResult>> response) {
////                Result<LoginResult> result = response.body();
////                System.out.println(result.toString());
////                url = result.Data.PriHost+"/GuestApi/PrivateCloud/GetSubSystems";
////            }
////
////            @Override
////            public void onFailure(Call<Result<LoginResult>> call, Throwable t) {
////
////            }
////        });
//
//        Call<Result<LoginResult>> call2 = CommonApi.login1("/DeviceApi/Device/Login", "123456", "jhs1", "123");
//        try {
//            Response<Result<LoginResult>> response = call2.execute();
//            Result<LoginResult> result = response.body();
//            System.out.println(result.toString());
//            MyRetrofit.get().createPrivateApiService(result.Data.PriHost);
//            token = result.Data.Token;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        Call<Results<SubSystem>> call = PrivateApi.getSubSystem();
////        call.enqueue(new Callback<Results<SubSystem>>() {
////            @Override
////            public void onResponse(Call<Results<SubSystem>> call, Response<Results<SubSystem>> response) {
////                Results<SubSystem> results = response.body();
////                System.out.println(results.toString());
////            }
////
////            @Override
////            public void onFailure(Call<Results<SubSystem>> call, Throwable t) {
////
////            }
////        });
//
//        Observable<Results<SubSystem>> observable = PrivateApi.getSubSystem1();
//        observable.subscribeOn(Schedulers.io());
//        CompositeSubscription compositeSubscription = new CompositeSubscription();
//        compositeSubscription.add(observable.subscribe(
//                new Observer<Results<SubSystem>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Results<SubSystem> subSystemResults) {
//                        System.out.println(subSystemResults.toString());
//                    }
//                }
//        ));
//
//        Observable<Result<Firmware>> observable1 = CommonApi.getRelease(token, "TC-U9D-H1");
//        compositeSubscription.add(observable1.subscribe(new Observer<Result<Firmware>>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Result<Firmware> firmwareResult) {
//                firmware = firmwareResult.Data;
//                System.out.println(firmwareResult.toString());
//            }
//        }));
////        try{
////            System.exit(0);
////        }finally {
////            System.out.println("test system.exit");
////        }
//        ProgressListener progressListener = new ProgressListener() {
//            @Override
//            public void onProgress(long progress, long total, boolean done) {
//                if (!done)
//                    System.out.println("download percent:" + progress * 100 / total + "%");
////                System.out.println("progress:"+progress+" total:"+total);
//            }
//        };
//        Call<okhttp3.ResponseBody> call = Download.downloadApk(progressListener, firmware.getFilePath());
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                InputStream inputStream = response.body().byteStream();
//                File file = new File("E://download.apk");
//                FileOutputStream fileOutputStream = null;
//                try {
//                    fileOutputStream = new FileOutputStream(file);
//                    int len = 0;
//                    byte[] temp = new byte[1024];
//                    while ((len = inputStream.read(temp)) != -1) {
//                        fileOutputStream.write(temp, 0, len);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (inputStream != null) {
//                            inputStream.close();
//                        }
//                        if (fileOutputStream != null) {
//                            fileOutputStream.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
    }

//    private static void isAuth() {
//        CommonApi.isAuth("A4:34:F1:7A:BB:ED").enqueue(new Callback<Result<Boolean>>() {
//            @Override
//            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
//                System.out.println(response.body().toString());
//                if (response.body().data) {
//                    System.out.println("print true");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
//
//            }
//        });
//    }

//    private static void listAuthUser() {
//        CommonApi.listAuthUser("F0:F8:F2:D2:52:DF").enqueue(new Callback<Results<AuthData>>() {
//            @Override
//            public void onResponse(Call<Results<AuthData>> call, Response<Results<AuthData>> response) {
//                System.out.println(response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Results<AuthData>> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private static void fetchUser() {
//        CommonApi.fetchUser("13534673710").enqueue(new Callback<Result<LoginEntity>>() {
//            @Override
//            public void onResponse(Call<Result<LoginEntity>> call, Response<Result<LoginEntity>> response) {
//                System.out.println(response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result<LoginEntity>> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private static void test() {
//        Set<String> autoOpenLocks = new HashSet<>();
//        autoOpenLocks.add("123");
//        Results<LockInfo> lockInfoResults = new Results<>();
//        lockInfoResults.setCode(2);
//        List<LockInfo> lockInfos = new ArrayList<>();
//        LockInfo lockInfo = new LockInfo();
//        lockInfo.setMac("123");
//        lockInfos.add(lockInfo);
//        lockInfoResults.setData(lockInfos);
//        if (autoOpenLocks != null && autoOpenLocks.size() > 0) {
//            List<LockInfo> temp = lockInfoResults.getData();
//            for (LockInfo info : temp) {
//                if (autoOpenLocks.contains(info.getMac())) {
//                    info.setAutoOpen(true);
//                }
//            }
//        }
//
//        System.out.println(lockInfoResults.toString());
//    }
//
//    private static void fetchLocks() {
//        CommonApi.fetchLocks().enqueue(new Callback<Results<LockInfo>>() {
//            @Override
//            public void onResponse(Call<Results<LockInfo>> call, Response<Results<LockInfo>> response) {
//                System.out.println(response.toString());
//                System.out.println(response.body().toString());
//                System.out.println(new Gson().toJson(response.body()));
//            }
//
//            @Override
//            public void onFailure(Call<Results<LockInfo>> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private static void resetPwd() {
//        try {
//            CommonApi.resetPassword("13534673710", DES.encryptDES("123456", DES.mypassword()), "183254").enqueue(new Callback<Result<Object>>() {
//                @Override
//                public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
//                    System.out.println(response.body().toString());
//                }
//
//                @Override
//                public void onFailure(Call<Result<Object>> call, Throwable t) {
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void regist() {
//        CommonApi.regist("13534673710", "123456", "243821").enqueue(new Callback<Result<Object>>() {
//            @Override
//            public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
//                System.out.println(response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result<Object>> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private static void getVerify() {
//        Call<Result<Object>> call = CommonApi.getVerify("13534673710");
//        call.enqueue(new Callback<Result<Object>>() {
//            @Override
//            public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
//                Result<Object> result = response.body();
//                System.out.println(result.toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result<Object>> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//
//    private static void login() {
//        try {
//            CommonApi.login("13534673710", DES.encryptDES("123456", DES.mypassword())).enqueue(new Callback<Result<LoginEntity>>() {
//                @Override
//                public void onResponse(Call<Result<LoginEntity>> call, Response<Result<LoginEntity>> response) {
////                    Result<LoginEntity> result = response.body();
////                    System.out.println(result.toString());
////                    fetchLocks();
////                    fetchUser();
////                    listAuthUser();
//                    isAuth();
//                }
//
//                @Override
//                public void onFailure(Call<Result<LoginEntity>> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
