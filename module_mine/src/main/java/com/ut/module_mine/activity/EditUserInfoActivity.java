package com.ut.module_mine.activity;

import android.Manifest;
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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.base.Utils.Util;
import com.ut.module_mine.databinding.ActivityEditUserInfoBinding;
import com.ut.module_mine.util.ImgUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditUserInfoActivity extends BaseActivity {

    private ActivityEditUserInfoBinding binding;
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
        requestWritePermission();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.editUserInfo));

        binding.headContainer.setOnClickListener(v -> editImg());

        binding.headImg.getViewTreeObserver().addOnGlobalLayoutListener(() -> setHeadImg());
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
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File photoFile = null;
                        try {
                            photoFile = ImgUtil.createImageFile(EditUserInfoActivity.this, imageFileName);
                            mCurrentPhotoPath = photoFile.getAbsolutePath();
                        } catch (IOException ex) {
                            Toast.makeText(EditUserInfoActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

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
            ImgUtil.setPic(binding.headImg, mCurrentPhotoPath);
            saveHeadImgPath();

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

            ImgUtil.setPic(binding.headImg, mCurrentPhotoPath);
            saveHeadImgPath();
        }
    }

    private void saveHeadImgPath() {
        SharedPreferences sharedPreferences = getSharedPreferences("cloudLock", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("headImg", mCurrentPhotoPath);
        editor.apply();
    }

    private void setHeadImg() {
        SharedPreferences sharedPreferences = getSharedPreferences("cloudLock", Context.MODE_PRIVATE);
        String headImgPath = sharedPreferences.getString("headImg", "");
        File file = new File(headImgPath);
        if (file.exists()) {
            ImgUtil.setPic(binding.headImg, headImgPath);
        }
    }
}
