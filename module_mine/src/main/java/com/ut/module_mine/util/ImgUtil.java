package com.ut.module_mine.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.ut.base.Utils.Util;

import java.io.File;
import java.io.IOException;

public class ImgUtil {

    public static Uri takePhoto(Activity activity, String authority, File photoFile, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, authority, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivityForResult(takePictureIntent, requestCode);
                return photoURI;
            }
        }
        return null;
    }

     public static File createImageFile(Context context, String imageFileName) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static void choosePhoto(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void cropImageUri(Activity activity, Uri uri, int outputX, int outputY, int requestCode){

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void setPic(ImageView imageView, String imgPath) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        Bitmap circleBitmap = Util.toRoundBitmap(bitmap);
        imageView.setImageBitmap(circleBitmap);
//        imageView.setImageBitmap(bitmap);
    }

}
