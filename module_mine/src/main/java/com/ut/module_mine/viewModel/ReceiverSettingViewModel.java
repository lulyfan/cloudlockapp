package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class ReceiverSettingViewModel extends AndroidViewModel {

    public ObservableField<String> receiverPhone = new ObservableField<>();
    public MutableLiveData<Boolean> isInputPhone = new MutableLiveData<>();

    public ReceiverSettingViewModel(@NonNull Application application) {
        super(application);

        receiverPhone.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (receiverPhone.get().length() == 11) {
                    isInputPhone.setValue(true);
                } else {
                    isInputPhone.setValue(false);
                }
            }
        });
    }
}