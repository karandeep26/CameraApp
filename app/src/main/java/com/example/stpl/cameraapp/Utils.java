package com.example.stpl.cameraapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.stpl.cameraapp.models.MediaDetails;

import java.io.File;
import java.util.ArrayList;


public class Utils {
    //        static public File mediaStorageDir = new File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//            "MyCameraApp");
    static public File mediaStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "Camera");
    static public int height;
    public static int width;
    public static int ROTATION_O = 1;
    public static int ROTATION_90 = 2;
    public static int ROTATION_180 = 3;
    public static int ROTATION_270 = 4;
    public static String JPG = "jpg";
    public static String MP4 = "mp4";
    public static String IMAGE = "image";
    public static String VIDEO = "video";
    public static String ALL = "all";
    public static final int MULTIPLE_PERMISSIONS = 2;
    public static final int RC_SIGN_IN = 1;
    public static final int DELETE_FILES = 3;


    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    static Bitmap decodeSampledBitmapFromFile(String fileName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]
                {MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new
                String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();

            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails
                    .MINI_KIND, null);
        }
        if (ca != null) {
            ca.close();
        }
        return null;

    }

    public static String getPath(ContentResolver contentResolver, ArrayList<MediaDetails>
            selectedImageUris) {
        long time1 = System.currentTimeMillis();

        Cursor ca = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]
                {MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new
                String[]{""}, null);
        int id = -1;
        if (ca != null && ca.moveToFirst()) {
            id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
        }
        if (id != -1) {
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//**EDIT**
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails
                        .DATA));
                cursor.close();
                long time2 = System.currentTimeMillis() - time1;
                Log.d("time2", "" + time2);
            }
        }


        return null;
    }


}
