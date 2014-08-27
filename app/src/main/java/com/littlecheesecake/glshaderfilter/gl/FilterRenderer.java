package com.littlecheesecake.glshaderfilter.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by luyu on 18/8/14.
 */
public class FilterRenderer extends GLSurfaceView
        implements  GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{

    //shader program
    private Shader filterShader;

    //SurfaceTexture, related to camera
    private FilterCamera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private OESTexture mCameraTexture;
    private int mWidth, mHeight;
    private boolean updateTexture = false;

    //OpenGL params
    private ByteBuffer mFullQuadVertices;
    private float[] mTransformM = new float[16];


    public FilterRenderer(Context context) {
        super(context);

        init(context);
    }

    public FilterRenderer(Context context, AttributeSet attrs){
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        //init screen quad geometry data
        final byte FULL_QUAD_COORDS[] = { -1, 1, -1, -1, 1, 1, 1, -1};;
        mFullQuadVertices = ByteBuffer.allocateDirect(4 * 2);
        mFullQuadVertices.put(FULL_QUAD_COORDS).position(0);

        //set opengl es parameters
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //set up the shader
        filterShader = new Shader(context);
        mCameraTexture = new OESTexture();

        //set up camera
        mCamera = new FilterCamera();
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture){
        if(surfaceTexture != null) {
            updateTexture = true;
            requestRender();
        }
    }

    @Override
    public synchronized void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(updateTexture && mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTransformM);

            updateTexture = false;

            GLES20.glViewport(0, 0, mWidth, mHeight);

            filterShader.useProgram();

            //TODO: pass in some parameters from camera
            int uTransform = filterShader.getHandle("uTransformM");
            int uOrientation = filterShader.getHandle("uOrientationM");
            int uRatio = filterShader.getHandle("uRatio");

            GLES20.glUniformMatrix4fv(uTransform, 1, false, mTransformM, 0);
            GLES20.glUniformMatrix4fv(uOrientation, 1, false, mCamera.getOrientation(), 0);
            GLES20.glUniform2fv(uRatio, 1, mCamera.getAspectRatio(), 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCameraTexture.getTextureId());

            renderQuad(filterShader.getHandle("aPosition"));
        }
    }

    @Override
    public synchronized void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        //TODO: check if camera starts alr, if start, restart the camera with new w h
        startCameraRender();
    }

    @Override
    public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {

        try {
            filterShader.setProgramWithFilter(ShaderFilterType.FILTER_MANGA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * change the rendering parameters according to the camera
     * init SurfaceTexture especially
     */
    private synchronized  void startCameraRender() {
        //generate camera texture
        mCameraTexture.init();

        //set up surfacetexture------------------
        SurfaceTexture oldSurfaceTexture = mSurfaceTexture;
        mSurfaceTexture = new SurfaceTexture(mCameraTexture.getTextureId());
        mSurfaceTexture.setOnFrameAvailableListener(this);
        if(oldSurfaceTexture != null){
            oldSurfaceTexture.release();
        }

        mCamera.start(mSurfaceTexture, mWidth, mHeight);

        requestRender();
    }

    /**
     * change the shader filter type
     * @param type
     */
    public void setFilterShader(int type) {
        try {
            filterShader.changeProgramWithFilter(type);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * change the shader filter type with customer provided shader file
     */
    public void setCustomerFilterShader(int rawId) {
        try{
            filterShader.setProgramWithFilterRawId(rawId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the camera
     * @return FilterCamera
     */
    public FilterCamera getCamera() {
        return mCamera;
    }

    /**
     * when the surface destroys
     */
    public void onDestroy(){
        updateTexture = false;
        mSurfaceTexture.release();

        if(mCamera != null){
            mCamera.onPause();
        }

        mCamera = null;
    }

    private void renderQuad(int aPosition) {
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_BYTE, false, 0, mFullQuadVertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}