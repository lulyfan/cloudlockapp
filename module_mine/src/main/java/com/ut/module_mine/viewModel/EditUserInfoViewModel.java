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

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditUserInfoViewModel extends AndroidViewModel {

    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> registTime = new ObservableField<>();
    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<String> headImgUrl = new ObservableField<>();
    public MutableLiveData<String> tip = new MutableLiveData<>();

    public EditUserInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserInfo() {
        User user = BaseApplication.getUser();
        phoneNum.set(user.getAccount());
        userName.set(user.getName());
        registTime.set(user.getCreateTime());
        headImgUrl.set(user.getHeadPic());
    }

    public void uploadHeadImg(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        CommonApiService service = MyRetrofit.get().getCommonApiService();

        service.uploadImg(body)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
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
                .subscribe(stringResult -> tip.postValue(getApplication().getString(R.string.uploadHeadImgSuccess)),
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
