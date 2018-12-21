package com.ut.module_lock.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.operation.CommonApi;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.adapter.ListAdapter;
import com.ut.commoncomponent.CLToast;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.BR;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :选择钥匙分组界面
 */

@Route(path = RouterUtil.LockModulePath.CHOOSE_LOCK_GROUP)
public class ChooseLockGroupActivity extends BaseActivity {
    private List<LockGroup> lockGroups = new ArrayList<>();
    private LockKey lockKey;
    private ListAdapter<LockGroup> adapter;
    private long currentGroupId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_choose_group);
        lockKey = getIntent().getParcelableExtra("lock_key");
        currentGroupId = lockKey.getGroupId();
        initDarkToolbar();
        setTitle(getString(R.string.choose_group));
        initAdd(() -> createGroup());

        ListView listView = findViewById(R.id.group_list);
        adapter = new ListAdapter<LockGroup>(this, R.layout.item_lock_group, lockGroups, BR.lockGroup) {
            @Override
            public void addBadge(ViewDataBinding binding, int position) {
                super.addBadge(binding, position);
                long lockKeyId = lockGroups.get(position).getId();
                CheckBox checkBox = binding.getRoot().findViewById(R.id.check_box);
                if (lockGroups.get(position).getName().equals(newGroupName)) {
                    currentGroupId = lockKeyId;
                }
                checkBox.setChecked(currentGroupId == lockKeyId);
                checkBox.setOnClickListener(v -> {
                    if (checkBox.isChecked()) {
                        if (lockKeyId == currentGroupId) {
                            currentGroupId = -1;
                        } else {
                            currentGroupId = lockKeyId;
                        }
                    } else {
                        currentGroupId = lockKeyId;
                    }
                    notifyDataSetChanged();
                });
            }
        };
        listView.setAdapter(adapter);
        loadGroup();
    }

    private String newGroupName = null;

    private void createGroup() {
        View contentView = View.inflate(this, R.layout.dialog_edit, null);
        EditText edt = contentView.findViewById(R.id.edt);
        edt.setHint(R.string.lock_create_group_hint);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(contentView)
                .setTitle(R.string.lock_create_group)
                .setPositiveButton(getText(R.string.lock_btn_confirm), (dialog, which) -> {
                    dialog.dismiss();
                    MyRetrofit.get().getCommonApiService().addGroup(edt.getText().toString().trim())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                CLToast.showAtCenter(getBaseContext(), result.msg);
                                if (result.isSuccess()) {
                                    newGroupName = edt.getText().toString().trim();
                                    loadGroup();
                                }
                            }, new ErrorHandler());
                })
                .setNegativeButton(getText(R.string.lock_cancel), null)
                .create();
        alertDialog.show();
    }

    private void loadGroup() {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().getGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        adapter.updateDate(result.data);
                    }
                }, new ErrorHandler());
    }

    @Override
    public void finish() {
        if (currentGroupId != lockKey.getGroupId()) {
            final long tmp = currentGroupId;
            currentGroupId = lockKey.getGroupId();
            Disposable subscribe = MyRetrofit.get().getCommonApiService().addLockIntoGroup(lockKey.getMac(), tmp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        CLToast.showAtCenter(getApplication(), result.msg);
                        if (result.isSuccess()) {
                            Intent intent = new Intent();
                            intent.putExtra("lock_group_id", tmp);
                            setResult(RESULT_OK, intent);
                        }
                        super.finish();
                    }, error -> super.finish());
        } else {
            super.finish();
        }
    }
}
