package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.database.entity.User;
import com.ut.module_mine.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditUserInfoViewModel extends AndroidViewModel {

    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> registTime = new ObservableField<>();
    public ObservableField<String> phoneNum = new ObservableField<>();
    public MutableLiveData<String> headImgUrl = new MutableLiveData<>();
    public MutableLiveData<String> tip = new MutableLiveData<>();
    private CommonApiService service;
    private User user;

    public EditUserInfoViewModel(@NonNull Application application) {
        super(application);
        service = MyRetrofit.get().getCommonApiService();
        user = BaseApplication.getUser();
    }

    public void getUserInfo() {
        phoneNum.set(user.getAccount());
        userName.set(user.getName());
        headImgUrl.setValue(user.getHeadPic());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String str = dateFormat.format(new Date(Long.parseLong(user.getCreateTime())));
        registTime.set(str);
    }

    public void editUserName(String name) {
        service.editUserName(name)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(stringResult -> {
                    userName.set(name);
                    user.setName(name);
                    tip.postValue(stringResult.msg);
                },
                    throwable -> tip.postValue(throwable.getMessage()));
    }

    public void uploadHeadImg(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        final String[] imageUrl = {""};

        service.uploadImg(body)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                    imageUrl[0] = stringResult.data;
                })
                .flatMap((Function<Result<String>, ObservableSource<Result<String>>>) stringResult ->
                        service.uploadHead(stringResult.data))
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(stringResult -> {
                    tip.postValue(getApplication().getString(R.string.uploadHeadImgSuccess));
                    user.setHeadPic(imageUrl[0]);
                    },
                    throwable -> tip.postValue(throwable.getMessage()));
    }
}
