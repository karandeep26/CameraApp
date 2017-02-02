package com.example.stpl.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.stpl.cameraapp.main.MainPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.subjects.PublishSubject;

import static android.content.ContentValues.TAG;

/**
 * Created by stpl on 11/21/2016.
 */

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
    static boolean surfaceCreated = false;
    String fileName;
    Context activity;
    MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    private int camId;
    PublishSubject<String> subject = PublishSubject.create();
    MainPresenter mainPresenter;

    public Observable<String> getSubject() {
        return subject;
    }


    public Preview(Context context, MainPresenter mainPresenter) {
        super(context);
        activity = context;
        this.mainPresenter = mainPresenter;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        camera = openFrontFacingCameraGingerbread();
        mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null)
            camera = openFrontFacingCameraGingerbread();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            setCamera();
//            for (Camera.Size size : mSupportedPreviewSizes) {
//                Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();


    }

    public void releaseCamera() {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    camId = camIdx;
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    private void initRecorder() {
        camera.unlock();
        mediaRecorder = new MediaRecorder();

        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        cpHigh.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP;
        cpHigh.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        mediaRecorder.setProfile(cpHigh);
        mediaRecorder.setOrientationHint(270);
        File mediaStorageDir = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        fileName = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setMaxDuration(50000); // 50 seconds
        mediaRecorder.setMaxFileSize(5000000); // Approximately 5 megabytes

    }

    public void takePicture() {
        camera.takePicture(null, null, (data, camera1) -> {
            mainPresenter.savePhotoSdCard(data);
                camera1.stopPreview();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        camera1.startPreview();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000);
        });
    }

    public void recordVideo() {
        initRecorder();
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Log.d("width", "" + width);
        Log.d("height", height + "");
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

    }

    public int getCorrectCameraOrientation(Camera.CameraInfo info) {

        int rotation = ((Activity) activity).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.d("result", "" + result);
        return result;
    }

    public void setCamera() {
        if (camera == null) {
            camera = openFrontFacingCameraGingerbread();
            try {
                camera.setPreviewDisplay(surfaceHolder);
                for (Camera.Size size : mSupportedPreviewSizes) {
                    Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (camera != null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, cameraInfo);
            camera.setDisplayOrientation(getCorrectCameraOrientation(cameraInfo));
            surfaceCreated = true;
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
            camera.setParameters(parameters);
            camera.startPreview();

        }

    }


    public void stopVideo() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mainPresenter.getCurrentSavedVideo(fileName);
    }

    public Camera getCamera() {
        return camera;
    }
}

