package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ut.module_lock.entity.KeyItem;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */
public class KeyManagerVM extends AndroidViewModel {

    public KeyManagerVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<KeyItem>> keys = null;

    public MutableLiveData<List<KeyItem>> getKeys(){
        if(keys == null) {
            keys = new MutableLiveData<>();
        }
        return keys;
    }
}
