package com.littlecheesecake.glshaderfilter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;

import com.littlecheesecake.glshaderfilter.api.ShaderCamera;
import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;
import com.littlecheesecake.glshaderfilter.gl.FilterRenderer;


public class MainActivity extends Activity {
    private ShaderCamera mSC;
    private FilterRenderer mRenderer;

    //view
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRenderer = (FilterRenderer)findViewById(R.id.cameraview);
        mSC = ShaderCamera.getInstance(mRenderer);

        button1 = (Button)findViewById(R.id.filter_1);
        button2 = (Button)findViewById(R.id.filter_2);
        button3 = (Button)findViewById(R.id.filter_3);
        button4 = (Button)findViewById(R.id.filter_4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_NONE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.switchCamera();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.pauseCamera();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.restartCamera(mRenderer);
            }
        });
    }


    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        mSC.pauseCamera();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
        mSC.restartCamera(mRenderer);
    }

    @Override
    public void onStart() {
        super.onStart();
        mSC.registerCamera(mRenderer);
    }


}
