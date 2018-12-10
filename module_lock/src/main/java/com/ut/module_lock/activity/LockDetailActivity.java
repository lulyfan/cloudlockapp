package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.base.BaseActivity;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityLockDetailBindingImpl;
import com.ut.module_lock.entity.LockKey;
import com.ut.module_lock.entity.OperationRecord;

public class LockDetailActivity extends BaseActivity {
    public static final String EXTRA_LOCK_KEY = "extra_lock_key";
    private LockKey mLockKey = null;

    ActivityLockDetailBindingImpl mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        mLockKey = getIntent().getParcelableExtra(EXTRA_LOCK_KEY);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_detail);
        addPaddingTop();
        mDetailBinding.setLockKey(mLockKey);
        mDetailBinding.setPresent(new Present());
    }

    private void addPaddingTop() {
        View view = findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(this), view.getPaddingRight(), view.getPaddingBottom());
    }

    public class Present {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onSendKeyClick(View view) {
            startActivity(new Intent(LockDetailActivity.this, GrantPermissionActivity.class));
        }

        public void onMangeKeyClick(View view) {
            startActivity(new Intent(LockDetailActivity.this, KeysManagerActivity.class));
        }

        public void onOperateRecordClick(View view) {
            startActivity(new Intent(LockDetailActivity.this, OperationRecordAcitivity.class));
        }

        public void onLockManageClick(View view) {
            startActivity(new Intent(LockDetailActivity.this, LockSettingActivity.class));
        }
    }

    @BindingAdapter("electricityDrawable")
    public static void loadDrawable(TextView textView, int electricity) {
        UTLog.i("electricity:" + electricity);
        Drawable leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_green);
        if (electricity < 20) {
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_red);
        } else if (electricity < 40) {
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_orange);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        textView.setCompoundDrawables(leftDrawable, textView.getCompoundDrawables()[1],
                textView.getCompoundDrawables()[2], textView.getCompoundDrawables()[3]);
    }

    @BindingAdapter("bgSrc")
    public static void loadbubble(TextView textView, int userType) {
        int srcId = R.mipmap.icon_bubble_orange;
        if (userType == 1) {
            srcId = R.mipmap.icon_bubble_purple;
        } else if (userType == 2) {
            srcId = R.mipmap.icon_bubble_gray;
        }
        textView.setBackgroundResource(srcId);
    }

    @BindingAdapter("userType")
    public static void loadUserImage(ImageView imageView, int userType) {
        if (userType == 0) {
            imageView.setImageResource(R.mipmap.icon_user_manager_detail);
        } else if (userType == 1) {
            imageView.setImageResource(R.mipmap.icon_user_auth_detail);
        } else {
            imageView.setImageResource(R.mipmap.icon_user_normal_detail);
        }
    }
}
