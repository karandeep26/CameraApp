package com.example.stpl.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.stpl.cameraapp.main.MainActivity;
import com.example.stpl.cameraapp.main.MainPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.content.ContentValues.TAG;
import static com.example.stpl.cameraapp.Utils.ROTATION_180;
import static com.example.stpl.cameraapp.Utils.ROTATION_270;
import static com.example.stpl.cameraapp.Utils.ROTATION_90;
import static com.example.stpl.cameraapp.Utils.ROTATION_O;


@SuppressWarnings("deprecation")
public class CustomCamera extends SurfaceView implements SurfaceHolder.Callback {
    static boolean surfaceCreated = false;
    String fileName;
    Context activity;
    MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> mSupportedPreviewSizes;
    public Camera.Size mPreviewSize;
    private int camId;
    PublishSubject<Boolean> takePictureSubject = PublishSubject.create();
    MainPresenter mainPresenter;
    int rotation;
    int correctOrientation;
    OrientationListener orientationListener;
    Camera.Parameters parameters;
    boolean isCameraSet = false;
    Configuration configuration;
    boolean shouldMeasure = true;
    PublishSubject<Integer> rotationSubject = PublishSubject.create();

    public Observable<Integer> _getRotation() {
        return rotationSubject;
    }


    public Observable<Boolean> _getTakePictureSubject() {
        return takePictureSubject;
    }


    public CustomCamera(Context context) {
        super(context);
    }


    public CustomCamera(Context context, MainPresenter mainPresenter) {
        this(context);
        activity = context;
        this.mainPresenter = mainPresenter;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rotation = activity.getResources().getConfiguration().orientation;
        camera = openFrontFacingCameraGingerbread();
        mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        configuration = context.getResources().getConfiguration();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            camera = openFrontFacingCameraGingerbread();
        }
        try {
            camera.setPreviewDisplay(holder);

            if (((MainActivity) activity).bottomSheetBehavior.getState() ==
                    BottomSheetBehavior.STATE_EXPANDED && ((MainActivity) activity)
                    .isRotationEnabled()) {
                if (orientationListener.getRotation() == Utils.ROTATION_O) {
                    setCamera();
                }
            } else {
                ((Activity) activity).setRequestedOrientation(ActivityInfo
                        .SCREEN_ORIENTATION_PORTRAIT);
                setCamera();
            }
            Log.d("surface created", "true");
//            for (Camera.Size size : mSupportedPreviewSizes) {
//                Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
            if (camera != null)
                camera.setPreviewDisplay(holder);

            if (((MainActivity) activity).bottomSheetBehavior.getState() ==
                    BottomSheetBehavior.STATE_EXPANDED && ((MainActivity) activity)
                    .isRotationEnabled()) {
                if (orientationListener.getRotation() == Utils.ROTATION_O) {
                    setCamera();
                }
            } else {
                ((Activity) activity).setRequestedOrientation(ActivityInfo
                        .SCREEN_ORIENTATION_PORTRAIT);
                setCamera();
            }
            Log.d("surface created", "true");
//            for (Camera.Size size : mSupportedPreviewSizes) {
//                Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();


    }

    public void releaseCamera() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewDisplay(null);
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
        File mediaStorageDir = Utils.mediaStorageDir;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        fileName = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setMaxDuration(50000); // 50 seconds
        mediaRecorder.setMaxFileSize(5000000); // Approximately 5 megabytes

    }

    public void takePicture() {
        camera.takePicture(null, null, (data, camera1) -> {
            String fileName = mainPresenter.savePhotoSdCard(data);
            if (fileName != null) {
                try {

                    ExifInterface exifInterface = new ExifInterface(fileName);
                    if (orientationListener.getRotation() == ROTATION_O) {
                        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_ROTATE_270 + "");
                        exifInterface.saveAttributes();
                    } else if (orientationListener.getRotation() == ROTATION_270) {
                        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_ROTATE_180 + "");
                    }
                    exifInterface.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            camera1.stopPreview();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    camera1.startPreview();
                    takePictureSubject.onNext(true);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 500);
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

        setMeasuredDimension(width, height);

        if (orientationListener == null) {
            orientationListener = new OrientationListener(activity);
            orientationListener.enable();
        }
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
        return result;
    }

    public void setCamera() {
        if (camera == null) {
            camera = openFrontFacingCameraGingerbread();
            try {
                camera.setPreviewDisplay(surfaceHolder);
//                for (Camera.Size size : mSupportedPreviewSizes) {
//                    Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
//                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (camera != null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, cameraInfo);
            surfaceCreated = true;
            parameters = camera.getParameters();
            correctOrientation = getCorrectCameraOrientation(cameraInfo);
            Log.d("mHeight", mPreviewSize.height + "");
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
            camera.setDisplayOrientation(correctOrientation);
            camera.setParameters(parameters);
            camera.startPreview();
            isCameraSet = true;
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

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configuration = newConfig;
    }

    private class OrientationListener extends OrientationEventListener {
        private int rotation = 0;

        OrientationListener(Context context) {
            super(context);
        }


        @Override
        public void onOrientationChanged(int orientation) {

            if ((orientation < 35 || orientation > 325) && rotation != ROTATION_O) { // PORTRAIT
                rotation = ROTATION_O;
                rotationSubject.onNext(rotation);

            } else if (orientation > 145 && orientation < 215 && rotation != ROTATION_180) { //
                // REVERSE PORTRAIT
                rotation = ROTATION_180;
                rotationSubject.onNext(rotation);

            } else if (orientation > 55 && orientation < 125 && rotation != ROTATION_270) { //
                // REVERSE LANDSCAPE
                rotation = ROTATION_270;
                rotationSubject.onNext(rotation);


            } else if (orientation > 235 && orientation < 305 && rotation != ROTATION_90) {
                //LANDSCAPE
                rotation = ROTATION_90;
                rotationSubject.onNext(rotation);

            }

        }

        int getRotation() {
            return rotation;
        }
    }

}

