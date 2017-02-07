package com.example.stpl.cameraapp.models;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;


public class SdCardInteractorImpl implements SdCardInteractor, SdCardInteractor.GetMediaList,
        SdCardInteractor.Selection {
    private ArrayList<MediaDetails> images, videos;
    private ArrayList<MediaDetails> selected;

    public SdCardInteractorImpl() {
        this.images = new ArrayList<>();
        this.videos = new ArrayList<>();
        selected = new ArrayList<>();
    }

    @Override
    public Observable<List<MediaDetails>> getFromSdCard(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] listFile;

        if (type.equals("video")) {
            listFile = file.listFiles(pathname -> {
                return pathname.getAbsolutePath().contains("mp4");
            });
        } else if (type.equalsIgnoreCase("image")) {
            listFile = file.listFiles(pathname -> {
                return pathname.getAbsolutePath().contains("jpg");
            });
        } else {
            listFile = file.listFiles();
        }

        Arrays.sort(listFile, (o1, o2) -> {
            if(o1.lastModified()>o2.lastModified()) {
                return -1;
            }
            else if(o1.lastModified()<o2.lastModified()) {
                return 1;
            }
            return 0;
        });
        Observable<File> fileObservable = Observable.from(listFile);
        return fileObservable.flatMap(file1 -> Observable.just(file1)
                .subscribeOn(Schedulers.io())
                .map(this::getMediaDetails)).toList();

    }

    @Override
    public boolean deleteFromSdCard(MediaDetails mediaDetails) {
        File deleteFile = new File(mediaDetails.getFilePath());
        return deleteFile.delete();

    }


    @Override
    public int getMediaCount(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] imageFile = file.listFiles(pathname -> {
            return pathname.getAbsolutePath().contains(type);
        });
        return imageFile.length;
    }


    private MediaDetails getMediaDetails(File file) {
        MediaDetails mediaDetails;
        String path = file.getAbsolutePath();

        if (path.contains("jpg")) {
            mediaDetails = new MediaDetails(path, "image");
            images.add(mediaDetails);
        } else {
            mediaDetails = new MediaDetails(path, "video");
            videos.add(mediaDetails);
        }
        return mediaDetails;
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String fileName = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";

        File mediaFile;
        mediaFile = new File(fileName);
        return mediaFile;
    }

    @Override
    public MediaDetails savePhoto(byte[] data) {
        File pictureFile = getOutputMediaFile();
        MediaDetails mediaDetails;
        if (pictureFile == null) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
//            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Matrix matrix = new Matrix();
//            matrix.postRotate(270);
//            Bitmap rotatedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
//                    image.getHeight(), matrix, true);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            fos.write(data);
            fos.close();
            images.add(0, getMediaDetails(pictureFile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return images.get(0);
    }

    @Override
    public MediaDetails getSavedVideo(String fileName) {
        return getMediaDetails(new File(fileName));
    }

    @Override
    public ArrayList<MediaDetails> getMedia(String type) {
        if (type.equals("image")) {
            return images;
        } else if (type.equals("video")) {
            return videos;
        }
        return null;
    }

    @Override
    public void remove(MediaDetails mediaDetails) {
        selected.remove(mediaDetails);
    }

    @Override
    public void add(MediaDetails mediaDetails) {
        selected.add(mediaDetails);
    }

    @Override
    public boolean isSelectionMode() {
        return selected.size() > 0;
    }

    @Override
    public void clearAll() {
        selected.clear();

    }
}

