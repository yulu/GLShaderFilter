package com.littlecheesecake.glshaderfilter.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.littlecheesecake.glshaderfilter.R;
import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by yu lu on 18/8/14.
 */
public class Shader {
    //Context
    private Context mContext;

    //shader program handles
    private int mProgram = 0;

    //shader handles
    private int mShaderVertex = 0;
    private int mShaderFragment = 0;

    //shader source
    private String vertexSource;
    private String fragmentSource;
    private String filterSource;

    //shader filter type
    private int filterType = -1;

    private final HashMap<String, Integer> mShaderHandleMap = new HashMap<String, Integer>();

    /**
     * Constructor
     * @param context
     */
    public Shader(Context context){
        mContext = context;
    }

    /**
     * set the program without filter, by just indicate the vertex and fragment shader raw file
     * @param vertexShaderRawId
     * @param fragmentShaderRawId
     * @throws Exception
     */
    public void setProgram(int vertexShaderRawId, int fragmentShaderRawId)
                throws Exception {
        vertexSource = loadRawString(vertexShaderRawId);
        fragmentSource = loadRawString(fragmentShaderRawId);
        setProgramFromString();
    }

    /**
     * change the program if the filter is changed, or the program is not init
     * by indicate the filter type
     * @param filterType
     * @throws Exception
     */
    public void changeProgramWithFilter(int filterType)
            throws Exception {
        if(this.filterType == filterType && mProgram != 0)
            return;
        else{
            this.filterType = filterType;
            setProgramWithFilter(filterType);
        }
    }

    /**
     * set the program with filter, by just indicate the filter type
     * all the shader file are loaded accordingly
     * @param filterType
     * @throws Exception
     */
    public void setProgramWithFilter(int filterType)
                    throws Exception {

        //load the filer with indicated filter type
        int filterShaderRawId = R.raw.none;
        switch(filterType){
            case ShaderFilterType.FILTER_SILENCE:
                filterShaderRawId = R.raw.silence;
                break;
            case ShaderFilterType.FILTER_MENTAL:
                filterShaderRawId = R.raw.metal;
                break;
            case ShaderFilterType.FILTER_SUN:
                filterShaderRawId = R.raw.sun;
                break;
            case ShaderFilterType.FILTER_ICE:
                filterShaderRawId = R.raw.ice;
                break;
            case ShaderFilterType.FILTER_LIVE:
                filterShaderRawId = R.raw.live;
                break;
            case ShaderFilterType.FILTER_DREAMY:
                filterShaderRawId = R.raw.dreamy;
                break;
            case ShaderFilterType.FILTER_CHOCOLATE:
                filterShaderRawId = R.raw.chocolate;
                break;
            case ShaderFilterType.FILTER_FIREWORKS:
                filterShaderRawId = R.raw.fireworks;
                break;
            case ShaderFilterType.FILTER_OLDTIME:
                filterShaderRawId = R.raw.oldtime;
                break;
            case ShaderFilterType.FILTER_MAY:
                filterShaderRawId = R.raw.may;
                break;


            default:
                filterShaderRawId = R.raw.none;
                break;

        }

        vertexSource = loadRawString(R.raw.vertex_shader);
        fragmentSource = loadRawString(R.raw.fragment_shader);
        filterSource = loadRawString(filterShaderRawId);

        fragmentSource = fragmentSource.replace("__FILTER__", filterSource);

        setProgramFromString();

    }

    /**
     * allow for customized filter raw file to be loaded
     * @param filterRawId
     * @throws Exception
     */
    public void setProgramWithFilterRawId(int filterRawId)
                throws Exception {
        vertexSource = loadRawString(R.raw.vertex_shader);
        fragmentSource = loadRawString(R.raw.fragment_shader);
        filterSource = loadRawString(filterRawId);

        fragmentSource.replace("__FILTER__", filterSource);

        setProgramFromString();
    }


    /**
     * Compiles vertex and fragment shaders and link
     * them to a shader program that can be used in OpenGL ES
     *
     * @throws Exception
     */
    private void setProgramFromString()
                    throws Exception {

        mShaderVertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        mShaderFragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        int program = GLES20.glCreateProgram();
        if(program != 0) {
            GLES20.glAttachShader(program, mShaderVertex);
            GLES20.glAttachShader(program, mShaderFragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if(linkStatus[0] != GLES20.GL_TRUE) {
                String error = GLES20.glGetProgramInfoLog(program);;
                deleteProgram();
                throw new Exception(error);
            }
        }

        mProgram = program;
        mShaderHandleMap.clear();
    }

    /**
     * Activates this shader program
     */
    public void useProgram() {
        GLES20.glUseProgram(mProgram);
    }

    /**
     * Deletes program and shaders associated with it
     */
    public void deleteProgram() {
        GLES20.glDeleteShader(mShaderVertex);
        GLES20.glDeleteShader(mShaderFragment);
        GLES20.glDeleteProgram(mProgram);
        mProgram = mShaderVertex = mShaderFragment = 0;
    }

    public int getProgramHandle() {
        return mProgram;
    }

    public int getFilterType() {
        return filterType;
    }

    /**
     * Get id for given handle name
     * @param name
     * @return
     */
    public int getHandle(String name) {
        if(mShaderHandleMap.containsKey(name)) {
            return mShaderHandleMap.get(name);
        }

        int handle = GLES20.glGetAttribLocation(mProgram, name);
        if( handle == -1) {
            handle = GLES20.glGetUniformLocation(mProgram, name);
        }
        if(handle == -1) {
            Log.d("GLSL shader", "Could not get attrib location for " + name);
        }else {
            mShaderHandleMap.put(name, handle);
        }

        return handle;
    }

    /**
     * Get ids for an array of handle names
     * @param names
     * @return
     */
    public int[] getHandles(String... names) {
        int[] res = new int[names.length];
        for (int i = 0; i < names.length; ++i) {
            res[i] = getHandle(names[i]);
        }

        return res;
    }

    private int loadShader(int shaderType, String source) throws Exception {
        int shader = GLES20.glCreateShader(shaderType);
        if(shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

            if (compiled[0] == 0) {
                String error = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                throw new Exception(error);
            }
        }

        return shader;
    }

    private String loadRawString(int rawId) throws Exception{
        InputStream is = mContext.getResources().openRawResource(rawId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        int len;
        while((len = is.read(buf))!= -1) {
            baos.write(buf, 0, len);
        }

        return baos.toString();
    }
}
