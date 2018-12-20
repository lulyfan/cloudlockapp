package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.ut.module_mine.GlobalData;

public class ReceiverSettingViewModel extends BaseViewModel {

    public ObservableField<String> receiverPhone = new ObservableField<>();
    public MutableLiveData<Boolean> isInputPhone = new MutableLiveData<>();

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
}
