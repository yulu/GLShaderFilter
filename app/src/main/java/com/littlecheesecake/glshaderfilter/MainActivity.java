package com.littlecheesecake.glshaderfilter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.littlecheesecake.glshaderfilter.api.ShaderCamera;
import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;
import com.littlecheesecake.glshaderfilter.gl.FilterRenderer;


public class MainActivity extends Activity {
    private ShaderCamera mSC;
    private FilterRenderer mRenderer;

    //view
    private Button startButton;
    private Button stopButton;
    private Button setFilterButton;
    private Button flipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRenderer = (FilterRenderer)findViewById(R.id.cameraview);
        mSC = ShaderCamera.getInstance(mRenderer);
        mSC.setFilter(ShaderFilterType.FILTER_MANGA);

        startButton = (Button)findViewById(R.id.start_camera);
        stopButton = (Button)findViewById(R.id.stop_camera);
        flipButton = (Button)findViewById(R.id.flip_camera);
        setFilterButton = (Button)findViewById(R.id.set_filter_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.restartCamera();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.pauseCamera();
            }
        });

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.switchCamera();
            }
        });

        setFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_MANGA);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        mSC.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSC.restartCamera();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRenderer.onResume();
        mSC.registerCamera(mRenderer);
    }
}
