package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ut.base.BaseActivity;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonViewHolder;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityNearLockBinding;
import com.ut.module_lock.entity.BleDevice;

import java.util.ArrayList;
import java.util.List;

public class NearLockActivity extends BaseActivity {
    private ActivityNearLockBinding mNearLockBinding;
    private CommonAdapter<BleDevice> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_near_lock);
        enableImmersive();
        mNearLockBinding = DataBindingUtil.setContentView(this, R.layout.activity_near_lock);
        setLightStatusBar();
        setTitle(R.string.lock_title_near_lock);
        initTestData();
        mNearLockBinding.lvBleDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NearLockActivity.this, ApplyKeyActivity.class);
                NearLockActivity.this.startActivity(intent);
            }
        });
    }

    private void initTestData() {
        List<BleDevice> list = new ArrayList<>();
        list.add(new BleDevice("M301_4494bc", false));
        list.add(new BleDevice("M301_1688ab", true));
        refreshListData(list);
    }

    private void refreshListData(List<BleDevice> datas) {
        if (mAdapter == null) {
            mAdapter = new CommonAdapter<BleDevice>(this, datas, R.layout.item_ble_list) {
                @Override
                public void convert(CommonViewHolder commonViewHolder, int position, BleDevice item) {
                    TextView textView = commonViewHolder.getView(R.id.tv_ble_name);
                    textView.setText(item.getName());
                    ImageButton imageButton = commonViewHolder.getView(R.id.iBtn_ble_add);
                    imageButton.setVisibility(item.isActive() ? View.GONE : View.VISIBLE);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NearLockActivity.this, SetNameActivity.class);
                            NearLockActivity.this.startActivity(intent);
                        }
                    });
                }
            };
            mNearLockBinding.lvBleDevice.setAdapter(mAdapter);
        } else {
            mAdapter.notifyData(datas);
        }
    }


}
