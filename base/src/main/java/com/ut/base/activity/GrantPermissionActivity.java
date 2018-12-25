package com.ut.base.activity;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.base.adapter.GrantPermissionAdapter;
import com.ut.base.databinding.ActivityGrantPermissionBinding;

@Route(path = RouterUtil.LockModulePath.SEND_KEY)
public class GrantPermissionActivity extends BaseActivity {
    private ActivityGrantPermissionBinding binding;
    public MutableLiveData<String> receiverPhoneNum = new MutableLiveData<>();
    public MutableLiveData<String> receiverName = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grant_permission);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.setTabWidth(binding.tabLayout);
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
                String number = cursor.getString(numberIndex).replace(" ","");
                String name = cursor.getString(nameIndex);
                receiverPhoneNum.setValue(number);
                receiverName.setValue(name);
            }
        }
    }
}
