package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityReceiverSettingBinding;
import com.ut.module_mine.viewModel.ReceiverSettingViewModel;

public class ReceiverSettingActivity extends BaseActivity {

    private ActivityReceiverSettingBinding binding;
    private ReceiverSettingViewModel viewModel;

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

        binding.setViewmodel(viewModel);
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.receiverSetting));

        binding.nextStep.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiverSettingActivity.this, ConfirmChangePermissionActivity.class);
            startActivity(intent);
        });

        binding.addressBook.setOnClickListener(v -> selectContact());
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
                String number = cursor.getString(numberIndex).replace(" ","");
                binding.etReceiverPhone.setText(number);
            }
        }
    }
}
