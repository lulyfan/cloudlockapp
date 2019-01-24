package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.ut.base.BaseApplication;
import com.ut.database.entity.User;
import com.ut.module_mine.GlobalData;
import com.ut.module_mine.R;

public class ReceiverSettingViewModel extends BaseViewModel {

    public ObservableField<String> receiverPhone = new ObservableField<>();
    public MutableLiveData<Boolean> isInputPhone = new MutableLiveData<>();
    public MutableLiveData<User> user = new MutableLiveData<>();

    public ReceiverSettingViewModel(@NonNull Application application) {
        super(application);

        receiverPhone.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (receiverPhone.get().length() == 11) {
                    isInputPhone.setValue(true);
                    GlobalData.getInstance().receiverPhone = receiverPhone.get();
                } else {
                    isInputPhone.setValue(false);
                }
            }
        });
    }

    public void getUserInfoByMobile() {
        String receiverPhone = GlobalData.getInstance().receiverPhone;
        if (receiverPhone.equals(BaseApplication.getUser().getAccount())) {
            tip.postValue(getApplication().getString(R.string.cannotTransformToSelf));
            return;
        }

        service.getUserInfoByMobile(receiverPhone)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(userResult -> {
                    user.postValue(userResult.data);
                }, throwable -> tip.postValue(throwable.getMessage()));
    }
}
