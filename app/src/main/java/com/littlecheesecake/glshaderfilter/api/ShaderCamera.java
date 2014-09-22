package com.littlecheesecake.glshaderfilter.api;

import com.littlecheesecake.glshaderfilter.gl.FilterCamera;
import com.littlecheesecake.glshaderfilter.gl.FilterRenderer;

/**
 * api functions for shader camera
 * Created by luyu on 18/8/14.
 */
public class ShaderCamera{
    private static ShaderCamera mSC;
    private FilterRenderer mFilterRenderer;
    private FilterCamera mFilterCamera;

    public static ShaderCamera getInstance(FilterRenderer r) {
        if (mSC == null)
            mSC = new ShaderCamera(r);

        return mSC;
    }

    private ShaderCamera(FilterRenderer r) {
        mFilterRenderer = r;
    }

    /**
     * Register camera with the renderer surface, this method needs to be called in OnStart()
     * @param renderer
     */
    public void registerCamera(FilterRenderer renderer) {
        if(mFilterCamera == null)
            mFilterCamera = new FilterCamera();

        mFilterRenderer = renderer;
        mFilterRenderer.RegisterSurfaceChangedListener(mFilterCamera);
    }


    /**
     * Start or restart the camera, this method needs to be called in OnResume()
     * or whenever the you want to restart a stopped camera view when the renderer surface is not destroyed
     */
    public void restartCamera(FilterRenderer renderer) {
        mFilterRenderer = renderer;
        mFilterCamera.onResume(mFilterRenderer.getSurfaceTexture(), mFilterRenderer.getSurfaceSize());

    }

    /**
     * Stop the camera and pause the renderer, this method needs to be called in OnPause()
     */
    public void stopCamera() {
        mFilterCamera.onPause();
        mFilterRenderer.onPause();
    }

    /**
     * Stop the camera, this method is to be called whenever you want to stop the camera, but keep the renderer
      */
    public void pauseCamera() {
        mFilterCamera.onPause();
    }

    /**
     * Switch between back and front camera
     */
    public void switchCamera() {
        //do it in another thread
        if (mFilterCamera != null ) {
            Thread t = new Thread() {
                public void run() {
                    mFilterCamera.flipit();
                }
            };

            t.start();
        }
    }

    public void setFilter(int filterType) {
        mFilterRenderer.setFilterShader(filterType);
    }

    public void setCustomerFilter(int rawId) {
        mFilterRenderer.setCustomerFilterShader(rawId);
    }

    public void takePicture() {}


}
