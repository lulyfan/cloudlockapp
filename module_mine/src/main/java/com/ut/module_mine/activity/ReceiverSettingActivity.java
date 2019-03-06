package com.ut.module_mine.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.ut.base.BaseActivity;
import com.ut.database.entity.User;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityReceiverSettingBinding;
import com.ut.module_mine.viewModel.ReceiverSettingViewModel;

public class ReceiverSettingActivity extends BaseActivity {

    private ActivityReceiverSettingBinding binding;
    private ReceiverSettingViewModel viewModel;
    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_IMAGE_URL = "userImageUrl";
    private static final int REQUEST_READ_PERMISSION_CODE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receiver_setting);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ReceiverSettingViewModel.class);
        viewModel.isInputPhone.observe(this, b -> binding.nextStep.setEnabled(b));
        viewModel.tip.observe(this, s -> toastShort(s));
        viewModel.user.observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(ReceiverSettingActivity.this, ConfirmChangePermissionActivity.class);
                intent.putExtra(EXTRA_USER_NAME, user.getName());
                intent.putExtra(EXTRA_USER_IMAGE_URL, user.getHeadPic());
                startActivity(intent);
            }
        });

        binding.setViewmodel(viewModel);
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.receiverSetting));

        binding.nextStep.setOnClickListener(v -> {
            viewModel.getUserInfoByMobile();
        });

        binding.addressBook.setOnClickListener(v -> selectContact());
    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        if (checkAndRequestPermission(Manifest.permission.READ_CONTACTS, REQUEST_READ_PERMISSION_CODE)) {
            // Start an activity for the user to pick a phone number from contacts
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
            }
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
                String number = cursor.getString(numberIndex).replace(" ", "");
                binding.etReceiverPhone.setText(number);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
                    }
                } else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.mine_app_need_contact_permission)
                            .setPositiveButton(R.string.mine_setting, (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            })
                            .setNegativeButton(R.string.mine_cancel, null)
                            .create()
                            .show();

                }
                break;
            default:
                break;
        }
    }

}
