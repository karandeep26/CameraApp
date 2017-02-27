package com.example.stpl.cameraapp.main;

import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by stpl on 2/27/2017.
 */

public class FirebaseMainImpl implements FirebaseMainPresenter {
    StorageReference storageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://selfie-geek.appspot.com");

    @Override
    public void uploadToCloud(String fileName) {
        try {
            InputStream inputStream = new FileInputStream(new File(fileName));
            fileName = new File(fileName).getName();
            Log.d("new File name", fileName);
            StorageReference imageRef = storageRef.child(fileName);

            UploadTask uploadTask = imageRef.putStream(inputStream);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                    Log.d("URL", "**" + taskSnapshot.getDownloadUrl()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadFromCloud() {

    }
}
