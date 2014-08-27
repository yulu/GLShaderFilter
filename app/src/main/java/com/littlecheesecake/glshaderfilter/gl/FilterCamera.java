package com.littlecheesecake.glshaderfilter.gl;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by luyu on 18/8/14.
 */
public class FilterCamera implements FilterRenderer.SurfaceChangedListener, DisplayParameter{
    private static final int MAX_UNSPECIFIED = -1;

    //which camera
    public static final int FRONT = 1;
    public static final int BACK = 0;

    /**
     * Camera related
     */
    private Camera mCamera;
    private Camera.Parameters params;
    private SurfaceTexture mSurfaceTexture;

    //private float mAspectRatioPreview[] = new float[2];
    private int mFrameWidth;
    private int mFrameHeight;
    private int mMaxWidth;
    private int mMaxHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int currentZoomLevel = 1;
    private int maxZoomLevel = 0;

    /**
     * State related
     */
    private int which = BACK;

    /**
     * Constructors of the class---------------------------------------------
     */
    public FilterCamera() {
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
    }


    @Override
    public void OnSurfaceChanged(SurfaceTexture surfaceTexture, int width, int height) {
        try{
            //disableView();
            mCamera.stopPreview();
            setDimension(width, height);
            setPreviewTexture(surfaceTexture);
            initializeCamera(which);

        }catch(final Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Must called from Activity.onPause()
     */
    public void onPause(){
        mSurfaceTexture = null;
        releaseCamera();
    }

    /**
     * Should be called from Activity.onResuem()
     */
    public void onResume(SurfaceTexture s, int[] mSurfaceSize){
        setDimension(mSurfaceSize[0], mSurfaceSize[1]);
        try {
            setPreviewTexture(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(which == BACK)
            initializeCamera(BACK);
        else
            initializeCamera(FRONT);
    }

    private void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException {
        mSurfaceTexture = surfaceTexture;
        //mCamera.setPreviewTexture(surfaceTexture);
    }

    private void setDimension(int width, int height){
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    /**
     * state related
     */

    public void flipit() {
        synchronized(this) {
            if (Camera.getNumberOfCameras()>=2) {
                if(which == FRONT){
                    initializeCamera(BACK);
                    which = BACK;
                }else{
                    initializeCamera(FRONT);
                    which = FRONT;
                }
            }
        }
    }

    public void zoomin(float m){
        if(params.isZoomSupported()){
            maxZoomLevel = params.getMaxZoom();

            float zoom = (float)currentZoomLevel;
            zoom += (m/100f);
            currentZoomLevel = (int)zoom;
            if(currentZoomLevel > maxZoomLevel)
                currentZoomLevel = maxZoomLevel;
            if(currentZoomLevel < 1)
                currentZoomLevel = 1;

            params.setZoom(currentZoomLevel);
            mCamera.setParameters(params);
        }
    }


    private boolean initializeCamera( int facing){
        Log.d("Camera", "Initialize java camera");
        boolean result = true;
        synchronized(this){
            if(mCamera != null){
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);

                mCamera.release();
                mCamera = null;
            }
            /**
             * Open Camera--------------------------
             */
            Log.i("Camera", "Trying to open back camera");
            int localCameraIndex = -1;

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int localCameraFacingIdx;
            if(facing == FRONT)
                localCameraFacingIdx = Camera.CameraInfo.CAMERA_FACING_FRONT;
            else
                localCameraFacingIdx = Camera.CameraInfo.CAMERA_FACING_BACK;

            for(int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx){
                Camera.getCameraInfo(camIdx, cameraInfo);

                if(cameraInfo.facing == localCameraFacingIdx){
                    localCameraIndex = camIdx;
                    break;
                }
            }
            if(localCameraIndex != -1){
                try{
                    mCamera = Camera.open(localCameraIndex);
                    Log.d("Camera", "Camera #" + localCameraIndex + " open this camera");
                }catch(RuntimeException e){
                    Log.e("Camera", "Camera #" + localCameraIndex + " failed to open: " +
                            e.getLocalizedMessage());
                }
            }else{
                Log.e("Camera", "Back camera not found!");
            }

            if(mCamera == null)
                return false;

            /**
             * set camera parameters------------------------------
             */
            try{
                //size
                //Camera.Parameters params = mCamera.getParameters();
                params = mCamera.getParameters();
                Log.d("Camera", "getSupportedPreviewSizes()");
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();

                if(sizes != null){
                /* Select the size that fits surface considering maximum size allowed*/
                    Size frameSize = calculateCameraFrameSize(sizes, new CameraSizeAccessor(),
                            mSurfaceWidth, mSurfaceHeight);
                    params.setPreviewFormat(ImageFormat.NV21);
                    Log.d("Camera", "Set preview size to "+Integer.valueOf((int)frameSize.width) + "x" +
                            Integer.valueOf((int)frameSize.height));

                    if(frameSize.width != 0 && frameSize.height != 0){
                        params.setPreviewSize((int)frameSize.width, (int)frameSize.height);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                            params.setRecordingHint(true);
                        }

                        //focus
                        List<String> FocusModes = params.getSupportedFocusModes();
                        if(FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }

                        mCamera.setParameters(params);
                        params = mCamera.getParameters();

                        mFrameWidth = params.getPreviewSize().width;
                        mFrameHeight = params.getPreviewSize().height;

                        //orientation and aspect ratio
                        //preview aspect ration

                        mFrameSize[0] = mFrameWidth;
                        mFrameSize[1] = mFrameHeight;

                        //set surface texture
                        if(mSurfaceTexture != null){
                            mCamera.setPreviewTexture(mSurfaceTexture);

                        /*Finally we are ready to start the preview*/
                            Log.d("Camera", "startPreview");
                            mCamera.startPreview();
                        }
                    }

                }else
                    result = false;

            }catch(Exception e){
                result = false;
                e.printStackTrace();
            }
            return result;
        }
    }

    private void releaseCamera(){
        synchronized(this){

            if(mCamera != null){
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);

                mCamera.release();
                Log.d("Camera", "release camera");
            }

            mCamera = null;
            Log.d("Camera", "set camera null");
            mSurfaceWidth = 0;
            mSurfaceHeight = 0;

        }
    }



    public static class CameraSizeAccessor implements ListItemAccessor {

        public int getWidth(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.width;
        }

        public int getHeight(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.height;
        }
    }


    public interface ListItemAccessor {
        public int getWidth(Object obj);
        public int getHeight(Object obj);
    };

    protected Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {
        int calcWidth = 0;
        int calcHeight = 0;

        int maxAllowedWidth = (mMaxWidth != MAX_UNSPECIFIED && mMaxWidth < surfaceWidth) ? mMaxWidth : surfaceWidth;
        int maxAllowedHeight = (mMaxHeight != MAX_UNSPECIFIED && mMaxHeight < surfaceHeight) ? mMaxHeight : surfaceHeight;

        for (Object size : supportedSizes) {
            int width = accessor.getWidth(size);
            int height = accessor.getHeight(size);

            if (width <= maxAllowedWidth && height <= maxAllowedHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    calcWidth = (int) width;
                    calcHeight = (int) height;
                }
            }
        }

        return mCamera.new Size(calcWidth, calcHeight);
    }

}
