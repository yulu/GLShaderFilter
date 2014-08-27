package com.littlecheesecake.glshaderfilter.gl;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by luyu on 18/8/14.
 */
public class FilterRenderer extends GLSurfaceView
        implements  GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, DisplayParameter{
    private Context mContext;

    //shader program
    private Shader filterShader;
    private int mFilterType = 0;

    //SurfaceTexture, related to camera
    private SurfaceTexture mSurfaceTexture;
    private OESTexture mCameraTexture;
    private int mWidth, mHeight;
    private boolean updateTexture = false;

    //OpenGL params
    private ByteBuffer mFullQuadVertices;
    private float[] mRatio = new float[2];
    private float[] mRatioPreview = new float[2];
    private float mOrientationM[] = new float[16];
    private float[] mTransformM = new float[16];

    //listener
    private SurfaceChangedListener listener;


    public FilterRenderer(Context context) {
        super(context);

        init(context);
    }

    public FilterRenderer(Context context, AttributeSet attrs){
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

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
    }

    public interface SurfaceChangedListener {
        public void OnSurfaceChanged(SurfaceTexture surfaceTexture, int width, int height);
    }

    public void RegisterSurfaceChangedListener(SurfaceChangedListener l) {
        listener = l;
    }

    public SurfaceTexture getSurfaceTexture(){

        return mSurfaceTexture;
    }

    public int[] getSurfaceSize(){
        return new int[]{mWidth, mHeight};
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
        changeFilter();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(updateTexture && mSurfaceTexture != null) {
            updateRatioPreview();

            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTransformM);

            updateTexture = false;

            GLES20.glViewport(0, 0, mWidth, mHeight);

            filterShader.useProgram();

            int uTransform = filterShader.getHandle("uTransformM");
            int uOrientation = filterShader.getHandle("uOrientationM");
            int uRatio = filterShader.getHandle("uRatio");
            int uRatioPreview = filterShader.getHandle("uRatioPreview");


            GLES20.glUniformMatrix4fv(uTransform, 1, false, mTransformM, 0);
            GLES20.glUniformMatrix4fv(uOrientation, 1, false, mOrientationM, 0);
            GLES20.glUniform2fv(uRatio, 1, mRatio, 0);
            GLES20.glUniform2fv(uRatioPreview, 1, mRatioPreview, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCameraTexture.getTextureId());

            renderQuad(filterShader.getHandle("aPosition"));
        }
    }

    @Override
    public synchronized void onSurfaceChanged(GL10 gl, int width, int height) {

        mWidth = width;
        mHeight = height;

        //check orientation of the device
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Matrix.setRotateM(mOrientationM, 0, 90.0f, 0f, 0f, 1f);
        }
        else {
            Matrix.setRotateM(mOrientationM, 0, 00.0f, 0f, 0f, 1f);
        }

        //set ratio
        mRatio[0] = (float) Math.min(mWidth, mHeight) / mWidth;
        mRatio[1] = (float) Math.min(mWidth, mHeight) / mHeight;

        //set up surfacetexture------------------
        mCameraTexture.init();

        SurfaceTexture oldSurfaceTexture = mSurfaceTexture;
        mSurfaceTexture = new SurfaceTexture(mCameraTexture.getTextureId());
        mSurfaceTexture.setOnFrameAvailableListener(this);
        if(oldSurfaceTexture != null){
            oldSurfaceTexture.release();
        }

        //for camera, width is always larger than height;
        int surfaceWidth, surfaceHeight;
        if(width > height) {
            surfaceWidth = width;
            surfaceHeight = height;
        } else {
            surfaceWidth = height;
            surfaceHeight = width;
        }

        if(listener != null)
            listener.OnSurfaceChanged(mSurfaceTexture, surfaceWidth, surfaceHeight);
    }

    @Override
    public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {

        /*try {
            filterShader.setProgramWithFilter(ShaderFilterType.FILTER_NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private synchronized void updateRatioPreview(){
        int frameWidth, frameHeight;

        frameWidth = mFrameSize[0];
        frameHeight = mFrameSize[1];

        //check the orientation for correct w/h ratio of camera frame
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRatioPreview[1] = (float) Math.min(frameWidth, frameHeight) / frameWidth;
            mRatioPreview[0] = (float) Math.min(frameWidth, frameHeight) / frameHeight;
        }
        else {
            mRatioPreview[0] = (float) Math.min(frameWidth, frameHeight) / frameWidth;
            mRatioPreview[1] = (float) Math.min(frameWidth, frameHeight) / frameHeight;
        }

    }


    /**
     * change the shader filter type
     * @param type
     */
    public void setFilterShader(int type) {
         mFilterType = type;
    }

    private void changeFilter() {
        try {
            filterShader.changeProgramWithFilter(mFilterType);
        } catch (Exception e) {
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
     * when the surface destroys
     */
    public void onDestroy(){
        updateTexture = false;
        mSurfaceTexture.release();

    }


    private void renderQuad(int aPosition) {
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_BYTE, false, 0, mFullQuadVertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}