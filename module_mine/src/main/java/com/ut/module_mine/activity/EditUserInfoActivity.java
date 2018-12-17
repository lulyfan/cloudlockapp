package com.ut.module_mine.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.module_mine.Constant;
import com.ut.module_mine.R;
import com.ut.base.Utils.Util;
import com.ut.module_mine.databinding.ActivityEditUserInfoBinding;
import com.ut.module_mine.util.ImgUtil;
import com.ut.module_mine.viewModel.EditUserInfoViewModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditUserInfoActivity extends BaseActivity {

    private ActivityEditUserInfoBinding binding;
    private EditUserInfoViewModel viewModel;
    private String mCurrentPhotoPath;
    private Uri photoURI;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int REQUEST_CHOOSE_PHOTO = 3;
    private static final int REQUEST_WRITE_PERMISSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user_info);
        initUI();
        initViewModel();
        requestWritePermission();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.editUserInfo));

        binding.headContainer.setOnClickListener(v -> editImg());
        binding.userName.setOnClickListener(v -> editUserName());
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(EditUserInfoViewModel.class);
        binding.setViewModel(viewModel);

        viewModel.headImgUrl.observe(this, s -> {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.headimg);
            options.circleCrop();
            Glide.with(EditUserInfoActivity.this).load(s).apply(options).into(binding.headImg);
        });


        viewModel.tip.observe(this, s -> {
            toastShort(s);

            if (getString(R.string.uploadHeadImgSuccess).equals(s)) {
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(EditUserInfoActivity.this).load(mCurrentPhotoPath).apply(options).into(binding.headImg);
            }
        });

        viewModel.getUserInfo();
    }

    private void editImg() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_headimg, null);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();

                    if (i == R.id.takePhoto) {
                        File photoFile = createImageFile();
                        if (photoFile == null) {
                            return;
                        }

                        mCurrentPhotoPath = photoFile.getAbsolutePath();
                        photoURI = ImgUtil.takePhoto(EditUserInfoActivity.this, "com.ut.module_mine.fileprovider",
                                photoFile, REQUEST_IMAGE_CAPTURE);
                        dialog1.dismiss();

                    } else if (i == R.id.chooseLocalImg) {
                        ImgUtil.choosePhoto(EditUserInfoActivity.this, REQUEST_CHOOSE_PHOTO);
                        dialog1.dismiss();
                    } else {
                    }
                })
                .create();

        dialog.show();
    }

    private boolean checkWritePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        if (!checkWritePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImgUtil.cropImageUri(EditUserInfoActivity.this, photoURI, 800, 800, CROP_PICTURE);

        } else if (requestCode == CROP_PICTURE && resultCode == RESULT_OK) {
            viewModel.uploadHeadImg(mCurrentPhotoPath);

        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
            if (!checkWritePermission()) {
                Toast.makeText(this, getString(R.string.needWritePermission), Toast.LENGTH_SHORT).show();
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap image = BitmapFactory.decodeFile(mCurrentPhotoPath);
            File imageFile = createImageFile();
            if (imageFile == null) {
                return;
            }

            ImgUtil.saveBitmap(image, imageFile);
            Uri uri = FileProvider.getUriForFile(this, "com.ut.module_mine.fileprovider", imageFile);
            ImgUtil.cropImageUri(EditUserInfoActivity.this, uri, 800, 800, CROP_PICTURE);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageFile = null;
        try {
            imageFile = ImgUtil.createImageFile(this, imageFileName, ".jpg");
            return imageFile;
        } catch (IOException e) {
            toastShort(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void editUserName() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user_name, null);
        EditText et_userName = view.findViewById(R.id.et_userName);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {
                        String userName = et_userName.getText().toString();
                        viewModel.editUserName(userName);
                        dialog1.dismiss();

                    } else {
                    }
                })
                .create();

        dialog.show();
    }

//    private void saveHeadImgPath() {
//        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(Constant.KEY_HEAD_IMG_LOCAL, mCurrentPhotoPath);
//        editor.apply();
//    }

//    private void setHeadImg() {
//        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//        String headImgPath = sharedPreferences.getString(Constant.KEY_HEAD_IMG_LOCAL, "");
//        RequestOptions options = new RequestOptions();
//        options.circleCrop();
//        Glide.with(this).load(headImgPath).apply(options).into(binding.headImg);
//    }
}
