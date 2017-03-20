package com.example.stpl.cameraapp.models;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by stpl on 3/20/2017.
 */

public class MyContentProvider extends ContentProvider {
    Context context;

    @Override
    public boolean onCreate() {
        context = getContext();
        if (context == null) {
            Log.d("null", "context");
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String
            selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor ca = context.getContentResolver().query(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI, new String[]
                {MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?",
                selectionArgs, null);
        int id = -1;
        if (ca != null && ca.moveToFirst()) {
            id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
        }
        if (id != -1) {
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    context.getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return cursor;
//                cursor.moveToFirst();//**EDIT**
//                String path= cursor.getString(cursor.getColumnIndex(MediaStore.Images
// .Thumbnails.DATA));
//                cursor.close();
//                return path;

            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
    }
}
