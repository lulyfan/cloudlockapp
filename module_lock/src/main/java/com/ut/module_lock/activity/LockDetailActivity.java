package com.ut.module_lock.activity;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.base.BaseActivity;
import com.ut.base.Utils.UTLog;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityLockDetailBindingImpl;
import com.ut.module_lock.entity.LockKey;

public class LockDetailActivity extends BaseActivity {
    public static final String EXTRA_LOCK_KEY = "extra_lock_key";
    private LockKey mLockKey = null;

    ActivityLockDetailBindingImpl mDetailBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLockKey = getIntent().getParcelableExtra(EXTRA_LOCK_KEY);
        mDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_lock_detail);
        mDetailBinding.setLockKey(mLockKey);
    }

    @BindingAdapter("bind:electricityDrawable")
    public static void loadDrawable(TextView textView, int electricity) {
        UTLog.i("electricity:"+electricity);
        Drawable leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_green);
        if (electricity < 20){
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_red);
        }else if (electricity < 40){
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_orange);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        textView.setCompoundDrawables(leftDrawable,textView.getCompoundDrawables()[1],
                textView.getCompoundDrawables()[2],textView.getCompoundDrawables()[3]);
    }

    @BindingAdapter("bind:bgSrc")
    public static void loadbubble(TextView textView, int userType) {
        int srcId = R.mipmap.icon_bubble_orange;
        if (userType == 1){
            srcId = R.mipmap.icon_bubble_purple;
        }else if(userType == 2){
            srcId = R.mipmap.icon_bubble_gray;
        }
        textView.setBackgroundResource(srcId);
    }

    @BindingAdapter("bind:userType")
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
