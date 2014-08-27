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
    private FilterCamera mCamera;

    public static ShaderCamera getInstance(FilterRenderer r) {
        if (mSC == null)
            mSC = new ShaderCamera(r);

        return mSC;
    }

    private ShaderCamera(FilterRenderer r) {
        mFilterRenderer = r;
        mCamera = r.getCamera();
    }

    public void openCamera(int which) {
        //mFilterRenderer.startCameraRender();
    }

    public void shutdownCamera() {

    }

    public void setFilter(int filterType) {
        mFilterRenderer.setFilterShader(filterType);
    }

    public void setCustomerFilter(int rawId) {
        mFilterRenderer.setCustomerFilterShader(rawId);
    }

    public void switchCamera() {

        //TODO: another thread!!
        if (mCamera != null ) {
            mCamera.flipit();
        }
    }

    public void takePicture() {

    }
}
