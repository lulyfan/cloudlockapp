package com.ut.base.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.base.adapter.GrantPermissionAdapter;
import com.ut.base.databinding.ActivityGrantPermissionBinding;
import com.ut.base.viewModel.SendKeyViewModel;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Route(path = RouterUtil.BaseModulePath.GRANTPERMISSION)
public class SendKeyActivity extends BaseActivity {
    private ActivityGrantPermissionBinding binding;
    private SendKeyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grant_permission);
        initUI();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.setTabWidth(binding.tabLayout);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SendKeyViewModel.class);
        viewModel.tip.observe(this, s -> {
            CLToast.showAtBottom(getApplicationContext(), s);
            finish();
        });
        viewModel.mac = getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MAC);

        String mobile = getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MOBILE);
        viewModel.receiverPhoneNum.setValue(mobile);

        viewModel.userType = getIntent().getIntExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY_USERTYPE,
                EnumCollection.UserType.ADMIN.ordinal());

        viewModel.receiverPhoneNum.observe(this, inputObserver);
        viewModel.keyName.observe(this, inputObserver);
        viewModel.limitStartTime.observe(this, inputObserver);
        viewModel.limitEndTime.observe(this, inputObserver);
        viewModel.loopStartTime.observe(this, inputObserver);
        viewModel.loopEndTime.observe(this, inputObserver);
        viewModel.startTimeRange.observe(this, inputObserver);
        viewModel.endTimeRange.observe(this, inputObserver);
        viewModel.weeks.observe(this, inputObserver);
    }

    private void initUI() {
        setSupportActionBar(binding.toolbar5);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(null);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_white);

        binding.headContainer.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        binding.viewPager.setAdapter(new GrantPermissionAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        int rulerType = getIntent().getIntExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_RULER_TYPE, 1);
        binding.viewPager.setCurrentItem(rulerType - 1);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                viewModel.setPhoneAndName();
                checkInputInfo();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        binding.sendKey.setOnClickListener(v -> {

            String phoneNum = viewModel.receiverPhoneNum.getValue();
            String keyName = viewModel.keyName.getValue();
            if (TextUtils.isEmpty(keyName)) {
                keyName = phoneNum;
            }
            boolean isAdmin = viewModel.isAdmin;
            String limitStartTime = viewModel.limitStartTime.getValue();
            String limitEndTime = viewModel.limitEndTime.getValue();
            String loopStartTime = viewModel.loopStartTime.getValue();
            String loopEndTime = viewModel.loopEndTime.getValue();
            String startTimeRange = viewModel.startTimeRange.getValue();
            String endTimeRange = viewModel.endTimeRange.getValue();
            String weeks = viewModel.weeks.getValue();
            String mac = viewModel.mac;

            String reg = "yyyy-MM-dd HH:ss:mm";

            SimpleDateFormat sdf = new SimpleDateFormat(reg, Locale.getDefault());
            try {
                switch (binding.viewPager.getCurrentItem()) {

                    case 0:
                        viewModel.sendForeverKey(phoneNum, mac, keyName, isAdmin);
                        break;

                    case 1:
                        long limitedStartTimeStamp = sdf.parse(limitStartTime).getTime();
                        long limitedEndTimeStamp = sdf.parse(limitEndTime).getTime();
                        if (limitedEndTimeStamp <= limitedStartTimeStamp) {
                            CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_send_key_time_error_tips));
                            return;
                        }
                        viewModel.sendLimitTimeKey(phoneNum, mac, keyName, limitStartTime, limitEndTime);
                        break;
                    case 2:
                        viewModel.sendOnceKey(phoneNum, mac, keyName);
                        break;

                    case 3:
                        reg = "HH:ss:mm";
                        sdf = new SimpleDateFormat(reg, Locale.getDefault());
                        if (sdf.parse(startTimeRange).getTime() >= sdf.parse(endTimeRange).getTime()) {
                            CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_send_key_time_error_tips));
                            return;
                        }

                        reg = "yyyy-MM-dd";
                        sdf = new SimpleDateFormat(reg, Locale.getDefault());
                        if (sdf.parse(loopStartTime).getTime() >= sdf.parse(loopEndTime).getTime()) {
                            CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_send_key_date_error_tips));
                            return;
                        }

                        viewModel.sendLoopKey(phoneNum, mac, keyName, loopStartTime, loopEndTime, weeks, startTimeRange, endTimeRange);
                        break;

                    default:
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Observer inputObserver = o -> {
        checkInputInfo();
    };

    private void checkInputInfo() {
        String receiverPhoneNum = viewModel.receiverPhoneNum.getValue();
        String limitStartTime = viewModel.limitStartTime.getValue();
        String limitEndTime = viewModel.limitEndTime.getValue();
        String loopStartTime = viewModel.loopStartTime.getValue();
        String loopEndTime = viewModel.loopEndTime.getValue();
        String startTimeRange = viewModel.startTimeRange.getValue();
        String endTimeRange = viewModel.endTimeRange.getValue();
        String weeks = viewModel.weeks.getValue();

        if (!(receiverPhoneNum != null && !"".equals(receiverPhoneNum.trim()))) {
            binding.sendKey.setEnabled(false);
            return;
        }

        switch (binding.viewPager.getCurrentItem()) {
            case 0:
                break;

            case 1:
                if (!(limitStartTime != null && !"".equals(limitStartTime.trim()) &&
                        limitEndTime != null && !"".equals(limitEndTime.trim()))) {
                    binding.sendKey.setEnabled(false);
                    return;
                }
                break;

            case 2:
                break;

            case 3:
                if (!(startTimeRange != null && !"".equals(startTimeRange.trim()) &&
                        endTimeRange != null && !"".equals(endTimeRange.trim()) &&
                        loopStartTime != null && !"".equals(loopStartTime.trim()) &&
                        loopEndTime != null && !"".equals(loopEndTime.trim()) &&
                        weeks != null && !"".equals(weeks.trim()))) {
                    binding.sendKey.setEnabled(false);
                    return;
                }
                break;

            default:
        }

        binding.sendKey.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String number = cursor.getString(numberIndex).replace(" ", "");
                String name = cursor.getString(nameIndex);
                viewModel.receiverPhoneNum.setValue(number);
                viewModel.keyName.setValue(name);
            }
        }
    }
}
