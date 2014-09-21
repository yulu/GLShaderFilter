package com.littlecheesecake.glshaderfilter.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * This class defines the OES Texture that can be attached to SurfaceTexture
 * which is updated to the most recent camera frame image when requested.
 * Created by luyu on 18/8/14.
 *
 */
public class Texture {
    //private int mTextureHandle;
    private int[] mTextureHandlesOES;
    private int[] mTextureHandles;

    public Texture() {

    }

    public int getTextureIdOES(int id){
        return mTextureHandlesOES[id];
    }

    public int getTextureId(int id){
        return mTextureHandles[id];
    }

    //generate external texture to hold camera frame
    public void initCameraFrame(int id){
        mTextureHandlesOES = new int[1];

        GLES20.glGenTextures(1, mTextureHandlesOES, 0);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureHandlesOES[id]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

    }

    public void loadTextures(Context context, int num, int[] resourceIds) {
        //generate texture handles
        mTextureHandles= new int[num];
        GLES20.glGenTextures(num, mTextureHandles, 0);

        //for each handle, bind the image from resource
        for (int i = 0; i < num; i++) {
            int texture = mTextureHandles[i];
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceIds[i], options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();

            if (texture == 0) {
                throw new RuntimeException("Error loading texture.");
            }
        }
    }

}
